package edu.rutgers.cs431.teamchen.trafficgen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddress;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListRequest;
import edu.rutgers.cs431.TrafficGeneratorProto.GateAddressListResponse;

public class RosterSync implements Runnable {

  protected List<GateAddress> gateAddressList = null;
  private int monitorPort;
  private InetAddress monitorAddress;
  private int myPort;
  private Socket connectionSocket = null;
  private Socket monitorSocket = null;
  private ServerSocket listeningSocket = null;
  private volatile boolean shutdown;

  public RosterSync(InetAddress monitorAddress, int monitorPort, int myPort) {
    this.monitorAddress = monitorAddress;
    this.monitorPort = monitorPort;
    this.myPort = myPort;

    try {
      listeningSocket = new ServerSocket(this.myPort);
      connectionSocket = listeningSocket.accept();
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
    while (!shutdown) {
      sendGateAddressListRequest();
      getGateAddressListResponse();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendGateAddressListRequest() {
    GateAddressListRequest addressListRequest = GateAddressListRequest.getDefaultInstance();
    try {
      addressListRequest.writeDelimitedTo(monitorSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<GateAddress> getGateAddressListResponse() {
    try {
      GateAddressListResponse response =
          GateAddressListResponse.parseDelimitedFrom(connectionSocket.getInputStream());

      this.gateAddressList = response.getGateAddressList();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this.gateAddressList;
  }

  private void close() {
    try {
      connectionSocket.close();
      listeningSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    close();
    shutdown = true;
  }

  public int getMonitorPort() {
    return this.monitorSocket.getPort();
  }

  public InetAddress getMonitorAddress() {
    return this.monitorSocket.getInetAddress();
  }

  public int getPort() {
    return this.connectionSocket.getPort();
  }

  public InetAddress getAddress() {
    return this.connectionSocket.getInetAddress();
  }
}
