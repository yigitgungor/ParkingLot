package edu.rutgers.cs431.teamchen.proto;



// Gate -> Monitor: The Gate sends this object to the Monitor, and expects GateRegisterResponse
public class GateRegisterRequest {
    // port for TCP service that the traffic generator connects to
    public final int port;

    // HTTP port that provides inter-component http service
    public final int httpPort;

    public GateRegisterRequest(int port, int httpPort){
        this.port = port;
        this.httpPort = httpPort;
    }
}
