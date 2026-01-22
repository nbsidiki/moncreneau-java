package com.moncreneau;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moncreneau.exceptions.MoncreneauException;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private final OkHttpClient client;
    private final String baseUrl;
    private final String apiKey;
    private final Gson gson;

    public HttpClient(String apiKey, String baseUrl, int timeout) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.replaceAll("/$", "");
        this.gson = new Gson();

        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    public <T> T get(String path, Class<T> responseType, Map<String, String> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "moncreneau-java/1.0.0")
                .build();

        return executeRequest(request, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) throws IOException {
        String json = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("X-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "moncreneau-java/1.0.0")
                .post(requestBody)
                .build();

        return executeRequest(request, responseType);
    }

    public void delete(String path) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("X-API-Key", apiKey)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() != 204 && !response.isSuccessful()) {
                handleError(response);
            }
        }
    }

    private <T> T executeRequest(Request request, Class<T> responseType) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();

            if (!response.isSuccessful()) {
                handleError(response, body);
            }

            return gson.fromJson(body, responseType);
        }
    }

    private void handleError(Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            if (body != null) {
                handleError(response, body.string());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleError(Response response, String body) {
        try {
            TypeToken<Map<String, Object>> mapType = new TypeToken<Map<String, Object>>() {
            };
            Map<String, Object> errorResponse = gson.fromJson(body, mapType.getType());

            Object errorObj = errorResponse.get("error");
            Map<String, Object> error;

            if (errorObj instanceof Map) {
                error = (Map<String, Object>) errorObj;
            } else {
                error = createSimpleError();
            }

            throw new MoncreneauException(error, response.code());
        } catch (MoncreneauException e) {
            throw e;
        } catch (Exception e) {
            Map<String, Object> error = createSimpleError();
            throw new MoncreneauException(error, response.code());
        }
    }

    private Map<String, Object> createSimpleError() {
        Map<String, Object> error = new HashMap<>();
        error.put("code", "UNKNOWN_ERROR");
        error.put("message", "An error occurred");
        return error;
    }
}
