package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AdminEventPage {
    Page page;
    private static final String Event_title_input = "#event-title-input";
    private static final String Event_description_input = "#admin-event-form textarea";
    private static final String Event_category_input = "Category";
    private static final String Event_city_input = "City";
    private static final String Event_venue_input = "Venue";
    private static final String Event_date_input = "Event Date & Time";
    private static final String Event_price_input = "Price ($)";
    private static final String Event_seat_input = "Total Seats";
    private static final String Event_add = "#add-event-btn";
    private static final String sucess_toast = "Event Created!";
    public AdminEventPage(Page page) {
        this.page = page;


    }
    public void goToAdminEventPage(){
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
    }
    public void createEvent(String title, String description, String category, String city,
                            String venue, String dateTime, String price,
                            String totalSeats){
        // action - timeout max 10 seconds, assert - timeout max 5 seconds by default.
        page.locator(Event_title_input).fill(title,new Locator.FillOptions().setTimeout(10000));
        page.locator(Event_description_input).fill(description);
        page.getByLabel(Event_category_input).selectOption(category);
        page.getByLabel(Event_city_input).fill(city);
        page.getByLabel(Event_venue_input).fill(venue);
        page.getByLabel(Event_date_input).fill(dateTime);
        page.getByLabel(Event_price_input).fill(price);
        page.getByLabel(Event_seat_input).fill(totalSeats);
        //page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Add Event")).click();
        page.locator(Event_add).click(new Locator.ClickOptions().setTimeout(12000));
        assertThat(page.getByText(sucess_toast)).isVisible();
    }

}
