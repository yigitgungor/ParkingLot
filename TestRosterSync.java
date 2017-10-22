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
    ServerSocket listeningSocket = null;
    try {
      InetAddress lh = InetAddress.getByName("localhost");
      listeningSocket = new ServerSocket(6666);

      // RosterSync rosterSync = new RosterSync(lh, 6666, false);
      // new Thread(rosterSync).start();
      connectionSocket = listeningSocket.accept();

      while (true) {
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
          // GateAddress gateAddress4 =
          //     GateAddress.newBuilder().setHostname("localhost").setPort(2024).build();
          // GateAddress gateAddress5 =
          //     GateAddress.newBuilder().setHostname("localhost").setPort(2025).build();
          // GateAddress gateAddress6 =
          //     GateAddress.newBuilder().setHostname("localhost").setPort(2026).build();

          List<GateAddress> gateAddressList = new ArrayList<GateAddress>();
          gateAddressList.add(gateAddress1);
          gateAddressList.add(gateAddress2);
          gateAddressList.add(gateAddress3);
          // gateAddressList.add(gateAddress4);
          // gateAddressList.add(gateAddress5);
          // gateAddressList.add(gateAddress6);

          GateAddressListResponse gateAddressListResponse =
              GateAddressListResponse.newBuilder().addAllGateAddress(gateAddressList).build();

          gateAddressListResponse.writeDelimitedTo(connectionSocket.getOutputStream());
          System.out.println("RES SENT");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
