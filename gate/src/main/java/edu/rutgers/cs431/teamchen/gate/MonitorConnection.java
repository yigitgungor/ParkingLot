package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import edu.rutgers.cs431.teamchen.proto.GateRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


// abstracts and defines the HTTP communication layer with the monitor
public class MonitorConnection {
    private static final String GATE_REGISTER_PATH = "register";
    private final URL monitor;

    public MonitorConnection(String monitorUrl) throws MalformedURLException {
        this(new URL(monitorUrl));
    }

    public MonitorConnection(URL monitorURL) {
        this.monitor = monitorURL;
    }

    public GateRegisterResponse registersGate(GateRegisterRequest req) throws IOException {
        Gson gson = new Gson();

        HttpURLConnection conn = (HttpURLConnection) (new URL(this.monitor, GATE_REGISTER_PATH)).openConnection();
        // TODO
        return null;
    }

}
