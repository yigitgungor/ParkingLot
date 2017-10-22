package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.GateStatResponse;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class GateStatHttpHandler implements HttpHandler {
    private final Gate gate;

    public GateStatHttpHandler(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void handle(HttpExchange exch) throws IOException {
        exch.getRequestBody().close();

        exch.sendResponseHeaders(200, 0);
        // Construct a response
        GateStatResponse resp = new GateStatResponse();
        resp.totalWaitingTime = gate.getTotalWaitingTime();
        resp.totalCarsProcessed = gate.getCarsProcessedCount();

        Gson gson = new Gson();
        OutputStreamWriter writer = new OutputStreamWriter(exch.getResponseBody());
        gson.toJson(resp, writer);
        writer.flush();
        writer.close();
    }
}
