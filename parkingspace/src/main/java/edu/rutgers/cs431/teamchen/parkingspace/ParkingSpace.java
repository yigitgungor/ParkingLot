package edu.rutgers.cs431.teamchen.parkingspace;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterResponse;
import edu.rutgers.cs431.teamchen.util.GateAddressBook;
import edu.rutgers.cs431.teamchen.util.SyncClock;
import edu.rutgers.cs431.teamchen.util.SystemConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ParkingSpace implements Runnable {
    private static final Logger logger = Logger.getLogger("parking-space");
    private final int httpPort;
    private final URL monitorAddr;
    private final GateAddressBook gateAddressBook = new GateAddressBook();
    private final PriorityQueue<CarWithToken> parkedQ = new PriorityQueue<>(0, new CWTComparator());
    private SyncClock clock;
    private HttpServer httpServer;
    private Lock parkedQLock = new ReentrantLock();
    private Condition notEmpty = parkedQLock.newCondition();


    public ParkingSpace(String monitorAddress, int httpPort) throws MalformedURLException {
        this.httpPort = httpPort;
        this.monitorAddr = new URL(monitorAddress);
    }

    private static void reportError(String mess) {
        logger.warning(mess);
    }

    public void onCarEntering(CarWithToken cwt) {
        logger.info("Incoming car with token " + cwt.token);
        this.letCarPark(cwt);
    }

    private void letCarPark(CarWithToken cwt) {
        parkedQLock.lock();
        this.parkedQ.add(cwt);
        notEmpty.signal();
        parkedQLock.unlock();
    }

    // sends the car to a random gate
    private void onCarDepart(CarWithToken cwt) {
        ArrayList<URL> gates = this.gateAddressBook.getAddresses();
        // Pick a random gate
        Random r = new Random(this.clock.getTime());
        URL gate = gates.get(r.nextInt(gates.size()));
        try {
            HttpURLConnection conn = (HttpURLConnection) gate.openConnection();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            Gson gson = new Gson();
            gson.toJson(cwt, writer);
            writer.flush();
            writer.close();
            conn.disconnect();
        } catch (IOException e) {
            reportError("can't send car through gate " + gate.toString());
            reportError("retry"); // because I don't wanna lose any tokens!
            onCarDepart(cwt);
            return;
        }

        logger.info("Car leaving through gate +" + gate.toString() + "+ with token " + cwt.token + "");
    }

    // gets the earliest car out of the queue to look down on...
    private CarWithToken earliestCarToLeave() {
        try {
            parkedQLock.lock();
            while (parkedQ.size() == 0) {
                notEmpty.await();
            }
            CarWithToken early = this.parkedQ.remove();
            return early;
        } catch (InterruptedException e) {
            // TODO: Do what?
        } finally {
            parkedQLock.unlock();
        }
        return null;
    }

    // waits for this car til it departs
    private void activelyWaitThenDepart(CarWithToken cwt) {
        while (this.clock.getTime() < cwt.departureTimestamp) {
            continue;
        }
        // allows car to leave
        new Thread(() -> onCarDepart(cwt)).start();
    }

    private void registersWithMonitor() throws IOException {
        ParkingSpaceRegisterRequest req = new ParkingSpaceRegisterRequest();
        req.hostname = InetAddress.getLocalHost().getHostName();
        req.httpPort = this.httpPort;


        URL url = null;
        try {
            url = new URL(this.monitorAddr, SystemConfig.MONITOR_PARKING_SPACE_REGISTER_PATH);
        } catch (MalformedURLException e) {
            reportError("registersWithMonitor: malformed url: " + this.monitorAddr.toString() + ": " + e.getMessage());
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        Gson gson = new Gson();
        gson.toJson(req, writer);
        writer.flush();
        writer.close();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            reportError("Something went wrong, can't register the parking space to the monitor");
            throw new IOException("can't register the Parking Space!");
        }
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        ParkingSpaceRegisterResponse resp = gson.fromJson(reader, ParkingSpaceRegisterResponse.class);
        reader.close();
        conn.disconnect();

        this.clock = new SyncClock(resp.trafGenAddr, resp.trafGenPort);
    }

    public void http() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress("localhost", this.httpPort), SystemConfig
                    .MAXIMUM_HTTP_CONNECTIONS);
        } catch (IOException e) {
            reportError("Unable to create the http service for parking space: " + e.getMessage());
            System.exit(1);
        }
        httpServer.createContext(SystemConfig.PARKING_SPACE_CAR_ENTERING_PATH, new CarEnteringHttpHandler(this));
        httpServer.createContext(SystemConfig.PARKING_SPACE_PEER_ADDRESS_CHANGE_PATH, this.gateAddressBook);
        httpServer.start();
    }

    public void run() {
        this.http();
        logger.info("HTTP Service is up at " + this.httpServer.getAddress().toString());
        try {
            this.registersWithMonitor();
            logger.info("Parking Space Registered.");
        } catch (IOException e) {
            reportError(e.getMessage());
            System.exit(1);
        }
        while (true) {
            CarWithToken cwt = this.earliestCarToLeave();
            activelyWaitThenDepart(cwt);
        }
    }

    private class CWTComparator implements Comparator<CarWithToken> {
        @Override
        public int compare(CarWithToken a, CarWithToken b) {
            if (a.departureTimestamp < b.departureTimestamp) {
                return -1;
            } else if (a.departureTimestamp == b.departureTimestamp) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public boolean equals(Object a) {
            return false;
        }
    }


}
