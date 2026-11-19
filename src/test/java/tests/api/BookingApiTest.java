package tests.api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingApiTest extends apiTestBase {
    private static final String CUSTOMER_NAME = "Minh Dang";
    private static final String CUSTOMER_EMAIL = "abc@bca.com";
    private static final String CUSTOMER_PHONE = "0938051444";
    private static final int QUANTITY = 2;
    private static final int EVENT_PRICE = 10;

    /** Event created for this class, so tests never depend on a hardcoded event id. */
    private int eventId;

    // TestNG runs the parent's @BeforeClass (setUpApi) before this one
    @BeforeClass
    public void createTestEvent() {
        Map<String, Object> event = sampleEvent(1000);
        event.put("price", EVENT_PRICE);
        eventId = createEvent(event);
    }

    @AfterClass(alwaysRun = true)
    public void deleteTestEvent() {
        if (eventAPI != null && eventId != 0) eventAPI.deleteEvent(eventId);
    }

    private Map<String, Object> bookingData(int forEventId, int quantity) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("eventId", forEventId);
        booking.put("customerName", CUSTOMER_NAME);
        booking.put("customerEmail", CUSTOMER_EMAIL);
        booking.put("customerPhone", CUSTOMER_PHONE);
        booking.put("quantity", quantity);
        return booking;
    }

    /** Creates a booking on the class event and returns its response body. Caller must cancel it. */
    private String createBooking(int quantity) {
        APIResponse response = bookingApi.createBooking(bookingData(eventId, quantity));
        assertStatus(response, 201);
        return response.text();
    }

    // ---------- create ----------

    @Test
    public void createBookingReturnsConfirmedBooking() {
        String created = createBooking(QUANTITY);
        int bookingId = JsonPath.read(created, "$.data.id");
        try {
            Assert.assertEquals(JsonPath.read(created, "$.success"), Boolean.TRUE);
            Assert.assertEquals(JsonPath.read(created, "$.message"), "Booking confirmed!");
            Assert.assertEquals(JsonPath.read(created, "$.data.status"), "confirmed");
            Assert.assertEquals((int) JsonPath.read(created, "$.data.eventId"), eventId);
            Assert.assertEquals(toDouble(JsonPath.read(created, "$.data.totalPrice")),
                    (double) EVENT_PRICE * QUANTITY, "totalPrice must be price x quantity");
            String ref = JsonPath.read(created, "$.data.bookingRef");
            Assert.assertTrue(ref.matches("^[A-Z0-9]+-[A-Z0-9]+$"), "Unexpected bookingRef format: " + ref);
        } finally {
            bookingApi.cancelBooking(bookingId);
        }
    }

    @Test
    public void bookingRefsAreUnique() {
        String first = createBooking(1);
        String second = createBooking(1);
        try {
            String firstRef = JsonPath.read(first, "$.data.bookingRef");
            String secondRef = JsonPath.read(second, "$.data.bookingRef");
            Assert.assertNotEquals(firstRef, secondRef);
        } finally {
            bookingApi.cancelBooking(JsonPath.read(first, "$.data.id"));
            bookingApi.cancelBooking(JsonPath.read(second, "$.data.id"));
        }
    }

    @Test
    public void createdBookingAppearsInMyBookings() {
        String created = createBooking(QUANTITY);
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");
        try {
            Assert.assertTrue(bookingApi.getMyBookingRefs().contains(bookingRef),
                    "Booking " + bookingRef + " not found in My Bookings");
        } finally {
            bookingApi.cancelBooking(bookingId);
        }
    }

    @Test
    public void bookingDetailMatchesInput() {
        String created = createBooking(QUANTITY);
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");
        try {
            APIResponse response = bookingApi.getBooking(bookingId);
            assertStatus(response, 200);

            String detail = response.text();
            Assert.assertEquals(JsonPath.read(detail, "$.data.bookingRef"), bookingRef, "Wrong bookingRef");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerName"), CUSTOMER_NAME, "Wrong customerName");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerEmail"), CUSTOMER_EMAIL, "Wrong customerEmail");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerPhone"), CUSTOMER_PHONE, "Wrong customerPhone");
            Assert.assertEquals((int) JsonPath.read(detail, "$.data.quantity"), QUANTITY, "Wrong quantity");
            Assert.assertEquals((int) JsonPath.read(detail, "$.data.eventId"), eventId, "Wrong eventId");
        } finally {
            bookingApi.cancelBooking(bookingId);
        }
    }

    // ---------- seats (business logic) ----------

    @Test
    public void bookingReducesAndCancelRestoresAvailableSeats() {
        int seatId = createEvent(sampleEvent(20));
        try {
            Assert.assertEquals(eventAPI.getAvailableSeats(seatId), 20);

            APIResponse booking = bookingApi.createBooking(bookingData(seatId, 3));
            assertStatus(booking, 201);
            int bookingId = JsonPath.read(booking.text(), "$.data.id");
            Assert.assertEquals(eventAPI.getAvailableSeats(seatId), 17, "Seats not reduced after booking");

            assertStatus(bookingApi.cancelBooking(bookingId), 200);
            Assert.assertEquals(eventAPI.getAvailableSeats(seatId), 20, "Seats not restored after cancel");
        } finally {
            eventAPI.deleteEvent(seatId);
        }
    }

    @Test
    public void cannotBookMoreThanAvailableSeats() {
        int smallId = createEvent(sampleEvent(5));
        try {
            APIResponse first = bookingApi.createBooking(bookingData(smallId, 4));
            assertStatus(first, 201);

            APIResponse overbook = bookingApi.createBooking(bookingData(smallId, 2));
            assertError(overbook, 400, "Only 1 seat(s) available, but 2 requested");
            Assert.assertEquals(eventAPI.getAvailableSeats(smallId), 1, "Failed booking must not change seats");

            // Booking exactly the remaining seats is allowed
            APIResponse last = bookingApi.createBooking(bookingData(smallId, 1));
            assertStatus(last, 201);
            Assert.assertEquals(eventAPI.getAvailableSeats(smallId), 0);

            bookingApi.cancelBooking(JsonPath.read(first.text(), "$.data.id"));
            bookingApi.cancelBooking(JsonPath.read(last.text(), "$.data.id"));
        } finally {
            eventAPI.deleteEvent(smallId);
        }
    }

    @Test
    public void cannotBookSoldOutEvent() {
        int soldOutId = createEvent(sampleEvent(2));
        try {
            APIResponse all = bookingApi.createBooking(bookingData(soldOutId, 2));
            assertStatus(all, 201);
            APIResponse response = bookingApi.createBooking(bookingData(soldOutId, 1));
            assertStatus(response, 400);
            Assert.assertEquals(JsonPath.read(response.text(), "$.success"), Boolean.FALSE);
            bookingApi.cancelBooking(JsonPath.read(all.text(), "$.data.id"));
        } finally {
            eventAPI.deleteEvent(soldOutId);
        }
    }

    // ---------- validation ----------

    @DataProvider
    public Object[][] invalidBookingData() {
        return new Object[][]{
                {"quantity", 0, "quantity"},
                {"quantity", -1, "quantity"},
                {"quantity", 11, "quantity"},          // max 10 per booking
                {"quantity", 1.5, "quantity"},
                {"quantity", null, "quantity"},
                {"customerName", "A", "customerName"},  // min 2 chars
                {"customerName", null, "customerName"},
                {"customerEmail", "not-an-email", "customerEmail"},
                {"customerEmail", null, "customerEmail"},
                {"customerPhone", "12345", "customerPhone"},       // < 10 digits
                {"customerPhone", "abcdefghijk", "customerPhone"}, // letters
                {"customerPhone", null, "customerPhone"},
                {"eventId", null, "eventId"},
                {"eventId", -1, "eventId"},
        };
    }

    /** value == null means the field is removed from the request. */
    @Test(dataProvider = "invalidBookingData")
    public void createBookingValidation(String field, Object value, String expectedErrorField) {
        Map<String, Object> booking = bookingData(eventId, 1);
        if (value == null) booking.remove(field); else booking.put(field, value);

        APIResponse response = bookingApi.createBooking(booking);
        if (response.status() == 201) {   // clean up if the API wrongly accepted it
            bookingApi.cancelBooking(JsonPath.read(response.text(), "$.data.id"));
        }
        assertValidationError(response, expectedErrorField);
    }

    @Test
    public void quantityBoundariesAreAccepted() {
        for (int quantity : new int[]{1, 10}) {
            String created = createBooking(quantity);
            Assert.assertEquals((int) JsonPath.read(created, "$.data.quantity"), quantity);
            bookingApi.cancelBooking(JsonPath.read(created, "$.data.id"));
        }
    }

    @Test
    public void bookingNonExistentEventReturns404() {
        assertError(bookingApi.createBooking(bookingData(999_999_999, 1)), 404, "not found");
    }

    @Test
    public void bookingDeletedEventReturns404() {
        int deletedId = createEvent(sampleEvent());
        assertStatus(eventAPI.deleteEvent(deletedId), 200);
        assertError(bookingApi.createBooking(bookingData(deletedId, 1)), 404, "not found");
    }

    // ---------- read / cancel ----------

    @Test
    public void getNonExistentBookingReturns404() {
        assertError(bookingApi.getBooking(999_999_999), 404, "not found");
    }

    @Test
    public void myBookingsHasPagination() {
        APIResponse response = bookingApi.getMyBookings();
        assertStatus(response, 200);
        String body = response.text();
        int total = JsonPath.read(body, "$.pagination.total");
        int limit = JsonPath.read(body, "$.pagination.limit");
        List<Object> data = JsonPath.read(body, "$.data");
        Assert.assertTrue(data.size() <= limit);
        Assert.assertEquals((int) JsonPath.read(body, "$.pagination.totalPages"),
                (int) Math.ceil((double) total / limit));
    }

    @Test
    public void cancelledBookingDisappearsFromMyBookings() {
        String created = createBooking(QUANTITY);
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");

        APIResponse cancelResponse = bookingApi.cancelBooking(bookingId);
        assertStatus(cancelResponse, 200);
        Assert.assertEquals(JsonPath.read(cancelResponse.text(), "$.message"), "Booking cancelled");

        Assert.assertFalse(bookingApi.getMyBookingRefs().contains(bookingRef),
                "Cancelled booking " + bookingRef + " still appears in My Bookings");
        assertError(bookingApi.getBooking(bookingId), 404, "not found");
    }

    @Test
    public void cancelBookingTwiceReturns404() {
        int bookingId = JsonPath.read(createBooking(1), "$.data.id");
        assertStatus(bookingApi.cancelBooking(bookingId), 200);
        assertError(bookingApi.cancelBooking(bookingId), 404, "not found");
    }

    @Test
    public void cancelNonExistentBookingReturns404() {
        assertError(bookingApi.cancelBooking(999_999_999), 404, "not found");
    }
}
