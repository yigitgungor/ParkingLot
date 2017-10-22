package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;

import java.io.IOException;
import java.io.InputStreamReader;

public class CarLeavingHttpHandler implements HttpHandler {

    private final Gate gate;

    public CarLeavingHttpHandler(Gate gate) {
        this.gate = gate;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        InputStreamReader reqBody = new InputStreamReader(ex.getRequestBody());
        CarWithToken cwt = gson.fromJson(reqBody, CarWithToken.class);
        reqBody.close();

        // process this car
        new Thread(() -> this.gate.onCarLeaving(cwt)).start();

        // close the http connection
        ex.close();
    }
}
