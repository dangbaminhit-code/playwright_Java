package tests;

import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;

public class dashBoardPageTest extends testBase
{
    @Test(description = "Test dashboard page")
    public void dashboardTest(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        //DashboardPage dashboardPage = new DashboardPage(page);
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
    }
}
