package edu.rutgers.cs431.teamchen.util;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import edu.rutgers.cs431.TrafficGeneratorProto.*;

// A clock that synchronizes with the traffic generator's chronos service
public class SyncClock {

    private static final Logger logger = Logger.getLogger("Gate.SyncClock");
    private static final long CLOCK_UPDATE_INTERVAL_IN_MILLISECONDS = 10;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture updateClockThread;
    private volatile long currentTime = 0L;
    private Socket chronosConn;

    // creates a clock with a given time service socket
    public SyncClock(Socket chronosConn) {
        this.chronosConn = chronosConn;
        this.updateClockThread = executor.scheduleWithFixedDelay(
                () -> this.updateTimeRoutine(),
                0, CLOCK_UPDATE_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    // creates a clock with the given address and port number of the time service
    public SyncClock(String addr, int port) throws IOException {
        this(new Socket(addr, port));
    }

    private void updateTimeRoutine() {
        try {
            TimeRequest tr = TimeRequest.getDefaultInstance();
            tr.writeDelimitedTo(this.chronosConn.getOutputStream());
            TimeResponse ts = TimeResponse.parseDelimitedFrom(this.chronosConn.getInputStream());
            this.currentTime = ts.getCurrentTimestamp();
        } catch (IOException e) {
            this.logger.warning("Unable to synchronize the lock clock: " + e.getMessage());
        }
    }

    // returns the current time synchronized with the traffic generator
    public long getTime() {
        return this.currentTime;
    }
}
