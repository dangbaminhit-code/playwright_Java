package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.awt.SystemColor.text;

public class AdminBookingPage {
    Page page;
    public final Locator bookingRows;
    public final Locator totalText;
    public final Locator statusSelect;
    public AdminBookingPage(Page page) {
        this.page = page;
        this.bookingRows =  page.locator("table tbody tr");
        this.totalText = page.getByText("total bookings");
        this.statusSelect = page.getByRole(AriaRole.COMBOBOX).
                filter(new Locator.FilterOptions().setHasText("All Statuses"));

    }
    public void goToAdminBookings() {
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/bookings");
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Manage Bookings"))).isVisible();
    }
    public Locator adminWaitBookings(){
        //Locator bookingCard = page.getByTestId("booking-card");
       bookingRows.first().waitFor();
        int numBooking = bookingRows.count();
        System.out.println("number of bookings in this page: " + numBooking);
        return bookingRows;
    }
    public Locator adminFindBookingCard(String Bookingref){
        Locator bookingRows = adminWaitBookings();
        Locator targetBooking = bookingRows.filter
                (new Locator.FilterOptions().setHasText(Bookingref));
        assertThat(targetBooking).isVisible();
        return targetBooking;
    }
    public void adminViewBookingDetails(String Bookingref){
        Locator targetBooking = adminFindBookingCard(Bookingref);
        targetBooking.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("View")).click();
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(Bookingref))).isVisible();


    }
    public int adminDeleteBooking(String Bookingref){
        Locator targetBooking = adminFindBookingCard(Bookingref);
        int countBeforeAdmin= bookingRows.count();
        targetBooking.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Cancel")).click();
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Yes, cancel it")).click();
        assertThat(page.getByText("Booking Cancelled")).isVisible();
        return countBeforeAdmin;

    }
    public void adminVerifyAfterDeleteBooking(String Bookingref,int countBeforeAdmin){
        Locator deletedBooking = bookingRows.filter(new
                Locator.FilterOptions().setHasText(Bookingref));
        assertThat(deletedBooking).hasCount(0);
        assertThat(bookingRows).hasCount(countBeforeAdmin-1);
        System.out.println("Cancelled booking " + Bookingref
                + ". Remain: " + bookingRows.count() + " booking");
    }
    public void filterByStatus(String status){
        statusSelect.selectOption(status);
    }
    public void verifyFilterByStatus(String status){
        Locator empty = page.getByText("No bookings found");
        assertThat(bookingRows.first().or(empty)).isVisible();
        int count = bookingRows.count();
        if (count == 0) {
            System.out.println("Filter by status \"" + status + "\" => 0 booking");
        } else {
            System.out.println("Filter by status \"" + status + "\" => " + count + " booking");
        }
    }

    public void verifyNoResults() {
        assertThat(bookingRows).hasCount(0);
        assertThat(page.getByText("No bookings found")).isVisible();
        System.out.println("Result is: " + bookingRows.count());
    }


}
