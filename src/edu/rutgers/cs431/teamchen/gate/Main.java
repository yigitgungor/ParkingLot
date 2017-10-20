package edu.rutgers.cs431.teamchen.gate;

public class Main {
    public static void main(String[] args){
        String monitorAddr = args[0];
        int monitorPort = Integer.parseInt(args[1]);
        int gatePort = Integer.parseInt(args[2]);
        long transferDuration = Long.parseLong(args[3]);

        Gate gate = new Gate(monitorAddr, monitorPort, gatePort, transferDuration);
        gate.run();

    }

}
