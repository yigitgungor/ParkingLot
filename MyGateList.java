import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;
// import edu.rutgers.cs431.TrafficGeneratorProto.Car;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeResponse;
import edu.rutgers.cs431.TrafficGeneratorProto.TimeRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListResponse;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListRequest;

public class MyGateList {
    public List<GateAddress> gateAdrList;

    public MyGateList() {
        this.gateAdrList = null;
    }

    public List<GateAddress> getAdrList() {
        return this.gateAdrList;
    }

    public void setGateAdrList(List<GateAddress> gateAdrList) {
        this.gateAdrList = gateAdrList;
    }

}
