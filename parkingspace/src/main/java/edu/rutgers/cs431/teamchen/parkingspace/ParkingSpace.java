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
}
