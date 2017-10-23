package edu.rutgers.cs431.teamchen.parkingspace;

import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {
        String monitorHttpAddr = args[0];
        int httpPort = Integer.parseInt(args[1]);
        try {
            ParkingSpace parkingSpace = new ParkingSpace(monitorHttpAddr, httpPort);
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
            System.exit(1);
        }
    }
}
