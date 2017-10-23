package edu.rutgers.cs431.teamchen.parkingspace;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class CarEnteringHttpHandler implements HttpHandler {

    private final ParkingSpace ps;

    public CarEnteringHttpHandler(ParkingSpace ps) {
        this.ps = ps;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        InputStreamReader reqBody = new InputStreamReader(ex.getRequestBody());
        CarWithToken cwt = gson.fromJson(reqBody, CarWithToken.class);
        reqBody.close();

        ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
        // close the http connection
        ex.close();
        // process this car
        new Thread(() -> this.ps.onCarEntering(cwt)).start();
    }
}
