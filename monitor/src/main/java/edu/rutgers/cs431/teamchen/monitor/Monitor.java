package edu.rutgers.cs431.teamchen.monitor;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import edu.rutgers.cs431.TrafficGeneratorProto;
import edu.rutgers.cs431.teamchen.proto.*;
import edu.rutgers.cs431.teamchen.util.SyncClock;
import edu.rutgers.cs431.teamchen.util.SystemConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Logger;

public class Monitor implements Runnable {

	private static final int DEFAULT_MAX_GATE = 6;
	private static final int DEFAULT_MAX_PARKING_CAPACITY = 200;

	private static final long STATS_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;


	private static final String DEFAULT_HOSTNAME = "localhost";
	private static final Logger logger = Logger.getLogger("Gate");
	// the list of gate in the system
	private final List<GateInfo> gates;
	private final Lock gatesLock = new ReentrantLock();
	// the monitor's http address
	private final String monitorHttpAddr;
	private final int httpPort;
	private final int tcpPort = SystemConfig.MONITOR_ROSTER_PROTOBUF_SERVICE_PORT;
	private final int strategy;
	private final TokenReservoir.Basic tokenReservoir;
	private final int maxGate;
	private final long maxParkingCapacity;
	private final int trafGenPort = SystemConfig.TRAFFIC_GENERATOR_CHRONOS_SERVICE_PORT;
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private SyncClock clock;
	private volatile String parkingSpaceHttpAddr;
	private volatile String trafGenAddr;
	private HttpServer httpServ;

	public Monitor(int httpPort, int strategy, int maxGate, long maxParkingCapacity) throws UnknownHostException {
		this.gates = Collections.synchronizedList(new ArrayList<>());
		this.httpPort = httpPort;
		this.monitorHttpAddr = "http://" + InetAddress.getLocalHost().getHostName() + ":" + Integer.toString(httpPort);
		this.strategy = strategy;
		this.maxGate = maxGate;
		this.maxParkingCapacity = maxParkingCapacity;
		this.tokenReservoir = new TokenReservoir.Basic(this.maxParkingCapacity, this.maxGate);
	}
	public Monitor(int httpPort, int strategy) throws UnknownHostException {
		this(httpPort, strategy, DEFAULT_MAX_GATE, DEFAULT_MAX_PARKING_CAPACITY);
	}

	private static void reportError(String msg) {
		logger.warning(msg);
	}

	private static void sendAddrChangeToGate(GateHttpAddressesChangeRequest req, int gateIndex) {
		URL gateUrl = null;
		try {
			gateUrl = new URL(new URL(req.gateHttpAddrs.get(gateIndex)), SystemConfig
					.GATE_PEER_ADDRESS_CHANGE_PATH);
			HttpURLConnection conn = (HttpURLConnection) gateUrl.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			Gson gson = new Gson();
			gson.toJson(req, writer);
			writer.flush();
			writer.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			reportError("sendAddrChangeToGate: invalid gate URL? How does this happen? " + e.getMessage());
		} catch (IOException e) {
			reportError("problem sending peer update request to gate " + gateUrl.toString() + ": " + e
					.getMessage());
		}
	}

	private static void sendAddrChangeToParkingSpace(GateHttpAddressesChangeRequest req, String parkingSpaceHttpAddr) {
		URL gateUrl = null;
		try {
			gateUrl = new URL(new URL(parkingSpaceHttpAddr), SystemConfig.PARKING_SPACE_PEER_ADDRESS_CHANGE_PATH);
			HttpURLConnection conn = (HttpURLConnection) gateUrl.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			Gson gson = new Gson();
			gson.toJson(req, writer);
			writer.flush();
			writer.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			reportError("sendAddrChangeToGate: invalid parking space URL? How does this happen");
		} catch (IOException e) {
			reportError("problem sending peer update request to the parking space " + gateUrl.toString() +
					": " + e
					.getMessage());
		}
	}

	// using the address in GateInfo updates this gate
	private static void updateStatsFromGateAt(GateInfo info) {
		URL gateUrl = null;
		try {
			gateUrl = new URL(new URL(info.httpAddress), SystemConfig.GATE_GET_STATS_PATH);
			HttpURLConnection conn = (HttpURLConnection) gateUrl.openConnection();

			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			Gson gson = new Gson();
			GateStatResponse gsr = gson.fromJson(in, GateStatResponse.class);
			in.close();
			conn.disconnect();
			info.totalWaitingTime = gsr.totalWaitingTime;
			info.totalCarsProcessed = gsr.totalCarsProcessed;
		} catch (MalformedURLException ex) {
			reportError("updateStatesFromGateAt " + info.httpAddress + ": invalid url? " + ex.getMessage());
			return;
		} catch (IOException ex) {
			reportError("problem sending update stats request to gate at " + info.httpAddress + " " + ex
					.getMessage());
			return;
		}
	}

