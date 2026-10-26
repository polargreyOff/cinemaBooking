package ru.cinema.client.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class ApiTransport {
    private final URI base;
    private final BasicCookieStore cookies = new BasicCookieStore();
    private final CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookies).build();

    ApiTransport(String url) {
        String normalized = url.strip().replaceAll("/+$", "");
        if (!normalized.matches("https?://[^/]+(/api)?")) {
            throw new IllegalArgumentException("Адрес должен иметь вид http://127.0.0.1:8080/api");
        }
        base = URI.create(normalized.endsWith("/api") ? normalized : normalized + "/api");
    }

    String request(String method, String path, String jsonBody) throws IOException {
        HttpUriRequestBase call = new HttpUriRequestBase(method, base.resolve(base.getPath() + path));
        call.addHeader("Accept", "application/json");
        if (jsonBody != null) call.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        if (!method.equals("GET")) addCsrfHeader(call);

        ApiResponse response = client.execute(call, raw -> new ApiResponse(raw.getCode(),
                raw.getEntity() == null ? "" : EntityUtils.toString(raw.getEntity(), StandardCharsets.UTF_8)));
        if (response.status() < 200 || response.status() >= 300) throw new IOException(errorMessage(response));
        return response.body();
    }

    private void addCsrfHeader(HttpUriRequestBase call) {
        String token = cookies.getCookies().stream()
                .filter(cookie -> cookie.getName().equals("XSRF-TOKEN"))
                .map(cookie -> cookie.getValue()).findFirst().orElse(null);
        if (token != null) {
            call.addHeader("X-XSRF-TOKEN", URLDecoder.decode(token.replace("+", "%2B"), StandardCharsets.UTF_8));
        }
    }

    private static String errorMessage(ApiResponse response) {
        String detail = "HTTP " + response.status();
        try {
            JsonObject error = JsonParser.parseString(response.body()).getAsJsonObject();
            for (String key : List.of("detail", "message", "title")) {
                if (error.has(key) && !error.get(key).isJsonNull()) {
                    detail += ": " + error.get(key).getAsString();
                    break;
                }
            }
        } catch (RuntimeException ignored) {
            // Тело ошибки бывает пустым.
        }
        if (response.status() == 403) detail += " — требуется вход администратора или CSRF-токен";
        return detail;
    }

    private record ApiResponse(int status, String body) {}
}
