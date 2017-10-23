package edu.rutgers.cs431.teamchen.parkingspace;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;

import java.util.ArrayList;
import java.util.Queue;

public class DepartureQueue {
	
	private final Queue<Car> exitQueue;
	
	public DepartureQueue(){
		this.exitQueue = (Queue<Car>) new ArrayList<Car>();
		
	}
	
}
