package uk.ac.westminster.cs;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class SensorRoomResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.ROOMS.values());
        return Response.ok(roomList).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        DataStore.ROOMS.put(newRoom.getId(), newRoom);

        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }

    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);
        if (room != null) {
            return Response.ok(room).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"Cannot delete room: Sensors are still active.\"}").build();
        }

        DataStore.ROOMS.remove(roomId);
        return Response.noContent().build();
    }
}
