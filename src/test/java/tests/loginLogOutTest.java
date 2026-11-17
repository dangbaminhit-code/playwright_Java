package tests;

import org.testng.annotations.Test;
import pages.LoginPage;

public class loginLogOutTest extends testBase
{
    @Test(description = "Test Login")
    public void loginTest(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
    }
    @Test(description = "Test Logout")
    public void logoutTest(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();   // each test gets a new page, so log in first
        loginPage.logOut();
    }
}
