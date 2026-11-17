package tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BookingFormPage;
import pages.DashboardPage;
import pages.EventPage;
import pages.LoginPage;

public class eventPageTest extends testBase{
    @Test(description = "find newly creator created or not")
    public void findNewlyEvent(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.findEventCard("POM");
    }
    @Test(description = "check remain seat count")
    public void checkRemainSeat(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.findEventCard("POM");
        int seatsBefore = eventPage.getSeatsCount("POM");
        BookingFormPage bookingFormPage= eventPage.processToBook("POM");
        bookingFormPage.bookEvent("Minh Dang",
                "abc@bcd.com","0938051444");
        String Bookingref= bookingFormPage.checkRefBooking();
        bookingFormPage.verifyBookingRef();
        eventPage.verifySeatAfter("POM",seatsBefore);
    }
    @DataProvider(name = "categories")
    public Object[][] categories() {
        return new Object[][] { {"Sports"}, {"Concert"}, {"Festival"},
                {"Conference"},{"Workshop"}};


    }
    @Test(dataProvider = "categories", description = "Filter by category")
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
    @DataProvider(name = "cities")
    public Object[][] cities() {
        return new Object[][] { {"Mumbai"}, {"Bangalore"}, {"Delhi"},
                {"Hyderabad"},{"Chennai"},{"Pune"}};
    }
    @Test(dataProvider = "cities", description = "Filter by city")
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
