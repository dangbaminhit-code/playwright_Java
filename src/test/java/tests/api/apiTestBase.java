package tests.api;

import api.AuthApi;
import api.BookingApi;
import api.EventAPI;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class apiTestBase {
    protected Playwright playwright;
    protected APIRequestContext request;
    protected EventAPI eventAPI;
    protected BookingApi bookingApi;

    @BeforeClass
    public void setUpApi() throws IOException {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            prop.load(fis);
        }
        String baseUrl = prop.getProperty("api.baseurl").trim();
        // Environment variables (e.g. in CI) override values in config.properties
        String email = getConfig(prop, "API_EMAIL", "api.email");
        String password = getConfig(prop, "API_PASSWORD", "api.password");

        playwright = Playwright.create();

        // 1. Context without token, used only to log in
        APIRequestContext anonymous = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(baseUrl));
        String token = new AuthApi(anonymous).getToken(email, password);
        anonymous.dispose();

        // 2. Context with token: every request automatically sends the Authorization header
        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(Map.of("Authorization", "Bearer " + token)));

        eventAPI = new EventAPI(request);
        bookingApi = new BookingApi(request);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownApi() {
        if (request != null) request.dispose();
        if (playwright != null) playwright.close();
    }

    private static String getConfig(Properties prop, String envKey, String propKey) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) value = prop.getProperty(propKey);
        if (value == null || value.isBlank())
            throw new IllegalStateException("Missing config: set env " + envKey + " or " + propKey);
        return value.trim();
    }
}
