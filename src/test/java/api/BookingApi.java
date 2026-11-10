package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.List;
import java.util.Map;

public class BookingApi {
    public final APIRequestContext request;

    public BookingApi(APIRequestContext request){
        this.request= request;
    }
    public APIResponse createBooking(Map<String,Object> bookingData){
        return request.post("/api/bookings", RequestOptions.create()
                .setData(bookingData));
    }
    public APIResponse getMybookings(){
        return request.get("/api/bookings");
    }
    public APIResponse getBooking(int id){
        return request.get("/api/bookings/" + id);
    }
    public APIResponse cancleBooking(int id){
        return request.delete("/api/bookings/" + id);
    }
    public List<String> getMyBookingRefs(){
        return JsonPath.read(getMybookings().text(), "$.data[*].bookingRef");
    }
}
