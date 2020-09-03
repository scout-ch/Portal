package ch.itds.pbs.portal.web.util;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class SeleniumHelper {

    private final WebDriver driver;
    public String currentTestClass = "";
    public String currentTestMethod = "";
    public int counter = 0;
    private String baseUrl;

    public SeleniumHelper(int port) {
        try {
            String envFfHost = System.getenv("SELENIUM-FIREFOX_HOST");
            String envFfPort = System.getenv("SELENIUM-FIREFOX_TCP_4444");
            Capabilities browserCapabilities = new FirefoxOptions();
            String seleniumHub = "http://" + (envFfHost != null ? envFfHost : "selenium") + ':' + (envFfPort != null ? envFfPort : 4444) + "/wd/hub";

            LoggingPreferences logs = new LoggingPreferences();
            logs.enable(LogType.BROWSER, Level.ALL);
            ((FirefoxOptions) browserCapabilities).setCapability(CapabilityType.LOGGING_PREFS, logs);

            baseUrl = "http://localhost:" + port;

            String localAddress = InetAddress.getLocalHost() != null ? InetAddress.getLocalHost().getHostAddress() : null;

            if (localAddress != null && localAddress.trim().length() > 0) {
                if (baseUrl != null && baseUrl.matches("/localhost/"))
                    baseUrl = baseUrl.replaceAll("localhost", localAddress);
                else
                    baseUrl = "http://" + localAddress + ":" + port;
                System.out.println("use new baseUrl: " + baseUrl);
            }

            driver = new RemoteWebDriver(new URL(seleniumHub), browserCapabilities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        driver.close();
    }

    public void navigateTo(String url) {
        driver.navigate()
                .to(baseUrl + url);
    }

    public void clickElement(WebElement element) {
        element.click();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void screenshot(String name) {

        byte[] screenshot = ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BYTES);

        try {
            Path path = Paths
                    .get("build/selenium-screenshots")
                    .resolve(String.format("%03d-%s-%s-%s.png",
                            ++counter,
                            currentTestClass,
                            currentTestMethod,
                            name));

            Files.createDirectories(path.getParent());
            Files.write(path, screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds visible mouse hack & console.log redirect to a visible div (bottom right)
     */
    public void enableDebugUtils() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript(new String(new ClassPathResource("mouse-follow.js").getInputStream().readAllBytes()));
            js.executeScript(new String(new ClassPathResource("inline-log.js").getInputStream().readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}