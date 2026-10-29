package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EventPage {
    Page page;

    public EventPage(Page page) {
        this.page = page;
    }

    public void goTo() {

        page.locator("#nav-events").click();
    }
        public Locator waitEvent(){
            Locator eventCards = page.getByTestId("event-card");
            eventCards.first().waitFor();          // chờ event đầu tiên hiện ra
            int soEvent = eventCards.count();
            System.out.println("Số event trên trang: " + soEvent);
            return eventCards;
        }
        public Locator findEventCard(String titleCard){
             Locator eventCards = waitEvent();
             Locator targetCard =eventCards.filter(new Locator.FilterOptions().setHasText(titleCard));
             assertThat(targetCard).isVisible();
             return targetCard;
        }
        public int getSeatsCount(String titleCard){
            Locator targetCard = findEventCard(titleCard);
            String seatCard = targetCard.getByText("seats").innerText();
            int seatsBefore = Integer.parseInt(seatCard.replaceAll("\\D+", "")); // lấy số: 500
            System.out.println("Số chỗ trước khi book: " + seatsBefore);
            return seatsBefore;
        }
        public BookingFormPage processToBook(String titleCard){
            Locator targetCard = findEventCard(titleCard);
            targetCard.getByTestId("book-now-btn").click();
            return new BookingFormPage(page);
        }
        public void verifySeatAfter(String  titleCard, int seatsBefore){
            goTo();
            Locator targetCardAfter = findEventCard(titleCard);
            assertThat(targetCardAfter).isVisible();
            int expectedSeats = seatsBefore - 1;   // đã book 1 vé
// hasText tự chờ cho đến khi trang tải xong dữ liệu mới
            assertThat(targetCardAfter.getByText("seats")).hasText(expectedSeats + " seats available");

            System.out.println("Số chỗ sau khi book: " + targetCardAfter.getByText("seats").innerText());
        }
    }

