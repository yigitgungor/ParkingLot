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


public class A {
    public static void main(String argv[]) throws Exception {

        Socket socket = null;
        try {
            InetAddress lh = InetAddress.getByName("localhost");
            socket = new Socket(lh, 5555);
            TimeRequest tr = TimeRequest.getDefaultInstance();
            tr.writeDelimitedTo(socket.getOutputStream());
            // TimeRequest timeRequest = TimeRequest.getDefaultIntance();
            TimeResponse timeRes = TimeResponse.parseDelimitedFrom(socket.getInputStream());
            long currentTimeStamp = timeRes.getCurrentTimestamp();
            System.out.println(currentTimeStamp);
        }
        finally {
            socket.close();
        }

    }
}
