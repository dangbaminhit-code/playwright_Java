package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.List;
import java.util.Map;

public class BookingApi {
    private final APIRequestContext request;

    public BookingApi(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse createBooking(Map<String, Object> bookingData) {
        return request.post("/api/bookings", RequestOptions.create().setData(bookingData));
    }

    public APIResponse getMyBookings() {
        return request.get("/api/bookings");
    }

    public APIResponse getMyBookings(int page, int limit) {
        return request.get("/api/bookings", RequestOptions.create()
                .setQueryParam("page", page).setQueryParam("limit", limit));
    }

    public APIResponse getBooking(int id) {
        return request.get("/api/bookings/" + id);
    }

    public APIResponse cancelBooking(int id) {
        return request.delete("/api/bookings/" + id);
    }

    /** Uses a large page so new bookings are not missed because of pagination. */
    public List<String> getMyBookingRefs() {
        return JsonPath.read(getMyBookings(1, 100).text(), "$.data[*].bookingRef");
    }
}
