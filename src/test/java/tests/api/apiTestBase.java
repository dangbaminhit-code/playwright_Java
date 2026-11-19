package tests.api;

import api.AuthApi;
import api.BookingApi;
import api.EventAPI;
import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class apiTestBase {
    protected Playwright playwright;
    protected Properties prop;
    protected String baseUrl;
    protected String email;

    /** Context without token: for login and 401 tests. */
    protected APIRequestContext anonymousRequest;
    /** Context with the Bearer token of the main user. */
    protected APIRequestContext request;

    protected AuthApi anonymousAuthApi;
    protected AuthApi authApi;
    protected EventAPI eventAPI;
    protected BookingApi bookingApi;

    @BeforeClass
    public void setUpApi() throws IOException {
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fis);
        }
        baseUrl = prop.getProperty("api.baseurl").trim();
        // Environment variables (e.g. in CI) override values in config.properties
        email = getConfig("API_EMAIL", "api.email");
        String password = getConfig("API_PASSWORD", "api.password");

        playwright = Playwright.create();
        anonymousRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(baseUrl));
        anonymousAuthApi = new AuthApi(anonymousRequest);

        request = newAuthorizedContext(anonymousAuthApi.getToken(email, password));
        authApi = new AuthApi(request);
        eventAPI = new EventAPI(request);
        bookingApi = new BookingApi(request);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownApi() {
        if (request != null) request.dispose();
        if (anonymousRequest != null) anonymousRequest.dispose();
        if (playwright != null) playwright.close();
    }

    protected APIRequestContext newAuthorizedContext(String token) {
        return playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(Map.of("Authorization", "Bearer " + token)));
    }

    /** Env var first, then config.properties; null if neither is set. */
    protected String findConfig(String envKey, String propKey) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) value = prop.getProperty(propKey);
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    protected String getConfig(String envKey, String propKey) {
        String value = findConfig(envKey, propKey);
        if (value == null)
            throw new IllegalStateException("Missing config: set env " + envKey + " or " + propKey);
        return value;
    }

    // ---------- test data ----------

    /** ISO-8601 date N days from now, so the event is always in the future. */
    protected static String futureDate(int days) {
        return Instant.now().plus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS).toString();
    }

    protected Map<String, Object> sampleEvent() {
        return sampleEvent(100);
    }

    protected Map<String, Object> sampleEvent(int totalSeats) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", "Playwright API Event " + System.nanoTime());
        event.put("description", "Playwright API Event");
        event.put("category", "Concert");
        event.put("venue", "HCM");
        event.put("city", "SG");
        event.put("eventDate", futureDate(30));
        event.put("price", 10);
        event.put("totalSeats", totalSeats);
        return event;
    }

    /** Creates an event and returns its id. Caller must delete it. */
    protected int createEvent(Map<String, Object> event) {
        APIResponse response = eventAPI.createEvent(event);
        assertStatus(response, 201);
        return JsonPath.read(response.text(), "$.data.id");
    }

    // ---------- assertions ----------

    protected static void assertStatus(APIResponse response, int expected) {
        Assert.assertEquals(response.status(), expected,
                response.url() + " returned unexpected status. Body: " + response.text());
    }

    /** Asserts a 400 "Validation failed" response that reports an error on the given field. */
    protected static void assertValidationError(APIResponse response, String field) {
        assertStatus(response, 400);
        String body = response.text();
        Assert.assertEquals(JsonPath.read(body, "$.success"), Boolean.FALSE);
        Assert.assertEquals(JsonPath.read(body, "$.error"), "Validation failed");
        List<String> fields = JsonPath.read(body, "$.details[*].field");
        Assert.assertTrue(fields.contains(field),
                "Expected a validation error on '" + field + "' but got " + fields);
    }

    /** Asserts an error response with success=false and the given status. */
    protected static void assertError(APIResponse response, int status, String errorContains) {
        assertStatus(response, status);
        String body = response.text();
        Assert.assertEquals(JsonPath.read(body, "$.success"), Boolean.FALSE);
        String error = JsonPath.read(body, "$.error");
        Assert.assertTrue(error.contains(errorContains),
                "Expected error containing '" + errorContains + "' but got '" + error + "'");
    }

    protected static double toDouble(Object value) {
        return Double.parseDouble(String.valueOf(value));
    }
}
