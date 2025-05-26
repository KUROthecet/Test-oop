package application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeminiService {
    private static final String API_KEY = "AIzaSyCOmuIjXZN--2VqFpbpiX1sKPKeL3U6fuk"; // Thay bằng API key thực của bạn
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponse(String userMessage, String productContext) {
        try {
            // Tạo prompt với context về sản phẩm
            String prompt = createPrompt(userMessage, productContext);

            // Tạo request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode content = objectMapper.createObjectNode();
            ObjectNode part = objectMapper.createObjectNode();

            part.put("text", prompt);
            content.set("parts", objectMapper.createArrayNode().add(part));
            requestBody.set("contents", objectMapper.createArrayNode().add(content));

            // Gửi request
            String response = sendPostRequest(requestBody.toString());

            // Parse response
            return parseGeminiResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, tôi không thể trả lời câu hỏi này lúc này. Vui lòng thử lại sau.";
        }
    }

    private String createPrompt(String userMessage, String productContext) {
        return String.format(
                "Bạn là trợ lý bán hàng máy tính của Seventeen's Store. " +
                        "Hãy trả lời câu hỏi của khách hàng một cách thân thiện và chuyên nghiệp. " +
                        "Chỉ sử dụng thông tin từ dữ liệu sản phẩm được cung cấp.\n\n" +
                        "Dữ liệu sản phẩm:\n%s\n\n" +
                        "Câu hỏi của khách hàng: %s\n\n" +
                        "Hãy trả lời ngắn gọn, rõ ràng và hữu ích:",
                productContext, userMessage
        );
    }

    private String sendPostRequest(String requestBody) throws Exception {
        URL url = new URL(GEMINI_URL + "?key=" + URLEncoder.encode(API_KEY, StandardCharsets.UTF_8.name()));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Gửi request body
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Đọc response
        int responseCode = conn.getResponseCode();
        InputStream inputStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode != 200) {
            throw new Exception("API Error: " + response.toString());
        }

        return response.toString();
    }

    private String parseGeminiResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode candidates = root.get("candidates");

        if (candidates != null && candidates.isArray() && candidates.size() > 0) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.get("content");
            if (content != null) {
                JsonNode parts = content.get("parts");
                if (parts != null && parts.isArray() && parts.size() > 0) {
                    JsonNode text = parts.get(0).get("text");
                    if (text != null) {
                        return text.asText();
                    }
                }
            }
        }

        return "Xin lỗi, tôi không thể tạo phản hồi lúc này.";
    }
}