	public int getTrafGenPort() {
		return trafGenPort;
	}


	
	public String getTrafGenAddr() {
		return trafGenAddr;
	}

	// sends to gates that need to have the address change and notifies the parking space
	// For a gate ring, the address change includes the first & the last gate in the list
	public void onGateListAppended() {
		gatesLock.lock();
		// update the gates in the ring
		if (gates.size() > 1) {
			if (gates.size() == 2) {
				// send update to two gate
				new Thread(() -> sendAddrChangeToGate(newGateAddrChangeReqFor(0), 0)).start();
				new Thread(() -> sendAddrChangeToGate(newGateAddrChangeReqFor(1), 1)).start();
			} else {
				new Thread(() -> sendAddrChangeToGate(newGateAddrChangeReqFor(gates.size() - 1), gates.size() - 1)).start();
				new Thread(() -> sendAddrChangeToGate(newGateAddrChangeReqFor(gates.size() - 2), gates.size() - 2)).start();
				new Thread(() -> sendAddrChangeToGate(newGateAddrChangeReqFor(0), 0)).start();
			}
		}

		// tell the parking space to update the gate list
		final ArrayList<String> gateAddrs = new ArrayList<>();
		for (GateInfo gi : gates) {
			gateAddrs.add(gi.httpAddress);
		}
		new Thread(() -> sendAddrChangeToParkingSpace(new GateHttpAddressesChangeRequest(gateAddrs), this.parkingSpaceHttpAddr)).start();
		gatesLock.unlock();

	}

	/**
	 * make a GateHttpAddressesChangeRequest for the gate at @param index. The addresses to change to
	 * are the adjacent left and right gates
	 * IMPORTANT: Assuming the gates list lock is acquired
	 **/
	private GateHttpAddressesChangeRequest newGateAddrChangeReqFor(int index) {
		final int len = this.gates.size();
		if (len == 0) {
			throw new RuntimeException("Expect a non-empty gate list");
		}

		Function<Integer, Boolean> inRange = (in) -> in >= 0 && in < len;
		if (!inRange.apply(index)) {
			throw new RuntimeException("Expect an address in range");
		}
		ArrayList<String> addrs = new ArrayList<>();

		int left, right;
		if (index == 0) {
			left = len - 1;
			right = index + 1;
		} else if (index == len - 1) {
			left = index - 1;
			right = 0;
		} else {
			left = index - 1;
			right = index + 1;
		}

		if (left == right) {
			if (inRange.apply(left)) {
				addrs.add(this.gates.get(left).httpAddress);
			}
		} else {
			if (inRange.apply(left)) {
				addrs.add(this.gates.get(left).httpAddress);
			}
			if (inRange.apply(right)) {
				addrs.add(this.gates.get(right).httpAddress);
			}
		}

		GateHttpAddressesChangeRequest nq = new GateHttpAddressesChangeRequest(addrs);
		return nq;
	}

	public void updateStatsFromGates() {
		gatesLock.lock();
		for (GateInfo gateInfo : this.gates) {
			final GateInfo gi = gateInfo;
			new Thread(() -> updateStatsFromGateAt(gi)).start();
		}
		gatesLock.unlock();
		
		System.out.println("\n");
		System.out.println("Monitor Update");
		System.out.println("_______________________________________________________");
		System.out.println("\n");
		for(GateInfo gate : this.gates)
		{
			 System.out.println("--Gate Update--");
			 System.out.println("\nGate:\n"+ gate.addr+
					 			"\nWait time: "+gate.totalWaitingTime+
					 			"\nCars processed: "+gate.totalCarsProcessed);

			 System.out.println("____________________\n");
		}
		System.out.println("\n");
		System.out.println("\n");


	}

	public boolean ableToStart() {
		return this.parkingSpaceHttpAddr != null && this.trafGenAddr != null;
	}

