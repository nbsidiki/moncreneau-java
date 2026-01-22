package com.moncreneau.resources;

import com.moncreneau.HttpClient;

import java.io.IOException;
import java.util.Map;

public class Appointments {
    private final HttpClient http;

    public Appointments(HttpClient http) {
        this.http = http;
    }

    /**
     * Create a new appointment
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> create(Map<String, Object> data) throws IOException {
        return http.post("/appointments", data, Map.class);
    }

    /**
     * Retrieve an appointment by ID
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> retrieve(String id) throws IOException {
        return http.get("/appointments/" + id, Map.class, null);
    }

    /**
     * List appointments with pagination and filters
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> list(Map<String, String> params) throws IOException {
        return http.get("/appointments", Map.class, params);
    }

    /**
     * Cancel an appointment
     */
    public void cancel(String id) throws IOException {
        http.delete("/appointments/" + id);
    }
}
