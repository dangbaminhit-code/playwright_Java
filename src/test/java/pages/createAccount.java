package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import tests.testBase;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class createAccount extends testBase {
    Page page;
    String baseUrl;
    private static final String email_placeholder= "you@email.com";
    private static final String password_placeholder="Min 8 chars, uppercase, number & symbol";
    private static final String confirmPassword_placeholder="Repeat your password";


    public createAccount(Page page, String baseUrl){
        this.page = page;
        this.baseUrl= baseUrl;
    }
    public void gotoCreateAccount(){
        page.navigate(baseUrl);
        assertThat(page.getByText("Don't have an account?")).isVisible();
        page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Register")).click();

    }
    public DashboardPage createNewAccount(String email, String password){
        gotoCreateAccount();
        assertThat(page.getByText("Already have an account?")).isVisible();
        //page.getByPlaceholder(email_placeholder).fill("abc@bca.com");
        page.getByPlaceholder(email_placeholder).fill(email);
        //page.getByPlaceholder(password_placeholder).fill("123456789@xX");
        page.getByPlaceholder(password_placeholder).fill(password);
        //page.getByPlaceholder(confirmPassword_placeholder).fill("123456789@xX");
        page.getByPlaceholder(confirmPassword_placeholder).fill(password);
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Create Account")).click();
        DashboardPage dashboardPage = new DashboardPage(page);
        return dashboardPage;
    }
    public LoginPage goToLoginPage(){
        gotoCreateAccount();
        page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Sign in"));
        LoginPage loginPage = new LoginPage(page,baseUrl);
        return loginPage;
    }
}
