package edu.rutgers.cs431.teamchen.monitor;

import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        int httpPort = Integer.parseInt(args[0]);
        int strategy = interpretStrategy(Integer.parseInt(args[1]));
        int maxGate = Integer.parseInt(args[2]);
        long maxParkingCap = Long.parseLong(args[3]);
        try {
            new Monitor(httpPort, strategy, maxGate, maxParkingCap).run();
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
            case 3:
                return GateRegisterResponse.STRATEGY_FOR_PROFIT;
            default:
                System.err.println("strategy not recognizable: please pick 1, 2, or 3.");
                System.exit(1);
        }
        return 0;
    }
}
