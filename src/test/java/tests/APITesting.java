package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITesting {

    @Test
    public void APITest(){
        //Login
        HashMap<Object,Object> loginData = new HashMap<>();
        loginData.put("email","dangbaminh.it@gmail.com");
        loginData.put("password","123456789@xX");

        Playwright playwright = Playwright.create();
        APIRequestContext apiRequest = playwright.request().newContext();
        APIResponse apiResponse= apiRequest.post(
                "https://api.eventhub.rahulshettyacademy.com/api/auth/login",
                RequestOptions.create().setData(loginData));
        Assert.assertTrue(apiResponse.ok());
        System.out.println(apiResponse.text());

        String token = JsonPath.read(apiResponse.text(), "$.token");
        System.out.println("Your token is: " + token);

        //Create event

        HashMap<Object,Object> eventData = new HashMap<>();
        String eventTitle = "Playwright API Event 2211";
        String eventDescription = "Playwright API Event";
        String eventCategory = "Concert";
        String eventVenue = "HCM";
        String eventCity= "SG";
        String eventDate= "2026-12-07T03:09:00.000Z";
        String eventPrice= "10";
        String eventSeat = "100";

        eventData.put("title", eventTitle);
        eventData.put("description", eventDescription);
        eventData.put("category", eventCategory);
        eventData.put("venue", eventVenue);
        eventData.put("city", eventCity);
        eventData.put("eventDate", eventDate);
        eventData.put("price", eventPrice);
        eventData.put("totalSeats", eventSeat);

        HashMap<Object,Object> bookingData = new HashMap<>();
        int eventId = 285;
        String customerName = "Minh Dang";
        String customerEmail = "abc@bca.com";
        String customerPhone = "0938051444";
        int quantity = 2;

        bookingData.put("eventId", eventId);
        bookingData.put("customerName", customerName);
        bookingData.put("customerEmail", customerEmail);
        bookingData.put("customerPhone", customerPhone);
        bookingData.put("quantity", quantity);




        APIResponse eventResponse = apiRequest.post
                ("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setHeader("Authorization"
                        , "Bearer " + token).setData(eventData));
        Assert.assertTrue(eventResponse.ok(),"Create event API successful");
        System.out.println(eventResponse.text());
        int eventID= JsonPath.read(eventResponse.text(),
                "$.data.id");
        System.out.println("Your event ID is: " + eventID);

        //get event

        APIResponse getEvent = apiRequest.get
                ("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setQueryParam("page","1")
                        .setQueryParam("limit","12")
                        .setHeader("Authorization","Bearer " + token));

        Assert.assertTrue(getEvent.ok(),"Get event API successful");
        System.out.println(getEvent.text());
       List<Integer> allEventId = JsonPath.read(getEvent.text(),"$.data[*].id");
      Assert.assertTrue(allEventId.contains(eventID),"Your event appears.");

        //Delete event

       APIResponse deleteEvent=  apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/"
                +eventID,RequestOptions.create().setHeader("Authorization",
                "Bearer " + token));
       Assert.assertTrue(deleteEvent.ok(),"Delete event API successful");

      //Verify after delete

        APIResponse verifyEvent = apiRequest.get
                ("https://api.eventhub.rahulshettyacademy.com/api/events",
                        RequestOptions.create().setHeader("Authorization"
                                , "Bearer " + token).setQueryParam("page","1")
                                .setQueryParam("limit","12"));
                Assert.assertTrue(verifyEvent.ok(),"Verify event API successful");

               List<String> idsAfterDelete=JsonPath.read(verifyEvent.text(),"$.data[*].id");
                Assert.assertFalse(idsAfterDelete.contains(eventID),
                        "Your deleted event is not display in the list.");
                System.out.println("Deleted event is not belong to the list anymore");

                // book event

        APIResponse eventBooking = apiRequest.post
                ("https://api.eventhub.rahulshettyacademy.com/api/bookings",
                        RequestOptions.create().setHeader("Authorization"
                                , "Bearer " + token).setData(bookingData));
        Assert.assertTrue(eventBooking.ok(),"Booking confirmed!");
        System.out.println(eventBooking.text());
        int bookingId = JsonPath.read(eventBooking.text(), "$.data.id");
        String bookingRef= JsonPath.read(eventBooking.text(),
                "$.data.bookingRef");
        System.out.println("Booking id: " + bookingId + " | ref: " + bookingRef);

        // go to my booking page
        APIResponse myBooking = apiRequest.get
                ("https://api.eventhub.rahulshettyacademy.com/api/bookings",
                        RequestOptions.create().setHeader("Authorization"
                                , "Bearer " + token));
        System.out.println("Status: " + myBooking.status());
        System.out.println(myBooking.text());
        Assert.assertTrue(myBooking.ok(), "Get my bookings failed: " + myBooking.text());

        List<String> allBookingRefs = JsonPath.read(myBooking.text(), "$.data[*].bookingRef");
        Assert.assertTrue(allBookingRefs.contains(bookingRef),
                "Booking " + bookingRef + " not found in My Bookings");


        // View booking details
        APIResponse bookingDetail = apiRequest.get(
                "https://api.eventhub.rahulshettyacademy.com/api/bookings/" + bookingId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + token));

        System.out.println("Status: " + bookingDetail.status());
        System.out.println(bookingDetail.text());
        Assert.assertTrue(bookingDetail.ok(), "Get booking detail failed: " + bookingDetail.text());

        String detail = bookingDetail.text();
        Assert.assertEquals(JsonPath.read(detail, "$.data.bookingRef"), bookingRef, "Wrong bookingRef");
        Assert.assertEquals(JsonPath.read(detail, "$.data.customerName"), customerName, "Wrong customerName");
        Assert.assertEquals(JsonPath.read(detail, "$.data.customerEmail"), customerEmail, "Wrong customerEmail");
        Assert.assertEquals(JsonPath.read(detail, "$.data.customerPhone"), customerPhone, "Wrong customerPhone");
        Assert.assertEquals((int) JsonPath.read(detail, "$.data.quantity"), quantity, "Wrong quantity");
        Assert.assertEquals((int) JsonPath.read(detail, "$.data.eventId"), eventId, "Wrong eventId");

        //delete booking



        APIResponse deleteBooking=  apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/bookings/"
                +bookingId,RequestOptions.create().setHeader("Authorization",
                "Bearer " + token));
        Assert.assertTrue(deleteBooking.ok(),"Booking cancelled");

        //Verify after delete booking

        APIResponse verifyBooking = apiRequest.get
                ("https://api.eventhub.rahulshettyacademy.com/api/bookings",
                        RequestOptions.create().setHeader("Authorization"
                                        , "Bearer " + token));
        Assert.assertTrue(verifyBooking.ok(),"Verify booking API successful");

        List<String> refAfterDelete=JsonPath.read(verifyBooking.text(),"$.data[*].bookingRef");
        Assert.assertFalse(refAfterDelete.contains(bookingRef),
                "Your deleted booking is not display in the list.");
        System.out.println("Deleted booking is not belong to the list anymore");


    }






}
