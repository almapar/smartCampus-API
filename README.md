# Smart Campus RESTful API

Student Name: Alexandra-Maria Paraschiv

Student ID: w2077760

Module: 5COSC022W

## Smart Campus REST API Overview

A reliable, scalable RESTful web service, the Smart Campus API was created to handle university infrastructure, such as rooms, sensors, and their past readings. The program, which was developed with Java and the JAX-RS framework, uses a lightweight embedded server to process client requests. The API maintains all resource entities, including library study rooms, CO2 monitors, and temperature measures, in static, in-memory collections like ConcurrentHashMaps to satisfy project limitations and guarantee thread safety without a conventional database. By using Sub-Resource Locators for nested historical data, dynamic query filtering, and a thorough global exception-handling mechanism to stop data leaks and preserve server stability, the architecture closely adheres to REST concepts.

## Build and Launch Instructions

Follow these steps to deploy and run the Smart Campus API locally:

- Clone the project repository from GitHub to your local machine.

- Open the project folder in your preferred Java IDE.

- Ensure that Maven is fully synced so that all dependencies, specifically the JAX-RS implementation and the embedded server, are downloaded.

- Build the project using Maven (e.g., running mvn clean install).

- Navigate to your main application entry point and run the main method to launch the server. The API will be securely hosted and accessible starting at the base URI http://localhost:8080/api/v1.

## Sample API Interactions (cURL Commands)

Below are five sample curl commands demonstrating how to interact with the core endpoints of the API:

1. Create a new Room:

curl -X POST -H "Content-Type: application/json" -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50,"sensorIds":[]}' http://localhost:8080/api/v1/rooms 

2. Retrieve all Rooms:

curl -X GET http://localhost:8080/api/v1/rooms 

3. Register a Sensor to a Room:

curl -X POST -H "Content-Type: application/json" -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}' http://localhost:8080/api/v1/sensors 

4. Filter Sensors by Type:

curl -X GET http://localhost:8080/api/v1/sensors?type=Temperature 

5. Post a Historical Reading (Sub-Resource):

curl -X POST -H "Content-Type: application/json" -d '{"value":22.5}' http://localhost:8080/api/v1/sensors/TEMP-001/readings

## Coursework Questions & Answers
### Part 1: JAX-RS Lifecycle & Data Synchronization
For each incoming HTTP request, the JAX-RS runtime by default creates a new instance of a Resource class. The class is not handled as a singleton. Any standard instance variables defined within the class will be lost as soon as the request is processed due to this architectural choice. The underlying in-memory data structures that map the rooms and sensors must be declared statically so that they remain globally across all instances of temporary resources in order to prevent data loss. Additionally, these collections need to be extensively synchronised (using thread-safe structures like ConcurrentHashMap) to avoid race situations and deadly data corruption because numerous client requests may try to read and write at the same time.

### Part 1: The Importance of Hypermedia
Providing Hypermedia is considered a hallmark of advanced RESTful design because it allows the API to be dynamically discoverable. Instead of forcing client developers to constantly refer to static documentation to figure out what endpoints exist, the server automatically embeds functional navigation links directly into its responses. This greatly benefits client applications, as they can adapt to server-side URL changes seamlessly without breaking, creating a much more resilient client-server architecture.

### Part 2: ID Lists vs. Full Object Payloads
When returning a collection of rooms, sending only the room IDs drastically minimises the JSON payload size, preserving network bandwidth. However, this offloads the processing burden onto the client, which must execute subsequent GET requests for every single ID just to display basic details. Conversely, returning the full room objects consumes slightly more bandwidth initially, but heavily reduces client-side processing by delivering all necessary display data in one efficient round-trip.

### Part 2: DELETE Operation Idempotency
Yes, the DELETE operation in this system is fundamentally idempotent. If a client mistakenly transmits the exact same DELETE request for a specific room multiple times, the first request will successfully locate the room, execute the deletion, and return a successful status code. Any subsequent, identical requests will search for the room, realise it no longer exists, and safely return an error (such as a 404 Not Found), entirely bypassing any further modifications to the server's state.

### Part 3: Media Type Mismatches
When a POST method is explicitly annotated with @Consumes(MediaType.APPLICATION_JSON), it creates a strict contract. If a client violates this by attempting to send text/plain or application/xml, JAX-RS acts as a protective shield and automatically rejects the payload before it even reaches the business logic. The framework handles the mismatch by immediately returning an HTTP 415 Unsupported Media Type status code directly to the client.

### Part 3: Query Parameters vs. Path Parameters
Using @QueryParam for searching and filtering is considered architecturally superior because query strings modify how a collection is presented without altering the hierarchical identity of the resource itself. Path parameters (e.g., /sensors/type/CO2) imply that "type" is a definitive structural folder, whereas query parameters (e.g., ?type=CO2) act as optional, stackable filters applied to a broader collection, resulting in much cleaner and more flexible URL structures.

### Part 4: Sub-Resource Locator Pattern
The Sub-Resource Locator pattern offers massive architectural benefits by allowing a parent resource to cleanly delegate requests for nested endpoints to entirely separate Java classes. In a large API, handling deeply nested paths (like /sensors/{id}/readings/{rid}) inside a single controller class quickly results in bloated, unmanageable code. By delegating this logic, the application remains modular, tightly focused, and vastly easier to test and maintain.

### Part 5: HTTP 422 vs. HTTP 404
When a client sends a structurally perfect JSON payload that contains a logical error—such as referencing a roomId that does not actually exist in the system—HTTP 422 Unprocessable Entity is semantically superior to a 404 Not Found. A 404 traditionally indicates that the target endpoint URI itself is invalid or missing. A 422 clearly communicates that the server found the correct endpoint and understood the JSON formatting, but had to reject the request due to a failed business validation.

### Part 5: The Dangers of Stack Traces
From a cybersecurity perspective, allowing an API to leak raw Java stack traces is incredibly dangerous because it acts as a blueprint for attackers. These traces reveal explicit details about the server's internal architecture, the frameworks being utilised, specific library version numbers, and internal file paths. An attacker can easily cross-reference these precise version numbers against known CVE databases to craft highly targeted, devastating exploits.

### Part 5: Advantages of Request Filters
Utilising JAX-RS filters for cross-cutting concerns like logging is vastly superior to manually writing Logger.info() inside every controller. Filters act as universal interceptors, ensuring that every single incoming request and outgoing response is perfectly and consistently logged in one centralised location. This prevents code duplication, reduces developer error, and keeps the actual resource methods entirely focused on executing their core business logic.