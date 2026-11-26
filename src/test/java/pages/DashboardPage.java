package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DashboardPage {
    Page page;

    public DashboardPage(Page page) {
        this.page = page;
    }

    public void waiteventLoad() {
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();
        //page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Register")).click();
    }

    public EventPage goToEventsPage() {
        page.getByText("Browse Events →").click();
        assertThat(page.getByText("Upcoming Events")).isVisible();
        EventPage eventPage = new EventPage(page);
        return eventPage;
    }
    public myBookings goToMyBookings(){
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("My Bookings")).click();
        assertThat(page.getByText("View and manage all your ticket bookings")).isVisible();
        myBookings myBooking = new myBookings(page);
        return myBooking;
    }
}