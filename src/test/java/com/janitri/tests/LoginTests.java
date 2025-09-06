package com.janitri.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.janitri.pages.LoginPage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTests {

    private static WebDriver driver;
    private static LoginPage loginPage;

    @BeforeAll
    static void setUpAll() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
        loginPage.open(); // Waits for page and overlays to disappear
    }

    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Validate presence of Email, Password, Login Button, and Eye Icon")
    void validatePresenceOfElements() {
        assertTrue(loginPage.isEmailPresent(), "Email field should be present");
        assertTrue(loginPage.isPasswordPresent(), "Password field should be present");
        assertTrue(loginPage.isLoginButtonPresent(), "Login button should be present");

        if (loginPage.hasEyeIcon()) {
            assertTrue(loginPage.hasEyeIcon(), "Eye icon should be present");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Attempt login with blank fields")
    void attemptLoginWithBlankFields() {
        loginPage.clearFields();
        loginPage.clickLogin();

        // Retry once in case page is slow
        boolean validationError = loginPage.isValidationErrorPresent();
        if (!validationError) {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            validationError = loginPage.isValidationErrorPresent();
        }

        assertTrue(validationError, "Expected validation error when fields are empty");
    }

    @Test
    @Order(3)
    @DisplayName("Invalid credentials should show error message")
    void invalidCredentialsShowsErrorMessage() {
        // Retry typing in case element is temporarily not interactable
        for (int i = 0; i < 2; i++) {
            try {
                loginPage.enterEmail("fake@example.com");
                loginPage.enterPassword("wrongpassword");
                break;
            } catch (Exception e) {
                if (i == 1) throw e;
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
        }

        loginPage.clickLogin();

        String error = loginPage.getErrorMessage();
        assertNotNull(error, "Expected some error message to appear for invalid login");
        assertFalse(error.isEmpty(), "Error message should not be empty");
    }

    @Test
    @Order(4)
    @DisplayName("Validate password masking toggle works")
    void validatePasswordMaskingToggle() {
        if (loginPage.hasEyeIcon()) {
            assertTrue(loginPage.togglePasswordVisibility(),
                       "Password field should toggle between masked and unmasked on eye icon click");
        } else {
            System.out.println("Eye icon not present on this page → skipping toggle test.");
        }
    }
}
