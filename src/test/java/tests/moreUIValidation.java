package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class moreUIValidation {

    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;

    @BeforeMethod
    public void setUp(){
       playwright = Playwright.create()  ;
       browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
       //chrome browser incognito browser
       context = browser.newContext();
       context.tracing().start(new Tracing.StartOptions().setScreenshots(true)
               .setSnapshots(true).setSources(true));
        page = context.newPage();
        page.navigate("https://rahulshettyacademy.com/loginpagePractise/");
    }
    @AfterMethod
    public void afterMethod(){
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace.zip")));

    }
    @Test
    public void Test(){
        Locator blinkingText = page.locator(".blinkingText");
        Page newPage = context.waitForPage(()-> blinkingText.first().click());
        // or we can use
        // Locator blinkingText2 = blinkingText.filter(new Locator.FilterOptions().setHasText("Free Access to InterviewQues/ResumeAssistance/Material"));
        newPage.waitForLoadState();
        assertThat(newPage.getByText("Documents request")).isVisible();
        String childText = newPage.locator(".red").textContent();
        //Please email us at mentor@rahulshettyacademy.com with below template to receive response
        String emailID = childText.split("at ")[1].split(" ")[0];
        System.out.println("Email ID: " + emailID);
        page.getByLabel("Username").fill(emailID);
        page.getByLabel("Password").fill("Learning@830$3mK2");
        //page.getByLabel("Admin").click();
        //page.getByRole(AriaRole.RADIO,new Page.GetByRoleOptions().setName("User")).click();
        Locator radio = page.getByRole(AriaRole.RADIO,new Page.GetByRoleOptions().setName("User"));
        radio.click();
        Assert.assertTrue(radio.isChecked());
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Okay")).click();
        page.getByRole(AriaRole.COMBOBOX).selectOption("Teacher");
        page.getByRole(AriaRole.CHECKBOX,new Page.GetByRoleOptions().setName("I Agree to the terms and conditions")).check();
        page.waitForTimeout(5000);
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Sign In")).click();
    }





}
