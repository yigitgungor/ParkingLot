package edu.rutgers.cs431.teamchen.util;

import edu.rutgers.cs431.TrafficGeneratorProto.TimeRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// A clock that synchronizes with the traffic generator's chronos service
public class SyncClock {

    private static final Logger logger = Logger.getLogger("Gate.SyncClock");
    private static final long CLOCK_SYNC_INTERVAL_IN_MILLISECONDS = 100;
    private static final long CLOCK_UPDATE_INTERVAL_IN_MILLISECONDS = 5;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final ScheduledFuture syncClockThread;
    private final ScheduledFuture updateClockThread;
    private volatile long currentTime = 0L;
    private Socket chronosConn;

    // creates a clock with a given time service socket
    public SyncClock(Socket chronosConn) {
        this.chronosConn = chronosConn;
        this.currentTime = System.currentTimeMillis();
        this.syncClockThread = executor.scheduleWithFixedDelay(
                () -> this.syncTime(),
                0, CLOCK_SYNC_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        this.updateClockThread = executor.scheduleAtFixedRate(
                () -> updateTime(CLOCK_UPDATE_INTERVAL_IN_MILLISECONDS),
                0, CLOCK_UPDATE_INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    // creates a clock with the given address and port number of the time service
    public SyncClock(String addr, int port) throws IOException {
        this(new Socket(addr, port));
    }

    // update the time by adding some seconds
    private void updateTime(long deltaInMils) {
        this.currentTime += deltaInMils;
    }

    // synchronize with the chrono-service
    private void syncTime() {
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
