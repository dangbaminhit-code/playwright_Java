package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.List;
import java.util.Map;

public class EventAPI {
    private final APIRequestContext request;

    public EventAPI(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse createEvent(Map<String, Object> eventData) {
        return request.post("/api/events", RequestOptions.create().setData(eventData));
    }

    public APIResponse getEvents(int page, int limit) {
        return request.get("/api/events", RequestOptions.create()
                .setQueryParam("page", page).setQueryParam("limit", limit));
    }

    /** GET /api/events with extra filters, e.g. search, category, city. */
    public APIResponse getEvents(Map<String, String> query) {
        RequestOptions options = RequestOptions.create();
        query.forEach(options::setQueryParam);
        return request.get("/api/events", options);
    }

    public APIResponse getEvent(int eventId) {
        return request.get("/api/events/" + eventId);
    }

    public APIResponse updateEvent(int eventId, Map<String, Object> eventData) {
        return request.put("/api/events/" + eventId, RequestOptions.create().setData(eventData));
    }

    public APIResponse deleteEvent(int eventId) {
        return request.delete("/api/events/" + eventId);
    }

    public List<Integer> getEventIds(int page, int limit) {
        return JsonPath.read(getEvents(page, limit).text(), "$.data[*].id");
    }

    public int getAvailableSeats(int eventId) {
        return JsonPath.read(getEvent(eventId).text(), "$.data.availableSeats");
    }
}
