package edu.rutgers.cs431.teamchen.gate;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {


        Options options = new Options();
        options.addOption("acp", "accept-car-port", true, "The port number to accept car from traffic generator. " +
                "Default: 9001");
        options.addOption("http", "gate-http", true, "The port number to serve the http service. Default: 9002");
        options.addOption("td", "transfer-duration", true, "The time it takes to transfer the car to the parking " +
                "space. Default: 6000ms");
        options.addOption("m", "monitor-http", true, "The http address of the monitor. Default: " +
                "\"http://localhost:8080\"");
        options.addOption("h", "help", false, "Print this help message");
        options.addOption("ts", "time-service", true, "The tcp address of the time service in \"host:port\"");

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.err.println("invalid arguments: " + e.toString());
            System.exit(1);
        }

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("gate -ts tshost:tsport", options);
            System.exit(0);
        }

        int acceptCarPort = 9001;
        int gateHttpPort = 9002;
        long transferDuration = 6000;
        String monitorHttpAddr = "http://localhost:8080";

        if (cmd.hasOption("acp")) {
            acceptCarPort = Integer.parseInt(cmd.getOptionValue("acp"));
        }

        if (cmd.hasOption("http")) {
            gateHttpPort = Integer.parseInt(cmd.getOptionValue("http"));
        }

        if (cmd.hasOption("td")) {
            transferDuration = Long.parseLong(cmd.getOptionValue("td"));
        }

        if (cmd.hasOption("m")) {
            monitorHttpAddr = cmd.getOptionValue("m");
        }

        if (!cmd.hasOption("ts")) {
            System.err.println("no time service address -ts provided.");
            System.exit(1);
        }

        String[] addr = cmd.getOptionValue("ts").split(":");
        if (addr.length != 2) {
            System.err.println("invalid time service address format");
            System.exit(1);
        }

        Gate gate = new Gate(monitorHttpAddr, acceptCarPort, gateHttpPort, transferDuration, addr[0], Integer.parseInt
                (addr[1]));
        gate.run();
    }
}
