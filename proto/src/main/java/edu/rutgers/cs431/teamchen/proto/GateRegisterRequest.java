package edu.rutgers.cs431.teamchen.proto;


// Gate -> Monitor: The Gate sends this object to the Monitor, and expects GateRegisterResponse
public class GateRegisterRequest {

    // the name of the gate's host
    public final String hostname;

    // port for TCP service that the traffic generator connects to
    public final int tcpPort;

    // Http Url that addresses this gate
    public final int httpPort;

    public GateRegisterRequest(String hostname, int tcpPort, int httpPort) {
        this.hostname = hostname;
        this.tcpPort = tcpPort;
        this.httpPort = httpPort;
    }
}
