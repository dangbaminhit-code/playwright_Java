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
        String eventDate= "2026-11-07T03:09:00.000Z";
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

    }
}
