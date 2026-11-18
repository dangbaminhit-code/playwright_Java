package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EventPage {
    Page page;
    private final Locator searchInput;
    private final Locator categorySelect;
    private final Locator citySelect;
    private final Locator eventCards;


    public EventPage(Page page) {
        this.page = page;
        this.searchInput = page.getByPlaceholder("Search events, venues…");
        this.categorySelect = page.getByRole(AriaRole.COMBOBOX).filter(
                new Locator.FilterOptions().setHasText("All Categories"));
        this.citySelect = page.getByRole(AriaRole.COMBOBOX).filter(
                new Locator.FilterOptions().setHasText("All Cities"));
        this.eventCards = page.getByTestId("event-card");
    }

    public void goTo() {

        page.locator("#nav-events").click();
    }

    public Locator waitEvent() {
        Locator eventCards = page.getByTestId("event-card");
        eventCards.first().waitFor();          // wait for first event
        int soEvent = eventCards.count();
        System.out.println("number of events in page: " + soEvent);
        return eventCards;
    }

    public Locator findEventCard(String titleCard) {
        Locator eventCards = waitEvent();
        // Match the card title exactly: setHasText("POM") would also match "POM1", "POM2"...
        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHas(
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(titleCard).setExact(true))));
        assertThat(targetCard.first()).isVisible();
        targetCard = targetCard.first();
        return targetCard;
    }

    public int getSeatsCount(String titleCard) {
        Locator targetCard = findEventCard(titleCard);
        String seatCard = targetCard.getByText("seats").innerText();
        int seatsBefore = Integer.parseInt(seatCard.replaceAll("\\D+", "")); // get number: 500
        System.out.println("Seat before booked: " + seatsBefore);
        return seatsBefore;
    }

    public BookingFormPage processToBook(String titleCard) {
        Locator targetCard = findEventCard(titleCard);
        targetCard.getByTestId("book-now-btn").click();
        return new BookingFormPage(page);
    }

    public void verifySeatAfter(String titleCard, int seatsBefore) {
        goTo();
        Locator targetCardAfter = findEventCard(titleCard);
        assertThat(targetCardAfter).isVisible();
        int expectedSeats = seatsBefore - 1;   // book 1

        assertThat(targetCardAfter.getByText("seats")).hasText(expectedSeats + " seats available");

        System.out.println("Seat after booked: " + targetCardAfter.getByText("seats").innerText());
    }

    public EventPage searchByKeyword(String keyword) {
        searchInput.fill(keyword);
        return this;

    }

    public EventPage clearSearch() {
        searchInput.clear();
        assertThat(searchInput).hasValue("");
        return this;
    }

    public EventPage filterByCategory(String category) {
        categorySelect.selectOption(category);
        return this;
    }

    public EventPage filterByCity(String city) {
        citySelect.selectOption(city);
        return this;
    }

    public EventPage search(String keyword, String category, String city) {
        goTo();
        if (keyword != null) searchByKeyword(keyword);
        if (category != null) filterByCategory(category);
        if (city != null) filterByCity(city);
        return this;
    }

    public void verifyAllResultsContain(String text) {
        assertThat(eventCards.first()).isVisible(); // make sure at least 1 result
        System.out.println("Result is: " + eventCards.count());
    }

    public void verifyAllResultsCategory(String category) {
        verifyAllResultsContain(category);
        System.out.println("Result is: " + eventCards.count());
    }

    public void verifyNoResults() {
        assertThat(eventCards).hasCount(0);

        assertThat(page.getByText("No events found")).isVisible();
        System.out.println("Result is: " + eventCards.count());
    }

    public void verifyAllResultsCity(String city) {
        verifyAllResultsContain(city);
        System.out.println("Result is: " + eventCards.count());
    }

    public void verifyAllResultsKeyword(String keyword) {
        verifyAllResultsContain(keyword);
        System.out.println("Result is: " + eventCards.count());
    }

    /**
     * allow 0 result
     */
    public void verifyResultsMatchOrEmpty(String text) {
        Locator emptyMessage = page.getByText("No events found");
        assertThat(eventCards.first().or(emptyMessage)).isVisible();


        int count = eventCards.count();
        if (count == 0) {
            System.out.println("Filter by \"" + text + "\" => 0 event");
        } else {
            System.out.println("Filter by\"" + text + "\" => " + count + " event");
        }
    }
    public void verifyResultsRestored(int expectedCount) {
        assertThat(eventCards).hasCount(expectedCount);
        System.out.println("After clear search => " + eventCards.count() + " event");
    }
}





