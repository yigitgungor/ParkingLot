package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.GateStatRequest;
import edu.rutgers.cs431.teamchen.proto.GateStatResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class GateStatsHttpHandler implements HttpHandler {
    private final Gate gate;

    public GateStatsHttpHandler(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void handle(HttpExchange exch) throws IOException {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(exch.getRequestBody());
        gson.fromJson(reader, GateStatRequest.class);
        reader.close();

        exch.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        // Construct a response
        GateStatResponse resp = new GateStatResponse();
        resp.totalWaitingTime = gate.getTotalWaitingTime();
        resp.totalCarsProcessed = gate.getCarsProcessedCount();
        resp.lastTimeProcessedCar = gate.getLastTimeProcessedCar();
        if (gate.gateWithErrorPort == gate.gateTcpPort) {
            gate.fixError();
        }

        OutputStreamWriter writer = new OutputStreamWriter(exch.getResponseBody());
        gson.toJson(resp, writer);
        writer.flush();
        writer.close();

        exch.close();
    }
}
