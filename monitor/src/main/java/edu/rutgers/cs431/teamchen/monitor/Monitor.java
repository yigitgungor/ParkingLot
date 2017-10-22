package edu.rutgers.cs431.teamchen.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
	   
	   ArrayList<Integer> ports = new ArrayList<Integer>();
	   ports.add(0000);
	   ports.add(0000);
	   ports.add(0000);
	   ports.add(0000);
	   ports.add(0000);
	   
	   for(int port : ports) 
	   {
		   try {
			sendGetRequest(port).GetStatusString();
		   }
		   catch (Exception e) {
			e.printStackTrace();
	       }
	   }
       System.out.println("______________________________________");
    }
    
    public String gateResponse() 
    {
    	return "\nGate: x\nCars waiting: x\nAverage wait time: x\nTokens: x";
    }
    
    public GateResponse sendGetRequest(int port) throws Exception {
   	 
      String USER_AGENT = "Mozilla/5.0";
  	  String urlString = "http://localhost:"+port;
  	  
  	  URL url = new URL(urlString);
  	  HttpURLConnection con = (HttpURLConnection) url.openConnection();
  	 
  	  // By default it is GET request
  	  con.setRequestMethod("GET");
  	 
  	  //add request header
  	  con.setRequestProperty("User-Agent", USER_AGENT);
  	 
  	  int responseCode = con.getResponseCode();
  	  System.out.println("Sending get request : "+ url);
  	  System.out.println("Response code : "+ responseCode);
  	 
  	  // Reading response from input Stream
  	  BufferedReader in = new BufferedReader(
  	          new InputStreamReader(con.getInputStream()));
  	  String output;
  	  StringBuffer response = new StringBuffer();
  	 
  	  while ((output = in.readLine()) != null) {
  	   response.append(output);
  	  }
  	  in.close();
  	  
  	  JSONObject obj = new JSONObject(response.toString());
  	  GateResponse gateResponse = new GateResponse(obj.getString("GateName"),obj.getString("Waiting"),obj.getString("AverageWaitTime"),obj.getString("Tokens"));	 

  	  return gateResponse;
  	 
   }
}
class GateResponse
{
	String GateName;
	String Waiting;
	String AverageWaitTime;
	String Tokens;
	
	public GateResponse(String GateName, String Waiting, String AverageWaitTime, String Tokens) 
	{
		this.GateName = GateName;
		this.Waiting = Waiting;
		this.AverageWaitTime = AverageWaitTime;
		this.Tokens = Tokens;		
	}	
	public String GetStatusString()
	{
		return "\nGate: "+GateName+"\nCars waiting: "+Waiting+"\nAverage wait time: "+AverageWaitTime+"\nTokens: "+Tokens;		
	}
}