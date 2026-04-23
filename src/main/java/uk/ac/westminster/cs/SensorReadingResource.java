package uk.ac.westminster.cs;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        List<SensorReading> history = DataStore.READINGS.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = DataStore.SENSORS.get(sensorId);

        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found.\"}")
                    .build();
        }

        parentSensor.setCurrentValue(reading.getValue());

        DataStore.READINGS.putIfAbsent(sensorId, new ArrayList<>());
        DataStore.READINGS.get(sensorId).add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}