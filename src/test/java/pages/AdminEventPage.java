package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
    public final Locator eventRow;
    public AdminEventPage(Page page) {
        this.page = page;
        this.eventRow= page.getByTestId("event-table-row");


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

    public Locator waitAdminEvent(){
        eventRow.first().waitFor();
        int numEvent = eventRow.count();
        System.out.println("Number of event in admin event page is: "+numEvent );
        return eventRow;
    }
    public Locator findAdminEvent(String titleCard){
        Locator adminEventCard =waitAdminEvent();
        Locator adminTargetCard = adminEventCard.filter(new Locator.FilterOptions()
                .setHasText(titleCard));
        assertThat(adminTargetCard).isVisible();
        return adminTargetCard;
    }

    public int adminDeleteEvent(String titleCard){
        Locator adminTargetCard= findAdminEvent(titleCard);
        int adminCountBefore= eventRow.count();
        adminTargetCard.getByTestId("delete-event-btn").click();
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Delete event")).click();
        return adminCountBefore;


    }

    public void adminVerifyAfterDeleteEvent(String titleCard, int adminCountBefore){
        Locator adminDeletedEvent= eventRow.filter(new Locator.FilterOptions()
                .setHasText(titleCard));
        assertThat(adminDeletedEvent).hasCount(0);
        assertThat(eventRow).hasCount(adminCountBefore-1);
        System.out.println("Deleted event " + titleCard
                + ". Remain: " + eventRow.count() + " event");
    }

    public void adminEditEventUpdate(String titleCard, String
            newTitle,String newDescription, String newCategory,
                                     String newCity, String newVenue,String newDateTime,
                                     String newPrice, String newTotalSeats){
        Locator adminTargetCard= findAdminEvent(titleCard);
        adminTargetCard.getByTestId("edit-event-btn").click();
        assertThat(page.locator(Event_title_input)).hasValue(titleCard);
        page.locator(Event_title_input).fill(newTitle,new Locator.FillOptions().setTimeout(10000));
        page.locator(Event_description_input).fill(newDescription);
        page.getByLabel(Event_category_input).selectOption(newCategory);
        page.getByLabel(Event_city_input).fill(newCity);
        page.getByLabel(Event_venue_input).fill(newVenue);
        page.getByLabel(Event_date_input).fill(newDateTime);
        page.getByLabel(Event_price_input).fill(newPrice);
        page.getByLabel(Event_seat_input).fill(newTotalSeats);
        page.getByRole(AriaRole.BUTTON,new
                Page.GetByRoleOptions().setName("Update Event")).click();
        assertThat(page.getByText("Event updated!")).isVisible();
    }

    public void adminEditCancleUpdate(String titleCard){
        Locator adminTargetCard= findAdminEvent(titleCard);
        adminTargetCard.getByTestId("edit-event-btn").click();
        page.getByRole(AriaRole.BUTTON,new
                Page.GetByRoleOptions().setName("Cancel edit")).click();
    }

}
