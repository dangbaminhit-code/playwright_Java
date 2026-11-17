package tests.api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class BookingApiTest extends apiTestBase {
    private static final int EVENT_ID = 285;
    private static final String CUSTOMER_NAME = "Minh Dang";
    private static final String CUSTOMER_EMAIL = "abc@bca.com";
    private static final String CUSTOMER_PHONE = "0938051444";
    private static final int QUANTITY = 2;

    private Map<String, Object> bookingData() {
        return Map.of(
                "eventId", EVENT_ID,
                "customerName", CUSTOMER_NAME,
                "customerEmail", CUSTOMER_EMAIL,
                "customerPhone", CUSTOMER_PHONE,
                "quantity", QUANTITY);
    }

    /** Creates a booking and returns its response body. */
    @Test
    private String createBooking() {
        APIResponse response = bookingApi.createBooking(bookingData());
        Assert.assertTrue(response.ok(), "Create booking failed: " + response.text());
        return response.text();
    }

    @Test
    public void createdBookingAppearsInMyBookings() {
        String created = createBooking();
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");
        try {
            Assert.assertTrue(bookingApi.getMyBookingRefs().contains(bookingRef),
                    "Booking " + bookingRef + " not found in My Bookings");
        } finally {
            bookingApi.cancleBooking(bookingId);
        }
    }

    @Test
    public void bookingDetailMatchesInput() {
        String created = createBooking();
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");
        try {
            APIResponse response = bookingApi.getBooking(bookingId);
            Assert.assertTrue(response.ok(), "Get booking detail failed: " + response.text());

            String detail = response.text();
            Assert.assertEquals(JsonPath.read(detail, "$.data.bookingRef"), bookingRef, "Wrong bookingRef");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerName"), CUSTOMER_NAME, "Wrong customerName");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerEmail"), CUSTOMER_EMAIL, "Wrong customerEmail");
            Assert.assertEquals(JsonPath.read(detail, "$.data.customerPhone"), CUSTOMER_PHONE, "Wrong customerPhone");
            Assert.assertEquals((int) JsonPath.read(detail, "$.data.quantity"), QUANTITY, "Wrong quantity");
            Assert.assertEquals((int) JsonPath.read(detail, "$.data.eventId"), EVENT_ID, "Wrong eventId");
        } finally {
            bookingApi.cancleBooking(bookingId);
        }
    }

    @Test
    public void cancelledBookingDisappearsFromMyBookings() {
        String created = createBooking();
        int bookingId = JsonPath.read(created, "$.data.id");
        String bookingRef = JsonPath.read(created, "$.data.bookingRef");

        APIResponse cancelResponse = bookingApi.cancleBooking(bookingId);
        Assert.assertTrue(cancelResponse.ok(), "Cancel booking failed: " + cancelResponse.text());

        Assert.assertFalse(bookingApi.getMyBookingRefs().contains(bookingRef),
                "Cancelled booking " + bookingRef + " still appears in My Bookings");
    }
}
