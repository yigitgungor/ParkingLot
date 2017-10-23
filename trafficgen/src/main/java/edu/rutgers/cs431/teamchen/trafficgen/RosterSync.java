package edu.rutgers.cs431.teamchen.trafficgen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListResponse;

public class RosterSync implements Runnable {

  private boolean DEBUG = false;
  protected List<GateAddress> gateAddressList = null;
  private int monitorPort;
  private InetAddress monitorAddress;
  private Socket monitorSocket = null;

  public RosterSync(InetAddress monitorAddress, int monitorPort, boolean debug) {
    this.monitorAddress = monitorAddress;
    this.monitorPort = monitorPort;
    this.DEBUG = debug;

    try {
      monitorSocket = new Socket(this.monitorAddress, this.monitorPort);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // TO START THIS THREAD :
  // Runnable r = new RosterSync(monitor address, monitor port, myPort);
  // new Thread(r).start();

  @Override
  public void run() {
    if (DEBUG)
      System.out.println("sending req");
    sendGateAddressListRequest();
    if (DEBUG)
      System.out.println("req sent");
    getGateAddressListResponse();
    if (DEBUG)
      System.out.println("got res");
    if (DEBUG)
      System.out.println("Shutdown");
  }

  private void sendGateAddressListRequest() {
    GateAddressListRequest addressListRequest = GateAddressListRequest.getDefaultInstance();
    try {
      addressListRequest.writeDelimitedTo(monitorSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<GateAddress> getGateAddr() {
    return this.gateAddressList;
  }

  private List<GateAddress> getGateAddressListResponse() {
    try {
      GateAddressListResponse response =
          GateAddressListResponse.parseDelimitedFrom(monitorSocket.getInputStream());
      if (DEBUG) {
        for (GateAddress x : response.getGateAddressList()) {
          System.out.println("hostname: " + x.getHostname());
          System.out.println("port: " + x.getPort());
        }
      }
      this.gateAddressList = response.getGateAddressList();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this.gateAddressList;
  }

  private void close() {
    try {
      monitorSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    close();
  }

  public int getMonitorPort() {
    return this.monitorSocket.getPort();
  }

  public InetAddress getMonitorAddress() {
    return this.monitorSocket.getInetAddress();
  }
}
