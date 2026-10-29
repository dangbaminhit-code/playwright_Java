package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingFormPage {
   Page page;
   private static final String customer_name = "#customerName";
   private static final String customer_email = "#customer-email";
   private static final String customer_phone = "Phone Number*";
   public BookingFormPage(Page page) {
        this.page = page;
   }
    public void bookEvent(String customerName, String customerEmail, String customerPhone){
        page.locator(customer_name).fill(customerName);
        //targetCard.getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName("Full Name*")).fill("Minh Dang");
        page.locator(customer_email).fill(customerEmail);
        page.getByLabel(customer_phone).fill(customerPhone);
        page.locator("#confirm-booking").click();
        assertThat(page.getByText("Booking Confirmed! ")).isVisible();
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
    }
    public String checkRefBooking(){
        String Bookingref= page.locator(".booking-ref").innerText();
        System.out.println("Your booking code is: " +Bookingref);
        return Bookingref;
    }
    public String verifyBookingRef(){
        page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("View My Bookings")).click();
        Locator bookingCards = page.locator("#booking-card");
        String Bookingref = checkRefBooking();
        Locator targetBookingCard= bookingCards.filter(new Locator.FilterOptions().setHasText(Bookingref));
        assertThat(targetBookingCard).isVisible();
        return Bookingref;
    }
}
