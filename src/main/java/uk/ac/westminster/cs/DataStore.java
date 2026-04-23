package uk.ac.westminster.cs;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
}
