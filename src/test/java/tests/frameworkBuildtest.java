package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.DataProvider;
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


        // mybooking page
        myBookings myBooking  = new myBookings(page);
        myBooking.goToMyBookings(); // vào trang my booking
        myBooking.waitBookings();// chờ cho các booking hiện lên
        myBooking.viewBookingDetails(Bookingref); // bấm view details ở booking ref
        myBooking.backToMyBookings();//trở lai trang my booking
        int countBefore = myBooking.cancelBooking(Bookingref);
        myBooking.verifyAfterDeletedBooking(Bookingref,countBefore);






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
    // Xóa booking có sẵn
    @DataProvider(name="Bookingref")
    public Object[][] Bookingref(){
        return new Object[][]{
                {"K-RY1KOM"},{"D-3UXNOS"}};
        }
    @Test(dataProvider = "Bookingref")
      public void cancelBooking(String Bookingref){
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

        myBookings bookings = new myBookings(page);
        bookings.goToMyBookings();

        int countBefore = bookings.cancelBooking(Bookingref);
        bookings.verifyAfterDeletedBooking(Bookingref, countBefore);
    }


    // search function
    @DataProvider(name = "categories")
    public Object[][] categories() {
        return new Object[][] { {"Sports"}, {"Concert"}, {"Festival"},
                {"Conference"},{"Workshop"}};
    }

    @Test(dataProvider = "categories", description = "Lọc theo từng category")
    public void filterByCategory(String category) {
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.filterByCategory(category);
        //eventPage.verifySelectedCategory(category);
        //eventPage.verifyAllResultsCategory(category);
        page.waitForTimeout(2000);
        eventPage.verifyResultsMatchOrEmpty(category);
    }

    @Test(description = "Search keyword không tồn tại thì không có kết quả")
    public void searchNoResult() {
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

        EventPage eventPage = new EventPage(page);
        eventPage.search("xyz_khong_ton_tai_123", null, null)
                .verifyNoResults();
    }
    @DataProvider(name = "cities")
    public Object[][] cities() {
        return new Object[][] { {"Mumbai"}, {"Bangalore"}, {"Delhi"},
                {"Hyderabad"},{"Chennai"},{"Pune"}};
    }
    @Test(dataProvider = "cities", description = "Lọc theo từng city")
    public void filterByCity(String city) {
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.filterByCity(city);
        //eventPage.verifySelectedCategory(category);
        //eventPage.verifyAllResultsCategory(city);
        page.waitForTimeout(2000);
        eventPage.verifyResultsMatchOrEmpty(city);
    }
   @Test
   public void searchByKey() {
       new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

       EventPage eventPage = new EventPage(page);
       eventPage.goTo();
       int totalBefore = eventPage.waitEvent().count();
       eventPage.searchByKeyword("api");
       page.waitForTimeout(2000);
       //eventPage.verifyAllResultsKeyword("api");
       eventPage.verifyResultsMatchOrEmpty("api");
       eventPage.clearSearch();
       eventPage.verifyResultsRestored(totalBefore);

   }

}
