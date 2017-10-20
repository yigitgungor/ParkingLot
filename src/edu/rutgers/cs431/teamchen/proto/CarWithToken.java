package edu.rutgers.cs431.teamchen.proto;

import edu.rutgers.cs431.TrafficGeneratorProto;


// For communication between Gate and ParkingSpace
// Gate -> ParkingSpace: The Gate sends this object to the Parking Space and expects no response.
// ParkingSpace -> Gate: When a car departs, the ParkingSpace sends this back to return the token, and expects no response
public class CarWithToken {
    public long arrivalTimestamp;
    public long departureTimestamp;
    public String token;

    public TrafficGeneratorProto.Car getCarProto(){
        return TrafficGeneratorProto.Car.newBuilder()
                .setArrivalTimestamp(this.arrivalTimestamp)
                .setDepartureTimestamp(this.departureTimestamp)
                .build();
    }

    public CarWithToken(TrafficGeneratorProto.Car car, String token){
        this.arrivalTimestamp = car.getArrivalTimestamp();
        this.departureTimestamp = car.getDepartureTimestamp();
        this.token = token;
    }

}
