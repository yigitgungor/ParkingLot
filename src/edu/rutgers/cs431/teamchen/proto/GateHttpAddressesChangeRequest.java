package edu.rutgers.cs431.teamchen.proto;

import java.util.ArrayList;

// Monitor -> all Gates: the monitor sends a new gate list to all gates when something changes
public class GateHttpAddressesChangeRequest {
    public ArrayList<String> gateHttpAddresses;
}
