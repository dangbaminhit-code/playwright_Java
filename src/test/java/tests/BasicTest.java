package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicTest {

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
        browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(8000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @Test(description = "Create event")
    public void DemoTest(){
        //code
       //Playwright playwright = Playwright.create();
        //Browser browser= playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
       //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
       //Page page =browser.newPage();
       //page.navigate("https://eventhub.rahulshettyacademy.com/login");
       System.out.println(page.title());
       assertThat(page).hasTitle("EventHub — Discover & Book Events");
        page.getByLabel("Email").fill("dangbaminh.it@gmail.com");
        page.getByLabel("Password").fill("123456789@xX");
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();
        //page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Register")).click();

        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        // action - timeout max 10 seconds, assert - timeout max 5 seconds by default.
        page.locator("#event-title-input").fill("kuta",new Locator.FillOptions().setTimeout(10000));
        page.locator("#admin-event-form textarea").fill("Hello World");
        page.getByLabel("Category").selectOption("Sports");
        page.getByLabel("City").fill("HCM");
        page.getByLabel("Venue").fill("TD");
        page.getByLabel("Event Date & Time").fill("2026-11-18T03:51");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("500");
        //page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Add Event")).click();
        page.locator("#add-event-btn").click(new Locator.ClickOptions().setTimeout(12000));
        assertThat(page.getByText("Event Created!")).isVisible();
        //page.waitForTimeout(3000);

        // find newly creator created or not
        page.locator("#nav-events").click();
        Locator eventCards=page.getByTestId("event-card");
        eventCards.first().waitFor();          // chờ event đầu tiên hiện ra
        int soEvent = eventCards.count();
        System.out.println("Số event trên trang: " + soEvent);

       Locator targetCard =eventCards.filter(new Locator.FilterOptions().setHasText("kuta"));
      assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
      String seatCard = targetCard.getByText("seats").innerText();
        int seatsBefore = Integer.parseInt(seatCard.replaceAll("\\D+", "")); // lấy số: 500
        System.out.println("Số chỗ trước khi book: " + seatsBefore);

      targetCard.getByTestId("book-now-btn").click();
      page.locator("#customerName").fill("Minh Dang");
        //targetCard.getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName("Full Name*")).fill("Minh Dang");
      page.locator("#customer-email").fill("dangbaminh.it@gmail.com");
      page.getByLabel("Phone Number*").fill("0932667550");
      page.locator("#confirm-booking").click();
      assertThat(page.getByText("Booking Confirmed! ")).isVisible();
      assertThat(page.getByText("Your tickets are reserved.")).isVisible();
      String Bookingref= page.locator(".booking-ref").innerText();
      System.out.println("Your booking code is: " +Bookingref);
      page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("View My Bookings")).click();

      //verify in booking history

        Locator bookingCards = page.locator("#booking-card");
        Locator targetBookingCard= bookingCards.filter(new Locator.FilterOptions().setHasText(Bookingref));
        assertThat(targetBookingCard).isVisible();
        // seat count reduction check
        page.locator("#nav-events").click();
        Locator eventCardsAfter=page.getByTestId("event-card");
        //eventCardsAfter.first().waitFor();          // chờ event đầu tiên hiện ra

        Locator targetCardAfter =eventCardsAfter.filter(new Locator.FilterOptions().setHasText("kuta"));
        //assertThat(targetCardAfter).isVisible();
        int expectedSeats = seatsBefore - 1;   // đã book 1 vé
// hasText tự chờ cho đến khi trang tải xong dữ liệu mới
        assertThat(targetCardAfter.getByText("seats")).hasText(expectedSeats + " seats available");

        System.out.println("Số chỗ sau khi book: " + targetCardAfter.getByText("seats").innerText());
    }
}
