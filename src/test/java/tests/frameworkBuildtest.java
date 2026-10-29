package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class frameworkBuildtest extends testBase {


    @Test(description = "Create event")
    public void DemoTest(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        //DashboardPage dashboardPage = new DashboardPage(page);
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        AdminEventPage adminEventPage= new AdminEventPage(page);
        adminEventPage.goToAdminEventPage();
        adminEventPage.createEvent("POM","hello Minh","Sports"
        ,"HCM","SG","2026-11-18T03:51",
                "50","500");
        //code
       //Playwright playwright = Playwright.create();
        //Browser browser= playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
       //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
       //Page page =browser.newPage();
       //page.navigate("https://eventhub.rahulshettyacademy.com/login");





        //page.waitForTimeout(3000);

        // find newly creator created or not
        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
       // page.locator("#nav-events").click();
       // Locator eventCards=page.getByTestId("event-card");
        //eventCards.first().waitFor();          // chờ event đầu tiên hiện ra
        //int soEvent = eventCards.count();
        //System.out.println("Số event trên trang: " + soEvent);
        eventPage.waitEvent();
        eventPage.findEventCard("POM");
       int seatsBefore = eventPage.getSeatsCount("POM");
        BookingFormPage bookingFormPage= eventPage.processToBook("POM");
        bookingFormPage.bookEvent("Minh Dang",
                "abc@bcd.com","0938051444");
        String Bookingref= bookingFormPage.checkRefBooking();
        bookingFormPage.verifyBookingRef();
        eventPage.verifySeatAfter("POM",seatsBefore);


      //assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
      //String seatCard = targetCard.getByText("seats").innerText();
        //int seatsBefore = Integer.parseInt(seatCard.replaceAll("\\D+", "")); // lấy số: 500
        //System.out.println("Số chỗ trước khi book: " + seatsBefore);

      //targetCard.getByTestId("book-now-btn").click();
      //page.locator("#customerName").fill("Minh Dang");
        //targetCard.getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName("Full Name*")).fill("Minh Dang");
      //page.locator("#customer-email").fill("dangbaminh.it@gmail.com");
      //page.getByLabel("Phone Number*").fill("0932667550");
      //page.locator("#confirm-booking").click();
      //assertThat(page.getByText("Booking Confirmed! ")).isVisible();
      //assertThat(page.getByText("Your tickets are reserved.")).isVisible();
      //String Bookingref= page.locator(".booking-ref").innerText();
      //System.out.println("Your booking code is: " +Bookingref);
      //page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("View My Bookings")).click();

      //verify in booking history

        //Locator bookingCards = page.locator("#booking-card");
        //Locator targetBookingCard= bookingCards.filter(new Locator.FilterOptions().setHasText(Bookingref));
        //assertThat(targetBookingCard).isVisible();
        // seat count reduction check
        //page.locator("#nav-events").click();
        //Locator eventCardsAfter=page.getByTestId("event-card");
        //eventCardsAfter.first().waitFor();          // chờ event đầu tiên hiện ra

        //Locator targetCardAfter =eventCardsAfter.filter(new Locator.FilterOptions().setHasText("POM"));
        //assertThat(targetCardAfter).isVisible();
       //int expectedSeats = seatsBefore - 1;   // đã book 1 vé
// hasText tự chờ cho đến khi trang tải xong dữ liệu mới
        //assertThat(targetCardAfter.getByText("seats")).hasText(expectedSeats + " seats available");

        //System.out.println("Số chỗ sau khi book: " + targetCardAfter.getByText("seats").innerText());
    }
}
