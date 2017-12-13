package edu.rutgers.cs431.teamchen.util;

// all system level configuration variables
public class SystemConfig {
    public static final int MONITOR_ROSTER_PROTOBUF_SERVICE_PORT = 6666;
    public static final int TRAFFIC_GENERATOR_CHRONOS_SERVICE_PORT = 5555;


    // HTTP Path config
    public static final String MONITOR_PARKING_SPACE_REGISTER_PATH = "/parking_register";
    public static final String MONITOR_GATE_REGISTER_PATH = "/gate_register";
    public static final String GATE_PEER_ADDRESS_CHANGE_PATH = "/gates_update";
    public static final String GATE_GET_STATS_PATH = "/stats";
    public static final String GATE_CAR_LEAVING_PATH = "/car_leaving";
    public static final String GATE_SHARE_TOKEN_PATH = "/share_token";
    public static final String PARKING_SPACE_CAR_ENTERING_PATH = "/car_entering";
    public static final String PARKING_SPACE_PEER_ADDRESS_CHANGE_PATH = "/gates_update";

    public static final int MAXIMUM_HTTP_CONNECTIONS = 200;
}
