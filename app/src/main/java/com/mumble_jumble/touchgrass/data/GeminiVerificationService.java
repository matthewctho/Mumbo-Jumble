package com.mumble_jumble.touchgrass.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.mumble_jumble.touchgrass.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Calls the Gemini API to check whether a submitted photo actually matches
 * what a task asked for (e.g. "summit or lookout photo").
 *
 * This runs a network call, so it must NOT be called on the main thread —
 * this class handles that internally (runs on a background thread, then
 * delivers the result back on the main thread via the callback).
 */
public class GeminiVerificationService {

    private static final String TAG = "GeminiVerification";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                    + BuildConfig.GEMINI_API_KEY;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public interface VerificationCallback {
        /** approved = true/false from the AI. reason is a short human-readable explanation. */
        void onResult(boolean approved, String reason);
        /** Called on network failure, bad response, timeout, etc. Treat as "pending review", not rejection. */
        void onError(Exception e);
    }

    /**
     * @param imageBytes   raw JPEG bytes of the photo (from Storage upload or straight from camera)
     * @param taskName     e.g. "Summit or lookout photo"
     * @param taskType     "scenery_photo" (kept for future task-type-specific prompts)
     */
    public void verifyPhoto(byte[] imageBytes, String taskName, String taskType, VerificationCallback callback) {
        if (BuildConfig.GEMINI_API_KEY == null || BuildConfig.GEMINI_API_KEY.isEmpty()) {
            callback.onError(new Exception("Gemini API key is missing. Please add GEMINI_API_KEY to your local.properties file."));
            return;
        }
        executor.execute(() -> {
            try {
                String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                JSONObject requestBody = buildRequestBody(base64Image, taskName);

                HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.toString().getBytes("UTF-8"));
                }

                int responseCode = connection.getResponseCode();
                String responseBody = readStream(
                        responseCode >= 200 && responseCode < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream());

                if (responseCode < 200 || responseCode >= 300) {
                    throw new IOException("Gemini API error " + responseCode + ": " + responseBody);
                }

                boolean[] approvedHolder = new boolean[1];
                String[] reasonHolder = new String[1];
                parseResponse(responseBody, approvedHolder, reasonHolder);

                mainThread.post(() -> callback.onResult(approvedHolder[0], reasonHolder[0]));

            } catch (Exception e) {
                Log.e(TAG, "Verification failed", e);
                mainThread.post(() -> callback.onError(e));
            }
        });
    }

    private JSONObject buildRequestBody(String base64Image, String taskName) throws Exception {
        String prompt = "You are verifying a photo submitted for a scavenger-hunt style app task called: \""
                + taskName + "\". Look at the image and decide if it genuinely matches what the task asks for. "
                + "Respond with ONLY a JSON object, no other text, in this exact format: "
                + "{\"approved\": true or false, \"reason\": \"one short sentence\"}";

        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONObject inlineData = new JSONObject();
        inlineData.put("mime_type", "image/jpeg");
        inlineData.put("data", base64Image);

        JSONObject imagePart = new JSONObject();
        imagePart.put("inline_data", inlineData);

        JSONArray parts = new JSONArray();
        parts.put(textPart);
        parts.put(imagePart);

        JSONObject content = new JSONObject();
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);

        JSONObject body = new JSONObject();
        body.put("contents", contents);
        return body;
    }

    private void parseResponse(String responseBody, boolean[] approvedOut, String[] reasonOut) throws Exception {
        JSONObject root = new JSONObject(responseBody);
        String rawText = root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        // Gemini sometimes wraps JSON in ```json ... ``` — strip that if present
        String cleaned = rawText.trim()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();

        JSONObject verdict = new JSONObject(cleaned);
        approvedOut[0] = verdict.optBoolean("approved", false);
        reasonOut[0] = verdict.optString("reason", "");
    }

    private String readStream(java.io.InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}