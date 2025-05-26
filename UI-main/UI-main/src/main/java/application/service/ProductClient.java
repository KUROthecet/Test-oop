package application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import application.model.Product;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductClient {

    /**
     * Fetch tất cả sản phẩm (tương đương GET /api/building/)
     */
    public static List<Product> fetchProducts() {
        return fetchWithParams(Collections.emptyMap());
    }

    /**
     * Fetch sản phẩm với query params (tương đương GET /api/building?brand=...&cpu=...)
     */
    public static List<Product> fetchWithParams(Map<String,String> params) {
        try {
            // Xây dựng URL
        	StringBuilder sb = new StringBuilder("https://bd68-2405-4802-1f04-d0-b85f-9eac-5a8b-2abb.ngrok-free.app/api/building/");
            if (params != null && !params.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String,String> e : params.entrySet()) {
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8"))
                      .append("=")
                      .append(URLEncoder.encode(e.getValue(), "UTF-8"))
                      .append("&");
                }
                // bỏ dấu & cuối cùng
                sb.deleteCharAt(sb.length() - 1);
            }

            // Mở kết nối
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Đọc JSON về và chuyển thành List<Product>
         // Đọc JSON về và chuyển thành List<Product>
            try (InputStream in = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                List<Product> all = mapper.readValue(in, new TypeReference<List<Product>>() {});
                return all.size() > 10 ? all.subList(0, 10) : all;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
