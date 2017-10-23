package edu.rutgers.cs431.teamchen.proto;

import java.util.ArrayList;

public class GateRegisterResponse {
    public static final int STRATEGY_NO_SHARED = 0;
    public static final int STRATEGY_DISTRIBUTED = 1;
    public static final int STRATEGY_FOR_PROFIT = 2;
    public int strategy;

    public String trafficGeneratorAddr;
    public int trafficGeneratorPort;

    public String parkingSpaceHttpUrl;

    // The list of initial tokens provided to this gate
    public ArrayList<String> tokens;

}
