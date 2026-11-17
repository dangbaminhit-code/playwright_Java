package tests;

import org.testng.annotations.Test;
import pages.BookingFormPage;
import pages.DashboardPage;
import pages.EventPage;
import pages.LoginPage;

public class bookingFormPageTest extends testBase{
    @Test(description = "Book event")
    public void bookEvent(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        EventPage eventPage = new EventPage(page);
        eventPage.goTo();
        eventPage.waitEvent();
        eventPage.findEventCard("POM");
        BookingFormPage bookingFormPage= eventPage.processToBook("POM");
        bookingFormPage.bookEvent("Minh Dang",
                "abc@bcd.com","0938051444");
        String Bookingref= bookingFormPage.checkRefBooking();
        bookingFormPage.verifyBookingRef();
    }
}
