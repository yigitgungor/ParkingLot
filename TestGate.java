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

public class TestGate {

    public static void main(String argv[]) throws Exception {
        Socket connectionSocket = null;
        ServerSocket listeningSocket = new ServerSocket(Integer.parseInt(argv[0]));
        while(true) {
            connectionSocket = listeningSocket.accept();
            Car car1 = Car.parseDelimitedFrom(connectionSocket.getInputStream());
            System.out.println(car1.getArrivalTimestamp());
            System.out.println(car1.getDepartureTimestamp());

        }
    }

}
