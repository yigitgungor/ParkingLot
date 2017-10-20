package edu.rutgers.cs431.teamchen.proto;

import edu.rutgers.cs431.TrafficGeneratorProto;

import java.util.ArrayList;

public class GateRegisterResponse {
    public static final int STRATEGY_NO_SHARED = 0;
    public static final int STRATEGY_DISTRIBUTED = 1;
    public static final int STRATEGY_FOR_PROFIT = 2;

    public String trafficGeneratorAddr;
    public int trafficGeneratorPort;

    public String parkingLotHttpUrl;

    // The list of all peers gate
    // should be null if the strategy is STRATEGY_NO_SHARED
    public ArrayList<TrafficGeneratorProto.GateAddress> gateAddrs;

    // The list of initial tokens provided to this gate
    public ArrayList<String> tokens;


    public int strategy;
}
