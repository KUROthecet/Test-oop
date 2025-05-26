package application.service;

import application.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class ChatbotService {
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private List<Product> productData;

    public ChatbotService() {
        this.geminiService = new GeminiService();
        this.objectMapper = new ObjectMapper();
        loadProductData();
    }

    private void loadProductData() {
        try {
            // Tạm thời sử dụng ProductClient để lấy dữ liệu
            // Sau này bạn có thể thay thế bằng việc đọc từ file JSON
            this.productData = ProductClient.fetchProducts();
        } catch (Exception e) {
            e.printStackTrace();
            this.productData = List.of(); // Empty list nếu không load được
        }
    }

    public String processUserMessage(String userMessage) {
        try {
            // Tạo context từ dữ liệu sản phẩm
            String productContext = createProductContext(userMessage);

            // Gọi Gemini API
            return geminiService.generateResponse(userMessage, productContext);

        } catch (Exception e) {
            e.printStackTrace();
            return handleFallbackResponse(userMessage);
        }
    }

    private String createProductContext(String userMessage) {
        if (productData == null || productData.isEmpty()) {
            return "Không có dữ liệu sản phẩm";
        }

        StringBuilder context = new StringBuilder();

        // Lọc sản phẩm liên quan dựa trên từ khóa trong tin nhắn
        String lowerMessage = userMessage.toLowerCase();

        for (Product product : productData) {
            if (isProductRelevant(product, lowerMessage)) {
                context.append(formatProductInfo(product)).append("\n\n");
            }
        }

        // Nếu không tìm thấy sản phẩm liên quan, trả về thông tin tổng quát
        if (context.length() == 0) {
            context.append("Danh sách một số sản phẩm:\n");
            int count = 0;
            for (Product product : productData) {
                if (count >= 5) break; // Giới hạn 5 sản phẩm
                context.append(formatProductInfo(product)).append("\n\n");
                count++;
            }
        }

        return context.toString();
    }

    private boolean isProductRelevant(Product product, String lowerMessage) {
        if (product.brand != null && lowerMessage.contains(product.brand.toLowerCase())) return true;
        if (product.model != null && lowerMessage.contains(product.model.toLowerCase())) return true;
        if (product.cpu != null && lowerMessage.contains(product.cpu.toLowerCase())) return true;
        if (product.gpu != null && lowerMessage.contains(product.gpu.toLowerCase())) return true;
        if (product.ram != null && lowerMessage.contains(product.ram.toLowerCase())) return true;

        // Kiểm tra từ khóa chung
        String[] keywords = {"laptop", "máy tính", "gaming", "văn phòng", "học tập", "giá rẻ", "cao cấp"};
        for (String keyword : keywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private String formatProductInfo(Product product) {
        StringBuilder info = new StringBuilder();
        info.append("Sản phẩm: ").append(product.model != null ? product.model : "N/A").append("\n");
        info.append("Thương hiệu: ").append(product.brand != null ? product.brand : "N/A").append("\n");
        info.append("Giá: ").append(product.price != null ? product.price : "Liên hệ").append("\n");
        info.append("CPU: ").append(product.cpu != null ? product.cpu : "N/A").append("\n");
        info.append("RAM: ").append(product.ram != null ? product.ram : "N/A").append("\n");
        info.append("GPU: ").append(product.gpu != null ? product.gpu : "N/A").append("\n");
        info.append("Màn hình: ").append(product.displaySize != null ? product.displaySize : "N/A").append("\n");
        info.append("Đánh giá: ").append(product.rate != null ? product.rate + "/5" : "N/A");

        return info.toString();
    }

    private String handleFallbackResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("xin chào") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
            return "Xin chào! Tôi là trợ lý ảo của Seventeen's Store. Tôi có thể giúp bạn tìm hiểu về các sản phẩm laptop. Bạn muốn tìm laptop như thế nào?";
        }

        if (lowerMessage.contains("giá") || lowerMessage.contains("price")) {
            return "Tôi có thể giúp bạn tìm laptop theo mức giá mong muốn. Bạn có thể cho tôi biết ngân sách của bạn không?";
        }

        if (lowerMessage.contains("gaming") || lowerMessage.contains("game")) {
            return "Chúng tôi có nhiều laptop gaming với cấu hình mạnh mẽ. Bạn quan tâm đến laptop gaming của thương hiệu nào?";
        }

        if (lowerMessage.contains("văn phòng") || lowerMessage.contains("học tập") || lowerMessage.contains("office")) {
            return "Chúng tôi có nhiều laptop phù hợp cho công việc văn phòng và học tập với giá cả hợp lý. Bạn cần laptop với cấu hình như thế nào?";
        }

        return "Xin lỗi, tôi chưa hiểu câu hỏi của bạn. Bạn có thể hỏi tôi về thông tin sản phẩm, giá cả, hoặc tư vấn chọn laptop phù hợp không?";
    }
}