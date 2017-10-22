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
    long lastCarTime;

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
        int stayingTime = 1000 * 60 * 60 * 2;

        // create gate list
        String gateHostname = "localhost";
        // int gatePort = 2020;
        GateAddress gateAddress1 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2021)
            .build();
        GateAddress gateAddress2 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2022)
            .build();
        GateAddress gateAddress3 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2023)
            .build();
        // GateAddress gateAddress4 = GateAddress.newBuilder()
        //     .setHostname(gateHostname)
        //     .setPort(2024)
        //     .build();
        // GateAddress gateAddress5 = GateAddress.newBuilder()
        //     .setHostname(gateHostname)
        //     .setPort(2025)
        //     .build();
        // GateAddress gateAddress6 = GateAddress.newBuilder()
        //     .setHostname(gateHostname)
        //     .setPort(2026)
        //     .build();
        List<GateAddress> gateAddressList = new ArrayList<GateAddress>();

        gateAddressList.add(gateAddress1);
        gateAddressList.add(gateAddress2);
        gateAddressList.add(gateAddress3);
        // gateAddressList.add(gateAddress4);
        // gateAddressList.add(gateAddress5);
        // gateAddressList.add(gateAddress6);



        // ----------------end gate list


        // generate arrival time
        // add staying time to get departureTimestamp
        // keep track of last arrival time

        // System.out.println(start);


        MyClock timer = new MyClock();
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

                GateAddress gate = gateAddressList.get(getRandomGate(0, 2));
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
            public Void call() throws IOException, InterruptedException {
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
//                 long inTime = System.currentTimeMillis();
//                 long outTime = inTime + stayingTime;
//                 Car car = Car.newBuilder()
//                     .setArrivalTimestamp(inTime)
//                     .setDepartureTimestamp(outTime)
//                     .build();
// //                System.out.println(car.getArrivalTimestamp());

//                 GateAddress gate = gateAddressList.get(getRandomGate(0, 2));
//                 String hostName = gate.getHostname();
//                 int port = gate.getPort();
//                 System.out.println(port);
//                 // int port = 2020;
//                 Socket socket = null;
//                 try {
//                     InetAddress lh = InetAddress.getByName(hostName);
//                     socket = new Socket(lh, port);
//                     long start = System.currentTimeMillis();
//                     car.writeDelimitedTo(socket.getOutputStream());

//                 }
//                 finally {
//                     socket.close();
//                 }
//                 carScheduler.schedule(this, getPoisson(FREQ), TimeUnit.SECONDS);
// //                return car;
                // return null;
            }
        };


        carScheduler.schedule(generateCars, getPoisson(FREQ), TimeUnit.SECONDS);
        carScheduler.schedule(listenToTime, 5, TimeUnit.SECONDS);
        // System.out.println(a);



        // long arrivalTimestamp = 1506451595665L;
        // long departureTimestamp = 1506451595670L;
        // Car car = Car.newBuilder()
        //     .setArrivalTimestamp(arrivalTimestamp)
        //     .setDepartureTimestamp(departureTimestamp)
        //     .build();
        // System.out.println(car);
    }
}
