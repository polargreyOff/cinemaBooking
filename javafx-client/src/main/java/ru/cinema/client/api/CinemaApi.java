package ru.cinema.client.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.cinema.client.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CinemaApi {
    private final URI base;
    private final BasicCookieStore cookies = new BasicCookieStore();
    private final CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookies).build();
    private final Gson gson = new Gson();

    public CinemaApi(String url) {
        String normalized = url.strip().replaceAll("/+$", "");
        if (!normalized.matches("https?://[^/]+(/api)?")) throw new IllegalArgumentException("Адрес должен иметь вид http://127.0.0.1:8080/api");
        base = URI.create(normalized.endsWith("/api") ? normalized : normalized + "/api");
    }

    public String login(String login, String password) throws IOException {
        JsonObject body = new JsonObject();
        body.addProperty("login", login);
        body.addProperty("password", password);
        JsonObject user = JsonParser.parseString(request("POST", "/auth/login", body)).getAsJsonObject();
        request("GET", "/auth/me", null);
        return user.has("role") ? user.get("role").getAsString() : "USER";
    }
    public List<Movie> movies() throws IOException {
        return gson.fromJson(request("GET", "/movies", null), new TypeToken<List<Movie>>() {}.getType());
    }
    public List<Hall> halls() throws IOException {
        return gson.fromJson(request("GET", "/halls", null), new TypeToken<List<Hall>>() {}.getType());
    }
    public Hall saveHall(Long id, int number) throws IOException {
        String path = id == null ? "/halls" : "/halls/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, Map.of("number", number)), Hall.class);
    }
    public void deleteHall(long id) throws IOException { request("DELETE", "/halls/" + id, null); }
    public SessionPage sessions() throws IOException {
        List<Session> all = new ArrayList<>();
        int page = 0, pages;
        do {
            SessionPage response = gson.fromJson(request("GET", "/sessions?page=" + page + "&size=100&sort=startTime&direction=asc", null), SessionPage.class);
            all.addAll(response.content()); pages = response.totalPages(); page++;
        } while (page < pages);
        return new SessionPage(all, 0, all.size(), all.size(), 1);
    }
    public Movie saveMovie(Long id, Map<String, Object> values) throws IOException {
        String path = id == null ? "/movies" : "/movies/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, values), Movie.class);
    }
    public void deleteMovie(long id) throws IOException { request("DELETE", "/movies/" + id, null); }
    public Session saveSession(Long id, Map<String, Object> values) throws IOException {
        String path = id == null ? "/sessions" : "/sessions/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, values), Session.class);
    }
    public void deleteSession(long id) throws IOException { request("DELETE", "/sessions/" + id, null); }

    public List<SessionSeat> sessionSeats(long sessionId) throws IOException {
        return gson.fromJson(request("GET", "/sessions/" + sessionId + "/seats", null),
                new TypeToken<List<SessionSeat>>() {}.getType());
    }
    public List<Booking> myBookings() throws IOException {
        return gson.fromJson(request("GET", "/bookings/my", null),
                new TypeToken<List<Booking>>() {}.getType());
    }
    public Booking createBooking(long sessionId, long seatId) throws IOException {
        return gson.fromJson(request("POST", "/bookings", Map.of("sessionId", sessionId, "seatId", seatId)), Booking.class);
    }
    public void cancelBooking(long id) throws IOException { request("DELETE", "/bookings/" + id, null); }
    private record ApiResponse(int status, String body) {}
    private String request(String method, String path, Object body) throws IOException {
        URI uri = base.resolve(base.getPath() + path);
        HttpUriRequestBase call = new HttpUriRequestBase(method, uri);
        call.addHeader("Accept", "application/json");
        if (body != null) call.setEntity(new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON));
        if (!method.equals("GET")) {
            String token = cookies.getCookies().stream()
                    .filter(c -> c.getName().equals("XSRF-TOKEN")).map(c -> c.getValue()).findFirst().orElse(null);
            if (token != null) call.addHeader("X-XSRF-TOKEN", URLDecoder.decode(token.replace("+", "%2B"), StandardCharsets.UTF_8));
        }
        ApiResponse response = client.execute(call, raw -> new ApiResponse(raw.getCode(),
                raw.getEntity() == null ? "" : EntityUtils.toString(raw.getEntity(), StandardCharsets.UTF_8)));
        if (response.status() < 200 || response.status() >= 300) {
            String detail = "HTTP " + response.status();
            try {
                JsonObject error = JsonParser.parseString(response.body()).getAsJsonObject();
                for (String key : List.of("detail", "message", "title")) {
                    if (error.has(key) && !error.get(key).isJsonNull()) { detail += ": " + error.get(key).getAsString(); break; }
                }
            } catch (RuntimeException ignored) { /* Some errors have an empty body. */ }
            if (response.status() == 403) detail += " — требуется вход администратора или CSRF-токен";
            throw new IOException(detail);
        }
        return response.body();
    }
}


