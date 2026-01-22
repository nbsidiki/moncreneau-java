package com.moncreneau.resources;

import com.moncreneau.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Departments {
    private final HttpClient http;

    public Departments(HttpClient http) {
        this.http = http;
    }

    /**
     * List all departments
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> list() throws IOException {
        return http.get("/departments", List.class, null);
    }

    /**
     * Retrieve a department by ID
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> retrieve(String id) throws IOException {
        return http.get("/departments/" + id, Map.class, null);
    }

    /**
     * Get availability for a department
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAvailability(String id, Map<String, String> params) throws IOException {
        return http.get("/departments/" + id + "/availability", Map.class, params);
    }
}
