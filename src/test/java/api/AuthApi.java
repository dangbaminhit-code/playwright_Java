package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

public class AuthApi
{
    private final APIRequestContext request;

    public AuthApi (APIRequestContext request){
        this.request=request;
    }
    public APIResponse apiLogin(String email, String password){
        return request.post("/api/auth/login",
                RequestOptions.create().setData(Map.of(
                        "email",email,"password",password)));
    }
    public String getToken(String email, String password){
        APIResponse response = apiLogin(email,password);
        if(!response.ok()) throw new
                RuntimeException("Login failed: " + response.text());
        return JsonPath.read(response.text(), "$.token");
    }

}
