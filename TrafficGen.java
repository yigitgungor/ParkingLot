import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;
// import edu.rutgers.cs431.TrafficGeneratorProto.Car;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeResponse;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListResponse;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListRequest;

// Between the monitor and Traffic Generator
// ASK about the start, should the traffic generator start with an empty List of GateAddresses

// for listening to a TimeRequest should this be done in a separate thread / instance?

// How long should the simulation be?
// If we consider 1 second = 500 seconds?

// Gates = 6
// Max number of Cars allowed = 200
// Time to travel from gate to Parking Space = 60 seconds
// Frequency of arrivals of cars = 5 per second
// Staying time for cars = 2 hours

public class TrafficGen {
    public final static double FREQ = 5.0;
    // long lastCarTime;



    public static int getPoisson(double lambda) {
      double L = Math.exp(-lambda);
      double p = 1.0;
      int k = 0;

      do {
        k++;
        p *= Math.random();
      } while (p > L);

      return k - 1;
    }

    public static int getRandomGate(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }


    public static void main(String[] args) throws IOException {
        InetAddress lh = InetAddress.getByName("localhost");
        RosterSync rosterSync = new RosterSync(lh, 6666, false);
    // public TrafficGen() {
        int stayingTime = 1000 * 60 * 60 * 2;


        // List<GateAddress> gateList = rosterSync.gateAddressList;
        MyClock timer = new MyClock();
        // MyGateList gateAdrList = new MyGateList();
        ScheduledExecutorService carScheduler = Executors.newScheduledThreadPool(5);
        // long inTime = System.currentTimeMillis();
//        Callable<Car> generateCars = new Callable<Car>() {
        Callable<Void> generateCars = new Callable<Void>() {
            public Void call() throws IOException {
                // long inTime = System.currentTimeMillis();
                timer.setTime(System.currentTimeMillis());
                long inTime = timer.getTime();
                long outTime = inTime + stayingTime;
                Car car = Car.newBuilder()
                    .setArrivalTimestamp(inTime)
                    .setDepartureTimestamp(outTime)
                    .build();
//                System.out.println(car.getArrivalTimestamp());
                // List<GateAddress> gateList = rosterSync.getGateAddr();
                List<GateAddress> gateAddressList = rosterSync.getGateAddr();
                // List<GateAddress> gateAddressList = gateAdrList.getAdrList();
                // GateAddress gate = gateAdrList.getAdrList().get(getRandomGate(0, 2));
                GateAddress gate = gateAddressList.get(getRandomGate(0, gateAddressList.size() - 1));
                String hostName = gate.getHostname();
                int port = gate.getPort();
                System.out.println(port);
                // int port = 2020;
                Socket socket = null;
                try {
                    InetAddress lh = InetAddress.getByName(hostName);
                    socket = new Socket(lh, port);
                    long start = System.currentTimeMillis();
                    car.writeDelimitedTo(socket.getOutputStream());

                }
                finally {
                    socket.close();
                }
                carScheduler.schedule(this, getPoisson(FREQ), TimeUnit.SECONDS);
//                return car;
                return null;
            }
        };

        Callable<Void> listenToTime = new Callable<Void>() {
            public Void call() throws IOException {
                Socket connectionSocket = null;
                ServerSocket listeningSocket = new ServerSocket(5555);
                while(true) {
                    connectionSocket = listeningSocket.accept();
                    TimeRequest tr = TimeRequest.parseDelimitedFrom(connectionSocket.getInputStream());
                    long currentTimeStamp = timer.getTime();
                    TimeResponse timeResponse = TimeResponse.newBuilder()
                        .setCurrentTimestamp(currentTimeStamp)
                        .build();
                    timeResponse.writeDelimitedTo(connectionSocket.getOutputStream());
                }
            }
        };

        // Callable<String> getGateList = new Callable<String>() {
        //     public String call() throws IOException {
        //         Socket socket = null;
        //         try {
        //             InetAddress lh = InetAddress.getByName("localhost");
        //             socket = new Socket(lh, 6666);
        //             GateAddressListRequest gateAdrLR = GateAddressListRequest.getDefaultInstance();
        //             gateAdrLR.writeDelimitedTo(socket.getOutputStream());

        //             GateAddressListResponse gateAdrResponse = GateAddressListResponse.parseDelimitedFrom(socket.getInputStream());
        //             List<GateAddress> gateAddressList = gateAdrResponse.getGateAddressList();
        //             gateAdrList.setGateAdrList(gateAddressList);
        //             System.out.println("inGate");
        //             return "Done";
        //         }
        //         finally {
        //             socket.close();
        //         }

        //     }
        // };

        // System.out.println(rosterSync.gateAddressList);
        carScheduler.schedule(generateCars, getPoisson(FREQ), TimeUnit.SECONDS);
        carScheduler.schedule(listenToTime, 0, TimeUnit.SECONDS);
        carScheduler.scheduleAtFixedRate(new Thread(rosterSync), 0, 10, TimeUnit.SECONDS);
        // carScheduler.submit(getGateList);
        // carScheduler.schedule(getGateList, 0, TimeUnit.SECONDS);
        // System.out.println(a);

    }
}
