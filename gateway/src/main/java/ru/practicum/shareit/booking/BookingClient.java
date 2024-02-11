package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(long userId, BookingDtoRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getStatus(long userId, long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );

        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(long userId, String state, Integer from, Integer size, boolean isOwner) {
        String ownerPath = "";
        Map<String, Object> parameters;

        if (isOwner) {
            ownerPath = "/owner";
        }

        if (from != null && size != null) {
            parameters = Map.of("state", state,"from", from,"size", size);
        } else if (from == null && size == null) {
            parameters = Map.of("state", state);
        } else if (size == null) {
            parameters = Map.of("state", state,"from", from);
        } else {
            parameters = Map.of("state", state,"size", size);
        }

        if (parameters.containsKey("state") && parameters.containsKey("from") && parameters.containsKey("size")) {
            return get(ownerPath + "?state={state}&from={from}&size={size}", userId, parameters);
        } else if (!parameters.containsKey("from") && !parameters.containsKey("size")) {
            return get(ownerPath + "?state={state}", userId, parameters);
        } else if (!parameters.containsKey("size")) {
            return get(ownerPath + "?state={state}&from={from}", userId, parameters);
        } else {
            return get(ownerPath + "?state={state}&size={size}", userId, parameters);
        }
    }
}
