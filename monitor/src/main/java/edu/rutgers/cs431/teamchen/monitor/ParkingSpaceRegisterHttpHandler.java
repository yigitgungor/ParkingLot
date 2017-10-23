package edu.rutgers.cs431.teamchen.monitor;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterRequest;
import edu.rutgers.cs431.teamchen.proto.ParkingSpaceRegisterResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class ParkingSpaceRegisterHttpHandler implements HttpHandler {

    private final Monitor mon;

    public ParkingSpaceRegisterHttpHandler(Monitor mon) {
        this.mon = mon;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        InputStreamReader in = new InputStreamReader(ex.getRequestBody());
        Gson gson = new Gson();
        ParkingSpaceRegisterRequest reg = gson.fromJson(in, ParkingSpaceRegisterRequest.class);
        in.close();
        ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);


        ParkingSpaceRegisterResponse resp = new ParkingSpaceRegisterResponse();
        resp.trafGenAddr = mon.getTrafGenAddr();
        resp.trafGenPort = mon.getTrafGenPort();
        OutputStreamWriter writer = new OutputStreamWriter(ex.getResponseBody());
        gson.toJson(resp, writer);
        writer.flush();
        writer.close();
        ex.close();
        new Thread(() -> mon.onParkingSpaceRegister(reg)).start();
    }
}