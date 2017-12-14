package edu.rutgers.cs431.teamchen.util;

import edu.rutgers.cs431.TrafficGeneratorProto;
import edu.rutgers.cs431.teamchen.proto.CarWithToken;

import java.text.SimpleDateFormat;

public class DataFormatter {
    private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss z");

    public static String format(long time) {
        return timeFormatter.format(time);
    }

    public static String format(TrafficGeneratorProto.Car car) {
        return "Car(" + format(car.getArrivalTimestamp()) + "->" + format(car.getDepartureTimestamp()) + ")";
    }

    public static String format(CarWithToken cwt) {
        TrafficGeneratorProto.Car car = cwt.getCarProto();
        return "Car(" + format(car.getArrivalTimestamp()) + "->" + format(car.getDepartureTimestamp()) + ", token " +
                cwt.token + ")";

    }
}
