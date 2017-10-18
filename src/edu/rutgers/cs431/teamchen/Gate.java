package edu.rutgers.cs431.teamchen;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;

public class Gate {

    private final int gatePort;
    private final Socket monitor;
    private final SyncClock clock;

    // the carsAcceptor's waiting queue
    private final Queue<Car> waitingQueue;

    // a service socket that accepts traffic generator's car stream
    private ServerSocket carsAcceptor;

    private long tokenCount = 0L;
    private long totalWaitingTime = 0L;
    private long carsParkedCount = 0L;


    // contacts the monitor to registers this carsAcceptor's address and port
    private void registersWithTheMonitor() throws RuntimeException{
        // TODO: figure out the communication protocol with the monitor??
    }

    // gets the traffic generator address from the monitor
    private static InetSocketAddress retrievesTrafficGeneratorAddress(Socket monitor) {
        // TODO: figure out the communication protocol with the monitor??
        return null;
    }

    private Runnable listensToTheMonitor(){
        // TODO: figure out the communication protocol with the monitor??
        return null;
    }

    // sends the monitor the needed statistics
    private void sendsStatResponse(){
        // TODO: figure out the communication protocol with the monitor??

    }


    // actively listens on the carsAcceptor, and expects a new car stream from
    // a traffic generator
    private void acceptsGeneratorCarStreams() {
        while(true){
            try(Socket carStream = this.carsAcceptor.accept()){
                new Thread(this.makeCarStreamHandler(carStream)).run();
            }catch (Exception e){
                reportError("Accepting a new car stream: "+e.getMessage());
            }
        }
    }

    // returns a thread that handles incoming car stream
    private Runnable makeCarStreamHandler(Socket incomingCarSocket) {
        // TODO
        return null;
    }

    private static final Logger logger = Logger.getLogger("Gate");

    private static void reportError(String msg) {
        logger.warning(msg);
    }

    private static void log(String msg) {
        logger.info(msg);
    }

    public Gate(String monitorAddr, int monitorPort, int gatePort) {
        this.waitingQueue = (LinkedList<Car>) Collections.synchronizedList(new LinkedList<Car>());
        this.gatePort = gatePort;

        // Connects to the monitor
        log("Connecting to the monitor...");
        Socket monitor = null;
        try {
            monitor = new Socket(monitorAddr, monitorPort);
        } catch (IOException e) {
            reportError("Unable to connect to the monitor: " + e.getMessage());
            System.exit(1);
        }
        this.monitor = monitor;

        // Discover the traffic generator and set up the clock
        log("Setting up connection to traffic generator");
        InetSocketAddress tgAddr = retrievesTrafficGeneratorAddress(this.monitor);
        Socket trafGen = null;
        try {
            trafGen = new Socket(tgAddr.getAddress(), tgAddr.getPort());
        } catch (IOException e) {
            reportError("Unable to connect to the traffic generator: " + e.getMessage());
            System.exit(1);
        }
        this.clock = new SyncClock(trafGen);

        // Creates a car accepting socket
        log("Starting to accept cars...");
        ServerSocket carAcceptor = null;
        try {
            carsAcceptor = new ServerSocket(gatePort);
        } catch (IOException e) {
            reportError("Unable to set up a car accepting socket: " + e.getMessage());
        }
        this.carsAcceptor = carAcceptor;

    }

    public static void main(String[] args) {
        String monitorAddr = args[0];
        int monitorPort = Integer.parseInt(args[1]);
        int gatePort = Integer.parseInt(args[2]);

        Gate gate = new Gate(monitorAddr, monitorPort, gatePort);

        try{
            gate.registersWithTheMonitor();
        }catch (RuntimeException e){
            reportError("Unable to register this gate with the monitor: "+e.getMessage());
            System.exit(1);
        }

        log("Listening to the monitor's requests");
        new Thread(gate.listensToTheMonitor()).run();

        gate.acceptsGeneratorCarStreams(); // runs forever as a main thread
    }
}
