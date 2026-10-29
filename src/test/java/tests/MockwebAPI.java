package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.microsoft.playwright.Locator;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class MockwebAPI {

    Page page;
    Browser browser;
    Playwright playwright;

    @BeforeMethod
    public void setUp() {
        //Playwright playwright = Playwright.create();
        //Browser browser= playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        //Page page =browser.newPage();
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(8000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @Test(description = "Sanbox")
    public void DemoTest() {
        //code
        //Playwright playwright = Playwright.create();
        //Browser browser= playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        //Page page =browser.newPage();
        //page.navigate("https://eventhub.rahulshettyacademy.com/login");
        //System.out.println(page.title());
        //assertThat(page).hasTitle("EventHub — Discover & Book Events");
        page.getByLabel("Email").fill("dangbaminh.it@gmail.com");
        page.getByLabel("Password").fill("123456789@xX");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();
        //page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Register")).click();

        page.route("**/api/events**", (Route route) -> route.fulfill(
                new Route.FulfillOptions().setPath(Paths.get("src/test/resources/event.json"))
        ));


        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        //page.waitForTimeout(7000);
        Locator eventCard = page.getByTestId("event-card");
        assertThat(eventCard.first()).isVisible();
        Assert.assertEquals(eventCard.count(), 3);
        //Assert.assertTrue(page.locator(".mx-1").isVisible());
        //assertThat(page.locator(".mx-1").first()).isVisible();
        //assertThat(page.locator(".mx-1")).not().isVisible();
        assertThat(page.locator(".mx-1")).isHidden();
    }
    // route resume
    @Test
    public void resumeTest(){
        page.getByLabel("Email").fill("dangbaminh.it@gmail.com");
        page.getByLabel("Password").fill("123456789@xX");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();
        //page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Register")).click();

        page.getByTestId("nav-bookings").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View Details")).first().click();
        page.route("**/api/bookings**", route ->
                route.resume(new Route.ResumeOptions()
                        .setUrl("https://eventhub.rahulshettyacademy.com/bookings/35317")));

        assertThat(page.getByText("Booking not found")).isVisible();

    }
}

