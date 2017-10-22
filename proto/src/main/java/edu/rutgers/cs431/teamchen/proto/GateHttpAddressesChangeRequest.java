package edu.rutgers.cs431.teamchen.proto;

import java.util.ArrayList;

// Monitor -> all Gates: the monitor sends a gate's http addresses to all gates when something changes.
// The monitor expects no response.
// (.ie after adding a new gate to the system)
public class GateHttpAddressesChangeRequest {
    // the gates' http addresses
    public ArrayList<String> gateHttpAddrs;
    // the index of the gate receiving this request in the address list
    public int index;
}
