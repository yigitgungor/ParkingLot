package edu.rutgers.cs431.teamchen.monitor;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.GateRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.GateRegisterResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class GateRegisterHttpHandler implements HttpHandler {

    private final Monitor mon;

    public GateRegisterHttpHandler(Monitor mon) {
        this.mon = mon;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        InputStreamReader in = new InputStreamReader(ex.getRequestBody());
        Gson gson = new Gson();
        GateRegisterRequest reg = gson.fromJson(in, GateRegisterRequest.class);
        in.close();
        GateRegisterResponse resp = mon.onGateRegister(reg);
        if (resp == null) {
            ex.sendResponseHeaders(HttpURLConnection.HTTP_NOT_ACCEPTABLE, -1);
            ex.close();
            return;
        }
        ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        OutputStreamWriter writer = new OutputStreamWriter(ex.getResponseBody());
        gson.toJson(resp, writer);
        writer.flush();
        writer.close();
        ex.close();

    }
}
