package edu.rutgers.cs431.teamchen.gate;

import edu.rutgers.cs431.teamchen.proto.GateRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;


// abstracts and defines the HTTP communication layer with the monitor
public class MonitorConnection {
    private final String monitorUrl;

    public MonitorConnection(String monitorAddress, int port) {
        this(monitorAddress + ":" + port);
    }

    public MonitorConnection(String monitorUrl) {
        this.monitorUrl = monitorUrl;
    }

    public GateRegisterResponse registersGate(GateRegisterRequest req) {
        // Construct an HTTP connection
        // Send the car request
        // then synchronously waits for a response
        return null; // TODO
    }

}
