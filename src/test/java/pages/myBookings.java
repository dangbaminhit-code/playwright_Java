package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class myBookings
{
    Page page;
    public final Locator bookingCard;

    public myBookings(Page page){
        this.page= page;
        this.bookingCard = page.getByTestId("booking-card");

    }
    public void goToMyBookings()
    {
        page.getByTestId("nav-bookings").click();
    }
    public Locator waitBookings(){
        //Locator bookingCard = page.getByTestId("booking-card");
        bookingCard.first().waitFor();
        int numBooking = bookingCard.count();
        System.out.println("number of bookings in this page: " + numBooking);
        return bookingCard;
    }
    public Locator findBookingCard(String Bookingref){
        Locator bookingCard = waitBookings();
        Locator targetBooking = bookingCard.filter
                (new Locator.FilterOptions().setHasText(Bookingref));
        assertThat(targetBooking).isVisible();
        return targetBooking;
    }
    public void viewBookingDetails(String Bookingref){
        Locator targetBooking = findBookingCard(Bookingref);
        targetBooking.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("View Details")).click();
        assertThat(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Event Details"))).isVisible();


    }
    public void backToMyBookings()
    {

        page.getByRole(
                AriaRole.BUTTON,new Page.GetByRoleOptions().
                        setName("← Back to My Bookings")).click();
       waitBookings();
    }
    public int cancelBooking(String Bookingref) {
        Locator targetBooking = findBookingCard(Bookingref);
        int countBefore= bookingCard.count();
        targetBooking.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Cancel Booking")).click();
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Yes, cancel it")).click();
        return countBefore;
    }

    public void verifyAfterDeletedBooking(String Bookingref, int countBefore) {
        Locator deletedBooking = bookingCard.filter(new
                Locator.FilterOptions().setHasText(Bookingref));
        assertThat(deletedBooking).hasCount(0);
        assertThat(bookingCard).hasCount(countBefore-1);
        System.out.println("Đã huỷ booking " + Bookingref
                + ". Còn lại: " + bookingCard.count() + " booking");
        }
    public void clearAllBookings(){
        waitBookings();
        page.onceDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON,new
                Page.GetByRoleOptions().setName("Clear all bookings")).click();


    }
    public void verifyAfterClearAllBookings(){
        assertThat(bookingCard).hasCount(0);
        System.out.println("Đã xoá hết booking. Còn lại: " + bookingCard.count());
    }
    }


