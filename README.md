# Moncreneau Java SDK

Official Moncreneau API client for Java.

[![Maven Central](https://img.shields.io/maven-central/v/com.moncreneau/moncreneau-java.svg)](https://central.sonatype.com/artifact/com.moncreneau/moncreneau-java)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.moncreneau</groupId>
    <artifactId>moncreneau-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.moncreneau:moncreneau-java:1.0.0'
```

## Quick Start

```java
import com.moncreneau.Moncreneau;
import java.util.Map;
import java.util.HashMap;

Moncreneau client = new Moncreneau("mk_live_YOUR_API_KEY");

// Create an appointment
Map<String, Object> data = new HashMap<>();
data.put("departmentId", "dept_123");
data.put("dateTime", "2026-01-20T10:00:00");
data.put("userName", "Jean Dupont");
data.put("userPhone", "+224621234567");

Map<String, Object> appointment = client.appointments.create(data);
System.out.println(appointment.get("id")); // appt_abc123
```

## Documentation

Full documentation: [https://moncreneau-docs.vercel.app/docs/v1/sdks/java](https://moncreneau-docs.vercel.app/docs/v1/sdks/java)

## Features

- ✅ Java 8+ support
- ✅ OkHttp client
- ✅ Gson JSON parsing
- ✅ Full error handling
- ✅ Webhook verification

## Usage

### Configuration

```java
// Default configuration
Moncreneau client = new Moncreneau("mk_live_...");

// Custom configuration
Moncreneau client = new Moncreneau(
    "mk_live_...",
    "https://mc-prd.duckdns.org/api/v1",
    30 // timeout in seconds
);
```

### Appointments

```java
import java.util.Map;
import java.util.HashMap;

// Create
Map<String, Object> data = new HashMap<>();
data.put("departmentId", "dept_123");
data.put("dateTime", "2026-01-20T10:00:00");
data.put("userName", "Jean Dupont");
data.put("userPhone", "+224621234567");

Map<String, Object> appointment = client.appointments.create(data);

// List
Map<String, String> params = new HashMap<>();
params.put("page", "0");
params.put("size", "20");
params.put("status", "SCHEDULED");

Map<String, Object> appointments = client.appointments.list(params);

// Retrieve
Map<String, Object> appointment = client.appointments.retrieve("appt_abc123");

// Cancel
client.appointments.cancel("appt_abc123");
```

### Departments

```java
// List
List<Map<String, Object>> departments = client.departments.list();

// Get availability
Map<String, String> params = new HashMap<>();
params.put("startDate", "2026-01-20");
params.put("endDate", "2026-01-27");

Map<String, Object> availability = client.departments.getAvailability("dept_123", params);
```

### Error Handling

```java
import com.moncreneau.exceptions.MoncreneauException;

try {
    Map<String, Object> appointment = client.appointments.create(data);
} catch (MoncreneauException e) {
    System.err.println("Code: " + e.getErrorCode());
    System.err.println("Message: " + e.getMessage());
    System.err.println("Status: " + e.getStatusCode());
    System.err.println("Details: " + e.getDetails());
} catch (IOException e) {
    System.err.println("Network error: " + e.getMessage());
}
```

### Webhooks

```java
import com.moncreneau.Moncreneau;

// In your webhook endpoint
String payload = request.getBody(); // Get raw request body
String signature = request.getHeader("X-Webhook-Signature");
String secret = System.getenv("WEBHOOK_SECRET");

if (!Moncreneau.verifyWebhookSignature(payload, signature, secret)) {
    response.setStatus(401);
    return "Invalid signature";
}

// Process webhook
Map<String, Object> event = gson.fromJson(payload, Map.class);
System.out.println("Event: " + event.get("type"));
```

## Spring Boot Example

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.moncreneau.Moncreneau;
import com.moncreneau.exceptions.MoncreneauException;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    
    private final Moncreneau client;
    
    public AppointmentController() {
        this.client = new Moncreneau(System.getenv("MONCRENEAU_API_KEY"));
    }
    
    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> data) {
        try {
            Map<String, Object> appointment = client.appointments.create(data);
            return ResponseEntity.ok(appointment);
        } catch (MoncreneauException e) {
            return ResponseEntity
                .status(e.getStatusCode())
                .body(Map.of(
                    "error", e.getErrorCode(),
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity
                .status(500)
                .body(Map.of("error", "INTERNAL_ERROR", "message", e.getMessage()));
        }
    }
}
```

## Support

- **Documentation**: [https://moncreneau-docs.vercel.app](https://moncreneau-docs.vercel.app)
- **Javadoc**: [https://javadoc.io/doc/com.moncreneau/moncreneau-java](https://javadoc.io/doc/com.moncreneau/moncreneau-java)
- **Issues**: [GitHub Issues](https://github.com/nbsidiki/moncreneau-java/issues)
- **Email**: moncreneau.rdv@gmail.com

## License

MIT © Moncreneau
