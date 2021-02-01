package ch.itds.pbs.portal.web.util;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

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

    /**
     * Create a screenshot with name prefixed by count, test class and test method
     *
     * @param name
     */
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

}
