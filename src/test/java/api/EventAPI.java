package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.List;
import java.util.Map;

public class EventAPI {
    private final APIRequestContext request;

    public EventAPI(APIRequestContext request){
        this.request=request;
    }
    public APIResponse createEvent(Map<String,Object> eventData){
        return request.post("/api/events",
                RequestOptions.create().setData(eventData));
    }
    public APIResponse getEvents(int page, int limit){
        return request.get("/api/events",RequestOptions.create()
                .setQueryParam("page",page).setQueryParam("limit",limit));

    }
    public APIResponse deleteEvent(int eventID){
        return request.delete("/api/events/" + eventID);
    }
    public List<Integer> getEventIds(int page, int limit){
        return JsonPath.read(getEvents(page,limit).text(),"$.data[*].id");
    }
}
