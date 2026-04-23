package uk.ac.westminster.cs;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor) {
        Room targetRoom = DataStore.ROOMS.get(newSensor.getRoomId());

        if (targetRoom == null) {
            return Response.status(422).entity("{\"error\": \"Room ID does not exist.\"}").build();
        }

        DataStore.SENSORS.put(newSensor.getId(), newSensor);
        targetRoom.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.SENSORS.values());

        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        List<Sensor> filteredSensors = allSensors.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        return Response.ok(filteredSensors).build();
    }
}
