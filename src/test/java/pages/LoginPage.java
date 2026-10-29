package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {
    Page page;
    String baseUrl;
    private static final String email_lable = "Email";
    private static final String password_lable = "Password";

    public LoginPage(Page page, String baseUrl) {
            this.page = page;
            this.baseUrl = baseUrl;
    }

    public DashboardPage logintoPage(){
    page.navigate(baseUrl);
       System.out.println(page.title());
    assertThat(page).hasTitle("EventHub — Discover & Book Events");
        page.getByLabel(email_lable).fill("dangbaminh.it@gmail.com");
        page.getByLabel(password_lable).fill("123456789@xX");
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Sign In")).click();
        DashboardPage dashboardPage = new DashboardPage(page);
        return dashboardPage;
}


}
