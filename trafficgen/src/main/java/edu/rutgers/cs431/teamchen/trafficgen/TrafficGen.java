package edu.rutgers.cs431.teamchen.trafficgen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import edu.rutgers.cs431.TrafficGeneratorProto.Car;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeResponse;

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
    return min + (int) (Math.random() * ((max - min) + 1));
  }


  public static void main(String[] args) throws IOException {
    InetAddress lh = InetAddress.getByName("localhost");
    RosterSync rosterSync = new RosterSync(lh, 6666, true);
    int stayingTime = 1000 * 60 * 60 * 2;


    MyClock timer = new MyClock();
    ScheduledExecutorService carScheduler = Executors.newScheduledThreadPool(5);
    Callable<Void> generateCars = new Callable<Void>() {
      public Void call() throws IOException {
        timer.setTime(System.currentTimeMillis());
        long inTime = timer.getTime();
        long outTime = inTime + stayingTime;
        Car car =
            Car.newBuilder().setArrivalTimestamp(inTime).setDepartureTimestamp(outTime).build();
        List<GateAddress> gateAddressList = rosterSync.getGateAddr();
        GateAddress gate = gateAddressList.get(getRandomGate(0, gateAddressList.size() - 1));
        String hostName = gate.getHostname();
        int port = gate.getPort();
        System.out.println(port);
        Socket socket = null;
        try {
          InetAddress lh = InetAddress.getByName(hostName);
          socket = new Socket(lh, port);
          car.writeDelimitedTo(socket.getOutputStream());

        } finally {
          socket.close();
        }
        carScheduler.schedule(this, getPoisson(FREQ), TimeUnit.SECONDS);
        // return car;
        return null;
      }
    };

    Callable<Void> listenToTime = new Callable<Void>() {
      public Void call() throws IOException {
        Socket connectionSocket = null;
        ServerSocket listeningSocket = new ServerSocket(5555);
        while (true) {
          connectionSocket = listeningSocket.accept();
          TimeRequest.parseDelimitedFrom(connectionSocket.getInputStream());
          long currentTimeStamp = timer.getTime();
          TimeResponse timeResponse =
              TimeResponse.newBuilder().setCurrentTimestamp(currentTimeStamp).build();
          timeResponse.writeDelimitedTo(connectionSocket.getOutputStream());
        }
      }
    };

    carScheduler.schedule(generateCars, getPoisson(FREQ), TimeUnit.SECONDS);
    carScheduler.schedule(listenToTime, 0, TimeUnit.SECONDS);
    carScheduler.scheduleAtFixedRate(new Thread(rosterSync), 0, 10, TimeUnit.SECONDS);
  }
}
