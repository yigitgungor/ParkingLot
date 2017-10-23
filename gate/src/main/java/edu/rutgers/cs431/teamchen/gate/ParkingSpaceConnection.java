package edu.rutgers.cs431.teamchen.gate;

import com.google.gson.Gson;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;
import edu.rutgers.cs431.teamchen.util.SystemConfig;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ParkingSpaceConnection {
    private final URL parkingSpace;


    public ParkingSpaceConnection(URL parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public ParkingSpaceConnection(String parkingSpaceHttpUrl) throws MalformedURLException {
        this(new URL(parkingSpaceHttpUrl));
    }

    // sends car to the parking lot
    public void sendCarToParkingSpace(CarWithToken cwt) throws IOException {
        // Construct a connection
        HttpURLConnection conn = (HttpURLConnection) new URL(this.parkingSpace, SystemConfig.PARKING_SPACE_CAR_ENTERING_PATH).openConnection();
        conn.setDoOutput(true); // For POST requests
        // Write to body
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        Gson gson = new Gson();
        gson.toJson(cwt, writer);

        writer.flush();
        writer.close();
        conn.disconnect();
    }

}
