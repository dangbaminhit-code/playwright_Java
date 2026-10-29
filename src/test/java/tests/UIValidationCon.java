package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UIValidationCon {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        page =browser.newPage();
        page.navigate("https://rahulshettyacademy.com/AutomationPractice/");
    }
    @Test
    public void popUpTest() {
        assertThat(page.getByPlaceholder("Hide/Show Example")).isVisible();
        page.locator("#hide-textbox").click();
        assertThat(page.getByPlaceholder("Hide/Show Example")).isHidden();
        page.onDialog(dialog -> dialog.accept());
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Alert")).click();
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Confirm")).click();
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Mouse Hover")).hover();
        page.waitForTimeout(3000);
        page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Top")).click();

        FrameLocator framePage= page.frameLocator("#courses-iframe");
        framePage.getByRole(AriaRole.LINK,new FrameLocator.GetByRoleOptions().setName("Learning paths")).click();
        assertThat(framePage.getByRole(AriaRole.HEADING,new FrameLocator.GetByRoleOptions().setName("LEARNING PATHS"))).isVisible();
        String textCheck = framePage.locator(".inner-box h1").textContent();
        System.out.println(textCheck);
    }
    @Test
    public void screenshotTest() {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pageScreenshot.png")));
        Locator hideShow= page.getByPlaceholder("Hide/Show Example");
        hideShow.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("editboxScreenshot.png")));
        page.locator("#hide-textbox").click();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pageScreenshot1.png")));
    }
}