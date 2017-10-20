package edu.rutgers.cs431.teamchen;

import java.io.IOException;
import java.net.Socket;

import edu.rutgers.cs431.TrafficGeneratorProto.*;

public class SyncClock {
    private volatile long currentTime = 0L;
    private Socket trafGen;

    public SyncClock(Socket trafGen) {
        this.trafGen = trafGen;
    }

    public long getTime() throws IOException {
        TimeRequest tr = TimeRequest.getDefaultInstance();
        tr.writeDelimitedTo(trafGen.getOutputStream());
        TimeResponse ts = TimeResponse.parseDelimitedFrom(trafGen.getInputStream());
        this.currentTime = ts.getCurrentTimestamp();
        return this.currentTime;
    }
}
