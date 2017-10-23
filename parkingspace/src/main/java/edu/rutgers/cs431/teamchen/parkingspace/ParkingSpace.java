package edu.rutgers.cs431.teamchen.parkingspace;

import edu.rutgers.cs431.TrafficGeneratorProto.Car;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;
import edu.rutgers.cs431.teamchen.util.SyncClock;

import java.util.ArrayList;
import java.util.Queue;


public class ParkingSpace {
	
	private final URL parkingSpace;
	private final Queue<CarWithToken> psQueue;
	public DepartureQueue departQueue;
	private long currentSize;
	private final int parkingSpaceHttpPort;
	private SyncClock clock;
	private static final String SEND_CAR_PATH = "/car_entering";
	private static final int MAXIMUM_HTTP_CONNECTION_CAPACITY = 200;

	 public ParkingSpace(int httpPort) {
	        this.parkingSpaceHttpPort = httpPort;
	        this.psQueue = (Queue<CarWithToken>) new ArrayList<CarWithToken>();
	        this.currentSize = 0L;
	    }

	public CarWithTocken carReciever(){
		 HttpURLConnection conn = (HttpURLConnection) new URL(this.parkingSpace, SEND_CAR_PATH).openConnection();
	        conn.setDoOutput(true); // For POST requests
	        conn.setRequestMethod("POST");
	        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
	        Gson gson = new Gson();
	        CarWithToken cwt = gson.fromJson(reader,CarWithToken.class);
	        reader.flush();
	        reader.close();
	        conn.disconnect();
	        return cwt;
	}
	
	public void enqueueCar(Queue<CarWithToken> pSQueue, CarWithToken c){
		this.psQueue.add(c);
		currentSize++;
	}
	
	public void dequeueCar(Queue<CarWithToken> pSQueue){
		CarWithToken cwt = this.psQueue.remove();
		//Send cwt to rand gate
		
		currentSize--;
	}
	
    
    public void http() {
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress("localhost", this.parkingSpaceHttpPort), MAXIMUM_HTTP_CONNECTION_CAPACITY/**/);
        } catch (IOException e) {
            reportError("Unable to create the http service for parking space: " + e.getMessage());
            System.exit(1);
        }
        httpServer.createContext("/car_entering", new CarEnteringHttpHandler(this));
        httpServer.start();
    }
    
    public void run(){
    	this.http();
    }
    private Runnable acceptsGeneratorCarStreams(ServerSocket parkingSpaceSocket) {
    	final ParkingSpace ps = this;
        return () -> {
            while (true) {
                try (Socket carStream = parkingSpaceSocekt.accept()) {
                    // handles the car stream on a thread for each new traffic generator
                    new Thread(gate.makeCarStreamHandler(carStream)).start();
                } catch (Exception e) {
                    reportError("accepting a new car stream: " + e.getMessage());
                }
            }
        };
    }
    private Runnable makeCarStreamHandler(Socket incomingCarSocket) {
        final ParkingSpace ps = this;
        return () -> {
            while (true) {
                try {
                    CarWithToken car = CarWithToken.parseDelimitedFrom(incomingCarSocket.getInputStream());
                    ps.enqueueCar(car);
                } catch (IOException e) {
                    reportError("cannot receive car from gate: " + e.getMessage());
                    return;
                }
            }
        };
    }
    
    public Queue<CarWithToken> checkDeparture(Queue<CarWithToken> psQueue, Socket trafficGenSocket){
    	if(psQueue.isEmpty()){
    		return psQueue;
    	}
    	while(true){
    		this.clock = new SyncClock(trafficGenSocket);
    		CarWithToken c = psQueue.peek();
    		if(c.getDepartureTimestamp < clock.currentTime){
    			dequeueCar(psQueue); 
    		}else{
    			break;
    		}
    			
    	}
    }
    
    public static void main(String[] args){
    	run();
    	
    	
    }
    }
}
