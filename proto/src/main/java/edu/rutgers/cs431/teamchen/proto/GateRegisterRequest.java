package edu.rutgers.cs431.teamchen.proto;


// Gate -> Monitor: The Gate sends this object to the Monitor, and expects GateRegisterResponse
public class GateRegisterRequest {
    // port for TCP service that the traffic generator connects to
    public final int port;

    // Http Url that addresses this gate
    public final String httpUrl;

    public GateRegisterRequest(int port, String httpUrl) {
        this.port = port;
        this.httpUrl = httpUrl;
    }
}
