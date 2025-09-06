package com.janitri.pages;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Base URL
    private static final String BASE_URL = "https://dev-dash.janitri.in/";

    // Locators
    private final List<By> userIdLocators = Arrays.asList(
        By.id("formEmail"),
        By.name("email"),
        By.cssSelector("input.login-input-field[type='text']")
    );

    private final List<By> passwordLocators = Arrays.asList(
        By.id("formPassword"),
        By.name("password"),
        By.cssSelector("input.login-input-field[type='password']")
    );

    private final List<By> loginButtonLocators = Arrays.asList(
        By.cssSelector("button.login-button"),
        By.xpath("//button[contains(text(),'Log In')]")
    );

    private final List<By> eyeIconLocators = Arrays.asList(
        By.cssSelector("img.password-visible[alt*='Password']")
    );

    private final List<By> errorLocators = Arrays.asList(
        By.cssSelector(".error-message"),
        By.cssSelector("[role='alert']"),
        By.cssSelector(".MuiAlert-message")
    );

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** Open the page and wait until core elements appear */
    public void open() {
        driver.get(BASE_URL);
        wait.until(d -> findFirstPresent(userIdLocators) != null || findFirstPresent(passwordLocators) != null);
    }

    private WebElement findFirstPresent(List<By> locators) {
        for (By by : locators) {
            List<WebElement> els = driver.findElements(by);
            if (!els.isEmpty() && els.get(0).isDisplayed()) return els.get(0);
        }
        for (By by : locators) {
            List<WebElement> els = driver.findElements(by);
            if (!els.isEmpty()) return els.get(0);
        }
        return null;
    }

    private void scrollIntoView(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        } catch (Exception ignored) {}
    }

    public boolean isEmailPresent() {
        return findFirstPresent(userIdLocators) != null;
    }

    public boolean isPasswordPresent() {
        return findFirstPresent(passwordLocators) != null;
    }

    public boolean isLoginButtonPresent() {
        return findFirstPresent(loginButtonLocators) != null;
    }

    public boolean hasEyeIcon() {
        return findFirstPresent(eyeIconLocators) != null;
    }

    public void clearFields() {
        WebElement user = findFirstPresent(userIdLocators);
        WebElement pass = findFirstPresent(passwordLocators);
        if (user != null) {
            scrollIntoView(user);
            user.clear();
        }
        if (pass != null) {
            scrollIntoView(pass);
            pass.clear();
        }
    }

    public void enterEmail(String email) {
        WebElement user = findFirstPresent(userIdLocators);
        if (user == null) throw new NoSuchElementException("Email field not found");
        wait.until(ExpectedConditions.elementToBeClickable(user));
        scrollIntoView(user);
        user.click();
        user.clear();
        user.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement pass = findFirstPresent(passwordLocators);
        if (pass == null) throw new NoSuchElementException("Password field not found");
        wait.until(ExpectedConditions.elementToBeClickable(pass));
        scrollIntoView(pass);
        pass.click();
        pass.clear();
        pass.sendKeys(password);
    }

    public void clickLogin() {
        WebElement btn = findFirstPresent(loginButtonLocators);
        if (btn == null) throw new NoSuchElementException("Login button not found");
        wait.until(ExpectedConditions.elementToBeClickable(btn));
        scrollIntoView(btn);
        btn.click();
    }

    public boolean togglePasswordVisibility() {
        WebElement eye = findFirstPresent(eyeIconLocators);
        if (eye == null) return false;
        scrollIntoView(eye);
        eye.click();
        return true;
    }

    public boolean isValidationErrorPresent() {
        try {
            wait.until(d -> findFirstPresent(errorLocators) != null &&
                    !findFirstPresent(errorLocators).getText().isBlank());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            WebElement err = wait.until(d -> findFirstPresent(errorLocators));
            return err != null ? err.getText().trim() : "";
        } catch (TimeoutException e) {
            return "";
        }
    }
}
