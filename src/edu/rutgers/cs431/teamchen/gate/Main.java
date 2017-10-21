package edu.rutgers.cs431.teamchen.gate;

public class Main {
    public static void main(String[] args) {
        String monitorAddr = args[0];
        int monitorPort = Integer.parseInt(args[1]);
        int gatePort = Integer.parseInt(args[2]);
        int gateHttpPort = Integer.parseInt(args[3]);
        long transferDuration = Long.parseLong(args[4]);
        Gate gate = new Gate(monitorAddr, monitorPort, gatePort, gateHttpPort, transferDuration);
        gate.run();
    }
}
