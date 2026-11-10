package tests.api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class EventApiTest extends apiTestBase {

    private Map<String, Object> sampleEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("title", "Playwright API Event " + System.currentTimeMillis());
        event.put("description", "Playwright API Event");
        event.put("category", "Concert");
        event.put("venue", "HCM");
        event.put("city", "SG");
        event.put("eventDate", "2026-12-07T03:09:00.000Z");
        event.put("price", "10");
        event.put("totalSeats", "100");
        return event;
    }

    private int createEventAndGetId() {
        APIResponse response = eventAPI.createEvent(sampleEvent());
        Assert.assertTrue(response.ok(), "Create event failed: " + response.text());
        return JsonPath.read(response.text(), "$.data.id");
    }

    @Test
    public void createdEventAppearsInList() {
        int eventId = createEventAndGetId();
        try {
            Assert.assertTrue(eventAPI.getEventIds(1, 12).contains(eventId),
                    "Event " + eventId + " not found in event list");
        } finally {
            eventAPI.deleteEvent(eventId); // clean up test data
        }
    }

    @Test
    public void deletedEventDisappearsFromList() {
        int eventId = createEventAndGetId();

        APIResponse deleteResponse = eventAPI.deleteEvent(eventId);
        Assert.assertTrue(deleteResponse.ok(), "Delete event failed: " + deleteResponse.text());

        Assert.assertFalse(eventAPI.getEventIds(1, 12).contains(eventId),
                "Deleted event " + eventId + " still appears in event list");
    }
}
