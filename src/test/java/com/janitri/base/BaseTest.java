package com.janitri.base;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;


public class BaseTest {

    protected WebDriver driver;

    @Parameters({"baseUrl"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("https://dev-dash.janitri.in") String baseUrl) {
        // Auto-manage drivers
        WebDriverManager.chromedriver().setup();

        // Handle permission requests (allow notifications)
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        // 1 = Allow, 2 = Block
        prefs.put("profile.default_content_setting_values.notifications", 1);
        options.setExperimentalOption("prefs", prefs);

        // Optional: headless via -Dheadless=true
        String headless = System.getProperty("headless", "false");
        if (headless.equalsIgnoreCase("true")) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().maximize();

        driver.get(baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}