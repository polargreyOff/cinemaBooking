package ru.cinema.client.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import ru.cinema.client.model.Booking;
import ru.cinema.client.model.Hall;
import ru.cinema.client.model.Movie;
import ru.cinema.client.model.Session;
import ru.cinema.client.model.SessionPage;
import ru.cinema.client.model.SessionSeat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CinemaApi {
    private final ApiTransport transport;
    private final Gson gson = new Gson();

    public CinemaApi(String url) {
        transport = new ApiTransport(url);
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

    public Movie saveMovie(Long id, Map<String, Object> values) throws IOException {
        String path = id == null ? "/movies" : "/movies/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, values), Movie.class);
    }

    public void deleteMovie(long id) throws IOException {
        request("DELETE", "/movies/" + id, null);
    }

    public List<Hall> halls() throws IOException {
        return gson.fromJson(request("GET", "/halls", null), new TypeToken<List<Hall>>() {}.getType());
    }

    public Hall saveHall(Long id, int number) throws IOException {
        String path = id == null ? "/halls" : "/halls/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, Map.of("number", number)), Hall.class);
    }

    public void deleteHall(long id) throws IOException {
        request("DELETE", "/halls/" + id, null);
    }

    public SessionPage sessions() throws IOException {
        List<Session> all = new ArrayList<>();
        int page = 0;
        int pages;
        do {
            SessionPage response = gson.fromJson(
                    request("GET", "/sessions?page=" + page + "&size=100&sort=startTime&direction=asc", null),
                    SessionPage.class);
            all.addAll(response.content());
            pages = response.totalPages();
            page++;
        } while (page < pages);
        return new SessionPage(all, 0, all.size(), all.size(), 1);
    }

    public Session saveSession(Long id, Map<String, Object> values) throws IOException {
        String path = id == null ? "/sessions" : "/sessions/" + id;
        return gson.fromJson(request(id == null ? "POST" : "PATCH", path, values), Session.class);
    }

    public void deleteSession(long id) throws IOException {
        request("DELETE", "/sessions/" + id, null);
    }

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

    public void cancelBooking(long id) throws IOException {
        request("DELETE", "/bookings/" + id, null);
    }

    private String request(String method, String path, Object body) throws IOException {
        return transport.request(method, path, body == null ? null : gson.toJson(body));
    }
}