	// returns null if the gate can't start
	public GateRegisterResponse onGateRegister(GateRegisterRequest grr) {
		if (!this.ableToStart()) {
			logger.info("can't register gate " + grr.hostname + ":" + grr.tcpPort + " : didn't have a " +
					"parking " +
					"space and a traffic generator info");
			return null;
		}

		GateInfo gi = new GateInfo();
		gi.httpAddress = "http://" + grr.hostname + ":" + Integer.toString(grr.httpPort);
		gi.addr = TrafficGeneratorProto.GateAddress.newBuilder()
				.setHostname(grr.hostname)
				.setPort(grr.tcpPort).build();
		gi.totalCarsProcessed = 0;
		gi.totalWaitingTime = 0;

		gatesLock.lock();
		gates.add(gi);

		GateRegisterResponse resp = new GateRegisterResponse();
		resp.parkingSpaceHttpUrl = this.parkingSpaceHttpAddr;
		resp.strategy = this.strategy;
		resp.trafficGeneratorAddr = this.trafGenAddr;
		resp.trafficGeneratorPort = this.trafGenPort;
		try {
			resp.tokens = tokenReservoir.next();
		} catch (Exception e) {
			logger.warning("can't get the next list of token: " + e.getMessage());
			gates.remove(gi);
			return null;
		} finally {
			gatesLock.unlock();
		}
		new Thread(() -> onGateListAppended()).start();
		return resp;
	}

	public void onParkingSpaceRegister(ParkingSpaceRegisterRequest req) {
		this.parkingSpaceHttpAddr = "http://" + req.hostname + ":" + Integer.toString(req.httpPort);
		logger.info("A Parking Space registered at " + this.parkingSpaceHttpAddr);
	}

	private void http() {
		httpServ = null;
		try {
			httpServ = HttpServer.create();
			httpServ.bind(new InetSocketAddress("localhost", this.httpPort), SystemConfig
					.MAXIMUM_HTTP_CONNECTIONS);
		} catch (IOException e) {
			logger.warning("unable to set up an http server: " + e.getMessage());
		}
		httpServ.createContext(SystemConfig.MONITOR_GATE_REGISTER_PATH, new GateRegisterHttpHandler(this));
		httpServ.createContext(SystemConfig.MONITOR_PARKING_SPACE_REGISTER_PATH, new ParkingSpaceRegisterHttpHandler(this));
		httpServ.start();
	}

	private void rosterRequestStreamHandler(Socket socket) throws IOException {
		this.trafGenAddr = socket.getInetAddress().getHostName();
		this.clock = new SyncClock(this.trafGenAddr, this.trafGenPort);

		try {
			while (true) {
				TrafficGeneratorProto.GateAddressListRequest galr = TrafficGeneratorProto.GateAddressListRequest
						.parseDelimitedFrom(socket.getInputStream());
				ArrayList<TrafficGeneratorProto.GateAddress> al = new ArrayList<>();
				this.gatesLock.lock();
				for (GateInfo gi : this.gates) {
					al.add(gi.addr);
				}
				this.gatesLock.unlock();
				TrafficGeneratorProto.GateAddressListResponse.newBuilder().addAllGateAddress(al)
						.build().writeDelimitedTo(socket.getOutputStream());
			}
		} catch (IOException e) {
			reportError("problem with the traffic generator's roster request stream: " + e
					.getMessage
							());
			return;
		}
	}

	// runs forever to accept as many as traffic generators as possible
	private void listensTCPForTrafGen() {
		try {
			ServerSocket serv = new ServerSocket();
			serv.bind(new InetSocketAddress("localhost", this.tcpPort));
			logger.info("Accepting Traffic Generator connections at " + serv.getLocalSocketAddress());
			while (true) {
				final Socket socket = serv.accept();
				logger.info("Accepted a traffic generator @" + socket.getLocalSocketAddress()
						.toString());
				rosterRequestStreamHandler(socket);
			}
		} catch (IOException e) {
			reportError("unable to set up TCP server socket: " + e.getMessage());
			System.exit(1);
		}
	}

	private void scheduleStatsUpdate() {
		executor.scheduleWithFixedDelay(() -> updateStatsFromGates(),
				0, STATS_UPDATE_INTERVAL_IN_MILLISECONDS,
				TimeUnit.MILLISECONDS);
	}

	public void run() {
		this.http();
		logger.info("HTTP Service is up at " + httpServ.getAddress().toString());
		this.scheduleStatsUpdate();
		logger.info("Periodically update gate's status.");
		this.listensTCPForTrafGen();
	}

}
