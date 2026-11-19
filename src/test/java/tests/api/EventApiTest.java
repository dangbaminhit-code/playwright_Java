package tests.api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventApiTest extends apiTestBase {

    // ---------- create ----------

    @Test
    public void createEventReturnsCorrectData() {
        Map<String, Object> event = sampleEvent();
        APIResponse response = eventAPI.createEvent(event);
        assertStatus(response, 201);

        String body = response.text();
        int eventId = JsonPath.read(body, "$.data.id");
        try {
            Assert.assertEquals(JsonPath.read(body, "$.success"), Boolean.TRUE);
            Assert.assertEquals(JsonPath.read(body, "$.message"), "Event created successfully");
            Assert.assertEquals(JsonPath.read(body, "$.data.title"), event.get("title"));
            Assert.assertEquals(JsonPath.read(body, "$.data.description"), event.get("description"));
            Assert.assertEquals(JsonPath.read(body, "$.data.category"), event.get("category"));
            Assert.assertEquals(JsonPath.read(body, "$.data.venue"), event.get("venue"));
            Assert.assertEquals(JsonPath.read(body, "$.data.city"), event.get("city"));
            Assert.assertEquals(JsonPath.read(body, "$.data.eventDate"),
                    ((String) event.get("eventDate")).replace("Z", ".000Z"));
            // API returns price as a string -> compare as numbers
            Assert.assertEquals(toDouble(JsonPath.read(body, "$.data.price")), toDouble(event.get("price")));
            Assert.assertEquals(toDouble(JsonPath.read(body, "$.data.totalSeats")), toDouble(event.get("totalSeats")));
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void newEventHasAllSeatsAvailableAndIsOwnedByUser() {
        APIResponse response = eventAPI.createEvent(sampleEvent(42));
        assertStatus(response, 201);
        String body = response.text();
        int eventId = JsonPath.read(body, "$.data.id");
        try {
            Assert.assertEquals((int) JsonPath.read(body, "$.data.availableSeats"), 42);
            Assert.assertEquals(JsonPath.read(body, "$.data.isStatic"), Boolean.FALSE);
            int userId = JsonPath.read(authApi.me().text(), "$.user.userId");
            Assert.assertEquals((int) JsonPath.read(body, "$.data.userId"), userId);
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @DataProvider
    public Object[][] invalidEventData() {
        return new Object[][]{
                {"title", null, "title"},
                {"title", "", "title"},
                {"category", null, "category"},
                {"venue", null, "venue"},
                {"city", null, "city"},
                {"eventDate", null, "eventDate"},
                {"eventDate", "not-a-date", "eventDate"},
                {"eventDate", "2020-01-01T00:00:00.000Z", "eventDate"},   // past date
                {"price", null, "price"},
                {"price", -1, "price"},
                {"price", "abc", "price"},
                {"totalSeats", null, "totalSeats"},
                {"totalSeats", 0, "totalSeats"},
                {"totalSeats", -5, "totalSeats"},
                {"totalSeats", 1.5, "totalSeats"},
        };
    }

    /** value == null means the field is removed from the request. */
    @Test(dataProvider = "invalidEventData")
    public void createEventValidation(String field, Object value, String expectedErrorField) {
        Map<String, Object> event = sampleEvent();
        if (value == null) event.remove(field); else event.put(field, value);

        APIResponse response = eventAPI.createEvent(event);
        if (response.status() == 201) {   // clean up if the API wrongly accepted it
            eventAPI.deleteEvent(JsonPath.read(response.text(), "$.data.id"));
        }
        assertValidationError(response, expectedErrorField);
    }

    @Test
    public void createEventWithPriceZeroIsAllowed() {
        Map<String, Object> event = sampleEvent();
        event.put("price", 0);
        int eventId = createEvent(event);
        eventAPI.deleteEvent(eventId);
    }

    // ---------- read ----------

    @Test
    public void getEventByIdReturnsEvent() {
        Map<String, Object> event = sampleEvent();
        int eventId = createEvent(event);
        try {
            APIResponse response = eventAPI.getEvent(eventId);
            assertStatus(response, 200);
            Assert.assertEquals((int) JsonPath.read(response.text(), "$.data.id"), eventId);
            Assert.assertEquals(JsonPath.read(response.text(), "$.data.title"), event.get("title"));
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void getNonExistentEventReturns404() {
        assertError(eventAPI.getEvent(999_999_999), 404, "not found");
    }

    @Test
    public void createdEventAppearsInList() {
        int eventId = createEvent(sampleEvent());
        try {
            Assert.assertTrue(eventAPI.getEventIds(1, 100).contains(eventId),
                    "Event " + eventId + " not found in event list");
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void searchFindsEventByTitle() {
        Map<String, Object> event = sampleEvent();
        int eventId = createEvent(event);
        try {
            APIResponse response = eventAPI.getEvents(Map.of("search", (String) event.get("title"), "limit", "50"));
            assertStatus(response, 200);
            List<Integer> ids = JsonPath.read(response.text(), "$.data[*].id");
            Assert.assertEquals(ids, List.of(eventId), "Search should return only the created event");
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void categoryFilterReturnsOnlyThatCategory() {
        int eventId = createEvent(sampleEvent());   // category = Concert
        try {
            APIResponse response = eventAPI.getEvents(Map.of("category", "Concert", "limit", "100"));
            assertStatus(response, 200);
            List<String> categories = JsonPath.read(response.text(), "$.data[*].category");
            Assert.assertFalse(categories.isEmpty());
            categories.forEach(c -> Assert.assertEquals(c, "Concert"));
            List<Integer> ids = JsonPath.read(response.text(), "$.data[*].id");
            Assert.assertTrue(ids.contains(eventId));
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    // ---------- pagination ----------

    @Test
    public void paginationIsConsistent() {
        int limit = 2;
        APIResponse response = eventAPI.getEvents(1, limit);
        assertStatus(response, 200);
        String body = response.text();

        int total = JsonPath.read(body, "$.pagination.total");
        int totalPages = JsonPath.read(body, "$.pagination.totalPages");
        List<Object> data = JsonPath.read(body, "$.data");

        Assert.assertEquals((int) JsonPath.read(body, "$.pagination.page"), 1);
        Assert.assertEquals((int) JsonPath.read(body, "$.pagination.limit"), limit);
        Assert.assertTrue(data.size() <= limit, "Page has more items than limit");
        Assert.assertEquals(totalPages, (int) Math.ceil((double) total / limit));
        Assert.assertEquals(data.size(), Math.min(limit, total));
    }

    @Test
    public void pagesDoNotOverlap() {
        List<Integer> page1 = eventAPI.getEventIds(1, 2);
        List<Integer> page2 = eventAPI.getEventIds(2, 2);
        page2.forEach(id -> Assert.assertFalse(page1.contains(id), "Event " + id + " is on both pages"));
    }

    @Test
    public void pageBeyondLastReturnsEmptyList() {
        APIResponse response = eventAPI.getEvents(9999, 2);
        assertStatus(response, 200);
        List<Object> data = JsonPath.read(response.text(), "$.data");
        Assert.assertTrue(data.isEmpty(), "Expected empty page but got " + data.size() + " items");
    }

    // ---------- update ----------

    @Test
    public void updateEventPersistsChanges() {
        int eventId = createEvent(sampleEvent());
        try {
            Map<String, Object> updated = sampleEvent();
            updated.put("title", "Updated " + System.nanoTime());
            updated.put("city", "Ha Noi");
            updated.put("price", 25);

            APIResponse response = eventAPI.updateEvent(eventId, updated);
            assertStatus(response, 200);
            Assert.assertEquals(JsonPath.read(response.text(), "$.message"), "Event updated successfully");

            String detail = eventAPI.getEvent(eventId).text();
            Assert.assertEquals(JsonPath.read(detail, "$.data.title"), updated.get("title"));
            Assert.assertEquals(JsonPath.read(detail, "$.data.city"), "Ha Noi");
            Assert.assertEquals(toDouble(JsonPath.read(detail, "$.data.price")), 25.0);
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void updateEventWithMissingFieldsIsRejected() {
        int eventId = createEvent(sampleEvent());
        try {
            Map<String, Object> partial = new HashMap<>();
            partial.put("title", "Only title");
            assertValidationError(eventAPI.updateEvent(eventId, partial), "category");
        } finally {
            eventAPI.deleteEvent(eventId);
        }
    }

    @Test
    public void updateNonExistentEventReturns404() {
        assertError(eventAPI.updateEvent(999_999_999, sampleEvent()), 404, "not found");
    }

    // ---------- delete ----------

    @Test
    public void deletedEventDisappearsFromList() {
        int eventId = createEvent(sampleEvent());

        APIResponse deleteResponse = eventAPI.deleteEvent(eventId);
        assertStatus(deleteResponse, 200);
        Assert.assertEquals(JsonPath.read(deleteResponse.text(), "$.message"), "Event deleted successfully");

        Assert.assertFalse(eventAPI.getEventIds(1, 100).contains(eventId),
                "Deleted event " + eventId + " still appears in event list");
        assertError(eventAPI.getEvent(eventId), 404, "not found");
    }

    @Test
    public void deleteEventTwiceReturns404() {
        int eventId = createEvent(sampleEvent());
        assertStatus(eventAPI.deleteEvent(eventId), 200);
        assertError(eventAPI.deleteEvent(eventId), 404, "not found");
    }

    @Test
    public void deleteNonExistentEventReturns404() {
        assertError(eventAPI.deleteEvent(999_999_999), 404, "not found");
    }

    @Test
    public void staticEventCannotBeDeleted() {
        List<Integer> staticIds = JsonPath.read(eventAPI.getEvents(1, 100).text(),
                "$.data[?(@.isStatic == true)].id");
        if (staticIds.isEmpty()) throw new SkipException("No static event available");
        int staticId = staticIds.get(0);

        assertError(eventAPI.deleteEvent(staticId), 403, "Cannot delete a static event");
        assertStatus(eventAPI.getEvent(staticId), 200);   // still exists
    }
}
