package tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.BookingFormPage;
import pages.DashboardPage;
import pages.EventPage;
import pages.LoginPage;

public class bookingFormPageTest extends testBase{
    @DataProvider(name = "dataBook")
    public Object[][] dataBook(){
        return new Object[][]{
                {"Minh Dang1","m1@abc.com","0938051123"},
                {"Minh Dang2","m2@abc.com","0938051124"},
                {"Minh Dang3","m3@abc.com","0938051125"},
                {"Minh Dang4","m4@abc.com","0938051126"}
        };
    }
    @Test(dataProvider = "dataBook", description = "Book event")
    public void bookEvent(String name, String email, String phone){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.findEventCard("POM");
        BookingFormPage bookingFormPage= eventPage.processToBook("POM");
        //bookingFormPage.bookEvent("Minh Dang",
              //  "abc@bcd.com","0938051444");
        bookingFormPage.bookEvent(name,email,phone);
        String Bookingref= bookingFormPage.checkRefBooking();
        bookingFormPage.verifyBookingRef();
    }
}
