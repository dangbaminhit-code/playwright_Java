package api;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.HashMap;
import java.util.Map;

public class AuthApi {
    private final APIRequestContext request;

    public AuthApi(APIRequestContext request) {
        this.request = request;
    }

    /** Sends only the non-null fields, so tests can omit email or password. */
    public APIResponse apiLogin(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        if (email != null) body.put("email", email);
        if (password != null) body.put("password", password);
        return request.post("/api/auth/login", RequestOptions.create().setData(body));
    }

    public String getToken(String email, String password) {
        APIResponse response = apiLogin(email, password);
        if (!response.ok()) throw new RuntimeException("Login failed: " + response.text());
        return JsonPath.read(response.text(), "$.token");
    }

    /** Requires a context that sends the Authorization header. */
    public APIResponse me() {
        return request.get("/api/auth/me");
    }
}
