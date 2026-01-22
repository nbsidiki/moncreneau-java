package com.moncreneau;

import com.moncreneau.resources.Appointments;
import com.moncreneau.resources.Departments;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Moncreneau API client
 * 
 * Example:
 * <pre>
 * Moncreneau client = new Moncreneau("mk_live_YOUR_API_KEY");
 * Map&lt;String, Object&gt; appointment = client.appointments.create(data);
 * </pre>
 */
public class Moncreneau {
    public final Appointments appointments;
    public final Departments departments;

    /**
     * Initialize Moncreneau client
     *
     * @param apiKey Your Moncreneau API key (mk_test_... or mk_live_...)
     */
    public Moncreneau(String apiKey) {
        this(apiKey, "https://mc.duckdns.org/api/v1", 30);
    }

    /**
     * Initialize Moncreneau client with custom configuration
     *
     * @param apiKey  Your Moncreneau API key
     * @param baseUrl API base URL
     * @param timeout Request timeout in seconds
     */
    public Moncreneau(String apiKey, String baseUrl, int timeout) {
        HttpClient http = new HttpClient(apiKey, baseUrl, timeout);
        
        this.appointments = new Appointments(http);
        this.departments = new Departments(http);
    }

    /**
     * Verify webhook signature using HMAC-SHA256
     *
     * @param payload   Webhook payload (JSON string)
     * @param signature Signature from X-Webhook-Signature header
     * @param secret    Your webhook secret
     * @return true if signature is valid, false otherwise
     */
    public static boolean verifyWebhookSignature(String payload, String signature, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            
            byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(hash);
            
            return computed.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
