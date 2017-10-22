package edu.rutgers.cs431.teamchen.parkingspace;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;

import java.util.ArrayList;
import java.util.Queue;

public class ParkingSpace {

	
	private final Queue<Car> psQueue = (Queue<Car>) new ArrayList<Car>();
	private long currentSize = 0L;
	
	
	public void enqueueCar(Queue<Car> pSQueue, Car c){
		this.psQueue.add(c);
		currentSize++;
	}
	
	public void dequeueCar(Queue<Car> pSQueue, DepartureQueue departQueue ){
		departQueue.depart(this.psQueue.remove());
		currentSize--;
	}
	
    // TODO:
    public DepartCallback onCarWantsDepart(){
        return null;
    }
    
    public void http() {
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress("localhost", this.gateHttpPort), MAXIMUM_HTTP_CONNECTION_CAPACITY/**/);
        } catch (IOException e) {
            reportError("Unable to create the http service for parking space: " + e.getMessage());
            System.exit(1);
        }
        httpServer.createContext("/car_entering", new CarEnteringHttpHandler(this));
        httpServer.start();
    }
}
