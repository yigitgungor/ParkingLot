package edu.rutgers.cs431.teamchen.parkingspace;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterResponse;
import edu.rutgers.cs431.teamchen.util.DataFormatter;
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

public class ParkingSpace implements Runnable {
    private final int httpPort;
    private final URL monitorAddr;
    private final GateAddressBook gateAddressBook = new GateAddressBook();
    private final PriorityQueue<CarWithToken> parkedQ = new PriorityQueue<>(new CWTComparator());
    private SyncClock clock;
    private HttpServer httpServer;
    private Lock parkedQLock = new ReentrantLock();
    private Condition notEmpty = parkedQLock.newCondition();

    public ParkingSpace(String monitorAddress, int httpPort, String trafGenAddr, int trafGenPort) throws IOException {
        this.httpPort = httpPort;
        this.monitorAddr = new URL(monitorAddress);
        this.clock = new SyncClock(trafGenAddr, trafGenPort);
    }

    private static void reportError(String msg) {
        System.out.println("WARNING: " + msg);
    }

    private static void log(String msg) {
        System.out.println("INFO: " + msg);
    }

    public void onCarEntering(CarWithToken cwt) {
        log("(Gate->ParkingSpace) " + DataFormatter.format(cwt));
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
            Gson gson = new Gson();
            HttpURLConnection conn = (HttpURLConnection) new URL(gate, SystemConfig.GATE_CAR_LEAVING_PATH)
                    .openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            gson.toJson(cwt, writer);
            writer.flush();
            writer.close();

            if (conn.getResponseCode() != 200) {
                reportError("can't send car back to gate: status not OK");
            }

            log("(ParkingSpace->Gate) " + DataFormatter.format(cwt));
            conn.disconnect();
        } catch (IOException e) {
            reportError("can't send car through gate " + gate.toString() + " " + e.getMessage());
        }
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
        log("HTTP Service is up at " + this.httpServer.getAddress().toString());
        try {
            this.registersWithMonitor();
            log("Parking Space Registered.");
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
