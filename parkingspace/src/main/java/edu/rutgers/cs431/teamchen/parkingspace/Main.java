package edu.rutgers.cs431.teamchen.parkingspace;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.MalformedURLException;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("m", "monitor-http", true, "The http address of the monitor. Default " +
                ": \"http://localhost:8080/\"");
        options.addOption("http", "parking-space-http", true, "The port number to serve the http service. Default" +
                ": 8081");
        options.addOption("h", "help", false, "Print this help message");
        options.addOption("ts", "time-service", true, "The tcp address of the time service in \"host:port\"");

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("invalid arguments: " + e.toString());
            System.exit(1);
        }

        // Print help message
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("parkspc -ts tshost:tsport", options);
            System.exit(0);
        }


        String monitorHttpAddr = "http://localhost:8080/";
        int httpPort = 8081;
        String[] timeServiceAddr = cmd.getOptionValue("ts").split(":");

        if (cmd.hasOption("m")) {
            monitorHttpAddr = cmd.getOptionValue("m");
        }

        if (cmd.hasOption("http")) {
            httpPort = Integer.parseInt(cmd.getOptionValue("http"));
        }

        if (!cmd.hasOption("ts")) {
            System.err.println("no time service address -ts provided.");
            System.exit(1);
        }

        if (timeServiceAddr.length != 2) {
            System.err.println("invalid time service address format");
            System.exit(1);
        }

        try {
            ParkingSpace parkingSpace = new ParkingSpace(monitorHttpAddr, httpPort, timeServiceAddr[0], Integer
                    .parseInt(timeServiceAddr[1]));
            parkingSpace.run();
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            System.exit(1);
        }
    }
}
