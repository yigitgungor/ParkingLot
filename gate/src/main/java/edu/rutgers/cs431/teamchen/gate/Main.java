package edu.rutgers.cs431.teamchen.gate;

public class Main {
    public static void main(String[] args) {
        String monitorAddr = args[0];
        int gateTcpPort = Integer.parseInt(args[1]);
        int gateHttpPort = Integer.parseInt(args[2]);
        long transferDuration = Long.parseLong(args[3]);
        Gate gate = new Gate(monitorAddr, gateTcpPort, gateHttpPort, transferDuration);
        gate.run();
    }
}
