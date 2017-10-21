package edu.rutgers.cs431.teamchen.monitor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Monitor {
	
	public static void main(String[] args)
	{		
		System.out.println("Parking Lot edu.rutgers.cs431.teamchen.monitor.Monitor");
		System.out.println("______________________________________");
		
		Timer timer = new Timer();
		timer.schedule(new GetUpdates(), 0, 15000);
	}

}
class GetUpdates extends TimerTask {
    public void run() {
	   System.out.println(new Date().getTime() + "\n");
	   System.out.println("Parking Lot Fullness: x%");
	   //for each gate
	   for(int i = 0; i < 6; i++)
	   {
       System.out.println(gateResponse());
       }
       System.out.println("______________________________________");
    }
    
    public String gateResponse() 
    {
    	return "\nGate: x\nCars waiting: x\nAverage wait time: x\nTokens: x";
    }
    
}