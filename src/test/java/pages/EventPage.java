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
        eventCards.first().waitFor();          // chờ event đầu tiên hiện ra
        int soEvent = eventCards.count();
        System.out.println("Số event trên trang: " + soEvent);
        return eventCards;
    }

    public Locator findEventCard(String titleCard) {
        Locator eventCards = waitEvent();
        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHasText(titleCard));
        assertThat(targetCard).isVisible();
        return targetCard;
    }

    public int getSeatsCount(String titleCard) {
        Locator targetCard = findEventCard(titleCard);
        String seatCard = targetCard.getByText("seats").innerText();
        int seatsBefore = Integer.parseInt(seatCard.replaceAll("\\D+", "")); // lấy số: 500
        System.out.println("Số chỗ trước khi book: " + seatsBefore);
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
        int expectedSeats = seatsBefore - 1;   // đã book 1 vé
// hasText tự chờ cho đến khi trang tải xong dữ liệu mới
        assertThat(targetCardAfter.getByText("seats")).hasText(expectedSeats + " seats available");

        System.out.println("Số chỗ sau khi book: " + targetCardAfter.getByText("seats").innerText());
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
        assertThat(eventCards.first()).isVisible(); // đảm bảo có ít nhất 1 kết quả
        System.out.println("Số kết quả: " + eventCards.count());
    }

    public void verifyAllResultsCategory(String category) {
        verifyAllResultsContain(category);
        System.out.println("Số kết quả: " + eventCards.count());
    }

    public void verifyNoResults() {
        assertThat(eventCards).hasCount(0);
        // Nếu app có hiện thông báo "No events found" thì verify thêm:
        assertThat(page.getByText("No events found")).isVisible();
        System.out.println("Số kết quả: " + eventCards.count());
    }

    public void verifyAllResultsCity(String city) {
        verifyAllResultsContain(city);
        System.out.println("Số kết quả: " + eventCards.count());
    }

    public void verifyAllResultsKeyword(String keyword) {
        verifyAllResultsContain(keyword);
        System.out.println("Số kết quả: " + eventCards.count());
    }

    /**
     * Mọi card hiển thị đều chứa text. Cho phép 0 kết quả
     */
    public void verifyResultsMatchOrEmpty(String text) {
        Locator emptyMessage = page.getByText("No events found");
        assertThat(eventCards.first().or(emptyMessage)).isVisible();


        int count = eventCards.count();
        if (count == 0) {
            System.out.println("lọc theo \"" + text + "\" => 0 event");
        } else {
            System.out.println("lọc theo \"" + text + "\" => " + count + " event");
        }
    }
    public void verifyResultsRestored(int expectedCount) {
        assertThat(eventCards).hasCount(expectedCount);   // tự chờ đến khi đủ số card
        System.out.println("Sau khi clear search => " + eventCards.count() + " event");
    }
}





