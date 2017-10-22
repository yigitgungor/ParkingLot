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

public class TestSync {

    public static void main(String argv[]) throws Exception {
        Socket connectionSocket = null;
        try {
            ServerSocket listeningSocket = new ServerSocket(6666);

            connectionSocket = listeningSocket.accept();
            while(true){
            GateAddressListRequest a = GateAddressListRequest.parseDelimitedFrom(connectionSocket.getInputStream());
            String gateHostname = "localhost";
// int gatePort = 2020;
            GateAddress gateAddress1 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2021)
            .build();
            GateAddress gateAddress2 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2022)
            .build();
            GateAddress gateAddress3 = GateAddress.newBuilder()
            .setHostname(gateHostname)
            .setPort(2023)
            .build();
// GateAddress gateAddress4 = GateAddress.newBuilder()
//     .setHostname(gateHostname)
//     .setPort(2024)
//     .build();
// GateAddress gateAddress5 = GateAddress.newBuilder()
//     .setHostname(gateHostname)
//     .setPort(2025)
//     .build();
// GateAddress gateAddress6 = GateAddress.newBuilder()
//     .setHostname(gateHostname)
//     .setPort(2026)
//     .build();
            List<GateAddress> gateAddressList = new ArrayList<GateAddress>();

            gateAddressList.add(gateAddress1);
            gateAddressList.add(gateAddress2);
            gateAddressList.add(gateAddress3);

            GateAddressListResponse gateAddressListResponse =
            GateAddressListResponse.newBuilder()
            .addAllGateAddress(gateAddressList)
            .build();
            gateAddressListResponse.writeDelimitedTo(connectionSocket.getOutputStream());
            // System.out.println("inside test");

            }

        }
            finally {
                // listeningSocket.close();
                connectionSocket.close();
            }
        }
    }

