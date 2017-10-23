package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.GateHttpAddressesChangeRequest;

import java.io.IOException;
import java.io.InputStreamReader;

public class GatePeerAddressChangeHttpHandler implements HttpHandler {
    private final Gate gate;

    public GatePeerAddressChangeHttpHandler(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(ex.getRequestBody());
        GateHttpAddressesChangeRequest req = gson.fromJson(reader, GateHttpAddressesChangeRequest.class);
        reader.close();
        ex.close();

        req.gateHttpAddrs.remove(req.index);
        gate.setPeerHttpAddressesFromStr(req.gateHttpAddrs);
    }
}
