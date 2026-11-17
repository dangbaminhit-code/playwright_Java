package tests;

import org.testng.annotations.Test;
import pages.AdminEventPage;
import pages.DashboardPage;
import pages.LoginPage;

public class adminEventTest extends testBase{
    @Test(description = "Test create event")
    public void createEvent(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        //DashboardPage dashboardPage = new DashboardPage(page);
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        AdminEventPage adminEventPage= new AdminEventPage(page);
        adminEventPage.goToAdminEventPage();
        adminEventPage.createEvent("POM","hello Minh","Sports"
                ,"HCM","SG","2026-11-18T03:51",
                "50","500");
    }
    @Test(description = "go to my admin event and delete event")
    public void deleteEvent(String titleCard){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        AdminEventPage adminEventPage= new AdminEventPage(page);
        adminEventPage.goToAdminEventPage();
        adminEventPage.waitAdminEvent();
        adminEventPage.findAdminEvent("POM");
        int adminCountBefore= adminEventPage.adminDeleteEvent("POM");
        adminEventPage.adminVerifyAfterDeleteEvent("POM",adminCountBefore);
    }
    @Test(description = "go to my admin event, edit and update event")
    public void updateEvent(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        AdminEventPage adminEventPage= new AdminEventPage(page);
        adminEventPage.goToAdminEventPage();
        adminEventPage.waitAdminEvent();
        adminEventPage.findAdminEvent("kuta1");
        adminEventPage.adminEditEventUpdate("kuta1","kutaaa",
                "hello, there is new description after updated",
                "Festival","Pune","Q1",
                "2026-12-19T03:51","56","125");
    }
    @Test(description = "go to my admin event, select edit and cancel")
    public void cancelUpdateEvent(){
        LoginPage loginPage = new LoginPage(page,baseUrl);
        loginPage.logintoPage();
        DashboardPage dashboardPage=loginPage.logintoPage();
        dashboardPage.waiteventLoad();
        AdminEventPage adminEventPage= new AdminEventPage(page);
        adminEventPage.goToAdminEventPage();
        adminEventPage.waitAdminEvent();
        adminEventPage.findAdminEvent("kutaaa");
        adminEventPage.adminEditCancleUpdate("kutaaa");
    }


}
