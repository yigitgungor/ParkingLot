package edu.rutgers.cs431.teamchen.monitor;

import edu.rutgers.cs431.TrafficGeneratorProto;

public class GateInfo {
    public volatile String httpAddress;
    public TrafficGeneratorProto.GateAddress addr;
    public volatile long totalWaitingTime;
    public volatile int totalCarsProcessed;

    public volatile long lastTimeProcessedCar;
}
