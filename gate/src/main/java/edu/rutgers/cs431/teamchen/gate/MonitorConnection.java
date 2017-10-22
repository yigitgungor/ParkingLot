package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import edu.rutgers.cs431.teamchen.proto.GateRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


// abstracts and defines the HTTP communication layer with the monitor
public class MonitorConnection {

    private static final Logger logger = Logger.getLogger("MonitorConnection");
    private static final String GATE_REGISTER_PATH = "register";
    private final URL monitor;

    public MonitorConnection(String monitorUrl) throws MalformedURLException {
        this(new URL(monitorUrl));
    }

    public MonitorConnection(URL monitorURL) {
        this.monitor = monitorURL;
    }

    public GateRegisterResponse registersGate(GateRegisterRequest req) throws IOException, RuntimeException {
        Gson gson = new Gson();

        HttpURLConnection conn = (HttpURLConnection) (new URL(this.monitor, GATE_REGISTER_PATH)).openConnection();
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        gson.toJson(req, writer);
        writer.flush();
        writer.close();

        GateRegisterResponse grr = null;
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) { // success
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            grr = gson.fromJson(reader, GateRegisterResponse.class);
            reader.close();
            conn.disconnect();
        } else {
            throw new RuntimeException("problem processing a register request: code is not HTTP_OK");
        }

        return grr;
    }

}
