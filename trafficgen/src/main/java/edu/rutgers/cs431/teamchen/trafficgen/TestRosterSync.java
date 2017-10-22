package edu.rutgers.cs431.teamchen.trafficgen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListResponse;

public class TestRosterSync {

  public static void main(String[] sa) {
    Socket connectionSocket = null;
    Socket syncSocket = null;
    ServerSocket listeningSocket = null;
    try {
      InetAddress lh = InetAddress.getByName("localhost");
      listeningSocket = new ServerSocket(5050);
      // System.out.println(" got here ");

      RosterSync rosterSync = new RosterSync(lh, 5050, 5051);
      new Thread(rosterSync).start();
      // System.out.println(" and got here ");
      connectionSocket = listeningSocket.accept();



      syncSocket = new Socket(lh, 5051);
      System.out.println("WAITING FOR REQUEST");
      GateAddressListRequest request =
          GateAddressListRequest.parseDelimitedFrom(connectionSocket.getInputStream());
      if (request != null) {
        System.out.println("GOT REQUEST");
        GateAddress gateAddress1 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2021).build();
        GateAddress gateAddress2 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2022).build();
        GateAddress gateAddress3 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2023).build();
        GateAddress gateAddress4 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2024).build();
        GateAddress gateAddress5 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2025).build();
        GateAddress gateAddress6 =
            GateAddress.newBuilder().setHostname("localhost").setPort(2026).build();

        List<GateAddress> gateAddressList = new ArrayList<GateAddress>();
        gateAddressList.add(gateAddress1);
        gateAddressList.add(gateAddress2);
        gateAddressList.add(gateAddress3);
        gateAddressList.add(gateAddress4);
        gateAddressList.add(gateAddress5);
        gateAddressList.add(gateAddress6);

        GateAddressListResponse gateAddressListResponse =
            GateAddressListResponse.newBuilder().addAllGateAddress(gateAddressList).build();

        gateAddressListResponse.writeDelimitedTo(syncSocket.getOutputStream());

        rosterSync.shutdown();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
