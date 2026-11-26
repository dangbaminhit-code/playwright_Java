package tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.AdminBookingPage;
import pages.LoginPage;

public class adminBookingTest extends testBase{
    @DataProvider(name="status")
    public Object[][] status(){
        return new Object[][]{
                {"Confirmed"},{"Cancelled"},{"All Statuses"}
        };

    }

    @Test(dataProvider = "status", description = "filter by status")
    public void filterByStatus(String status){
        new LoginPage(page, baseUrl).logintoPage().waiteventLoad();
        AdminBookingPage admin = new AdminBookingPage(page);
        admin.goToAdminBookings();
        admin.adminWaitBookings();
        admin.filterByStatus(status);
        admin.verifyFilterByStatus(status);

    }
    @Test(description = "view booking details")
    public void viewBookingDetails(){
        new LoginPage(page,baseUrl).logintoPage().waiteventLoad();
        AdminBookingPage admin = new AdminBookingPage(page);
        admin.goToAdminBookings();
        admin.adminWaitBookings();
        admin.adminFindBookingCard("P-1D20GG");
        admin.adminViewBookingDetails("P-1D20GG");
        admin.closeBookingDetails();
    }
    @Test(description = "delete booking")
    public void deleteBooking(){
        new LoginPage(page,baseUrl).logintoPage().waiteventLoad();
        AdminBookingPage admin = new AdminBookingPage(page);
        admin.goToAdminBookings();
        admin.goToAdminBookings();
        admin.adminFindBookingCard("P-1D20GG");
       int countBefore= admin.adminDeleteBooking("P-1D20GG");
        admin.adminVerifyAfterDeleteBooking("P-1D20GG",countBefore);
    }
}
