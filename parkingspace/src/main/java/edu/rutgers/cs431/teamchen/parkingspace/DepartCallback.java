package edu.rutgers.cs431.teamchen.parkingspace;

import edu.rutgers.cs431.TrafficGeneratorProto;

public interface DepartCallback {
    public void depart(TrafficGeneratorProto.Car car);
}
