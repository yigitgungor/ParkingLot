package edu.rutgers.cs431.teamchen;

public class ParkingSpace {

	
	private final Queue<Car> pSQueue = new Queue<Car>;
	private long currentSize = 0L;
	
	
	public void enqueueCar(Queue<Car> pSQueue, Car c){
		psQueue.enqueue(c);
		currentSize++;
	}
	
	public void dequeueCar(Queue<Car> pSQueue, DepartureQueue departQueue ){
		departQueue.depart(psQueue.dequeue());
		currentSize--;
	}
	
    // TODO:
    public DepartCallback onCarWantsDepart(){
        return null;
    }
}
