package tests;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class myBookingsTest extends testBase{
    @Test(description = "go to my booking page")
    public void goToMyBooking(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        myBookings myBooking  = new myBookings(page);
        myBooking.goToMyBookings(); // vào trang my booking
        myBooking.waitBookings();// chờ cho các booking hiện lên
    }
    @Test(description = "view booking details")
    public void viewDetails(){ //view booking details of newly booking
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
        myBookings myBooking  = new myBookings(page);
        myBooking.goToMyBookings(); // vào trang my booking
        myBooking.waitBookings();// chờ cho các booking hiện lên
        myBooking.viewBookingDetails(Bookingref); // bấm view details ở booking ref
    }
    @Test(description = "go back my booking page from view details")
    public void backToMyBookings(){
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
            myBookings myBooking  = new myBookings(page);
            myBooking.goToMyBookings(); // vào trang my booking
            myBooking.waitBookings();// chờ cho các booking hiện lên
            myBooking.viewBookingDetails(Bookingref); // bấm view details ở booking ref
            myBooking.backToMyBookings();

    }
    @Test(description = "cancel newly booking")
    public void cancelNewlyBooking(){
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
        myBookings myBooking  = new myBookings(page);
        myBooking.goToMyBookings(); // vào trang my booking
        myBooking.waitBookings();// chờ cho các booking hiện lên
        int countBefore = myBooking.cancelBooking(Bookingref);
        myBooking.verifyAfterDeletedBooking(Bookingref,countBefore);//verify sau khi delete
    }
    @Test(description = "Clear All booking")
    public void clearAllBooking(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        myBookings myBooking  = new myBookings(page);
        myBooking.goToMyBookings(); // vào trang my booking
        myBooking.waitBookings();// chờ cho các booking hiện lên
        myBooking.clearAllBookings();
        myBooking.verifyAfterClearAllBookings();

    }
    // cancel existing booking
    @DataProvider(name="Bookingref")
    public Object[][] Bookingref(){
        return new Object[][]{
                {"T-XDB384"},{"W-YRYJZF"}};
    }
    @Test(dataProvider = "Bookingref")
    public void cancelBooking(String Bookingref){
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();

        myBookings bookings = new myBookings(page);
        bookings.goToMyBookings();

        int countBefore = bookings.cancelBooking(Bookingref);
        bookings.verifyAfterDeletedBooking(Bookingref, countBefore);


    }

    // clear all booking in my booking page
    @Test(description = "clear all bookings")
    public void clearAllBookings(){
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();
        myBookings bookings = new myBookings(page);
        bookings.goToMyBookings();
        assertThat(bookings.bookingCard.first().
                or(page.getByText("No bookings yet"))).isVisible();
        int num = bookings.bookingCard.count();
        if (num==0){
            throw new SkipException("There is no Booking to Delete");
        }
        bookings.clearAllBookings();
        bookings.verifyAfterClearAllBookings();
    }
}
