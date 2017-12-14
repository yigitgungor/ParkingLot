package edu.rutgers.cs431.teamchen.monitor;

import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;
import org.apache.commons.cli.*;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("http", "monitor-http", true, "The port number to serve the http service. Default: 8080");
        options.addOption("s", "strategy", true, "The strategy to distribute tokens within the system. 1 for no " +
                "token sharing between gates, and 2 for sharing tokens. Default: 2");
        options.addOption("maxg", "max-gates", true, "The maximum number of gates. Default: 6 ");
        options.addOption("pc", "parking-cap", true, "The parking capacity of the parking lot. Default: 200");
        options.addOption("h", "help", false, "Print this help message");

        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);

        } catch (ParseException e) {
            System.err.println("invalid arguments: " + e.toString());
            System.exit(1);
        }
        if (cmd.hasOption("h")) {
            new HelpFormatter().printHelp("monitor", options);
            System.exit(0);
        }

        int httpPort = 8080;
        int strategy = 2;
        int maxGate = 6;
        long maxParkingCap = 200;

        if (cmd.hasOption("http")) {
            httpPort = Integer.parseInt(cmd.getOptionValue("http"));
        }

        if (cmd.hasOption("s")) {
            strategy = Integer.parseInt(cmd.getOptionValue("s"));
        }

        if (cmd.hasOption("maxg")) {
            maxGate = Integer.parseInt(cmd.getOptionValue("maxg"));
        }

        if (cmd.hasOption("pc")) {
            maxParkingCap = Long.parseLong(cmd.getOptionValue("pc"));
        }

        try {
            new Monitor(httpPort, interpretStrategy(strategy), maxGate, maxParkingCap).run();
        } catch (UnknownHostException e) {
            System.err.println("can't get hostname: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int interpretStrategy(int i) {
        switch (i) {
            case 1:
                return GateRegisterResponse.STRATEGY_NO_SHARED;
            case 2:
                return GateRegisterResponse.STRATEGY_DISTRIBUTED;
            default:
                System.err.println("strategy not recognizable: please pick 1, or 2.");
                System.exit(1);
        }
        return 0;
    }
}
