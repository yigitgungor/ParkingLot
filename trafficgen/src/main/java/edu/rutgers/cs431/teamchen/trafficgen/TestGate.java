package edu.rutgers.cs431.teamchen.trafficgen;

import java.net.ServerSocket;
import java.net.Socket;
import edu.rutgers.cs431.TrafficGeneratorProto.Car;

public class TestGate {

  public static void main(String argv[]) throws Exception {
    Socket connectionSocket = null;
    ServerSocket listeningSocket = new ServerSocket(Integer.parseInt(argv[0]));
    while (true) {
      connectionSocket = listeningSocket.accept();
      Car car1 = Car.parseDelimitedFrom(connectionSocket.getInputStream());
      System.out.println(car1.getArrivalTimestamp());
      System.out.println(car1.getDepartureTimestamp());
    }
  }
}
