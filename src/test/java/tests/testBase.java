package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class testBase {
    Page page;
    Browser browser;
    Playwright playwright;
    String baseUrl;

    @BeforeMethod
    public void setUp() throws IOException {
        //Playwright playwright = Playwright.create();
        //Browser browser= playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        //Page page =browser.newPage();
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
        prop.load(fis);
        String browserName = prop.getProperty("browser");
        playwright = Playwright.create();
        if("firefox".equals(browserName)){
            browser = playwright.firefox().launch();
        }
        else if ("safari".equals(browserName)) {
            browser = playwright.webkit().launch();
        }
         else {
            browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
            }


        //playwright = Playwright.create();
        //browser= playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        page = browser.newPage();
        page.setDefaultTimeout(8000);
        baseUrl= prop.getProperty("qa.baseurl");
        //page.navigate("https://eventhub.rahulshettyacademy.com/login");


        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }
}
