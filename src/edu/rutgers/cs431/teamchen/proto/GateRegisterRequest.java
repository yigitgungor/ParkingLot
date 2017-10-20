package edu.rutgers.cs431.teamchen.proto;



// For communication between Gate and Monitor
// Gate -> Monitor: The Gate sends this object to the Monitor, and expects GateRegisterResponse
public class GateRegisterRequest {
    // port for TCP service that the traffic generator connects to
    public int port;

    // HTTP port that provides inter-component http service
    public int httpPort;

    public GateRegisterRequest(int port){
        this.port = port;
    }
}
