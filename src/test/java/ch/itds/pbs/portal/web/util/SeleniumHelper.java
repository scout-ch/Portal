package ch.itds.pbs.portal.web.util;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            FirefoxOptions browserCapabilities = new FirefoxOptions();
            browserCapabilities.setHeadless(true);
            browserCapabilities.setAcceptInsecureCerts(true);
            browserCapabilities.addArguments("use-fake-device-for-media-stream");
            browserCapabilities.addArguments("use-fake-ui-for-media-stream");

            String seleniumHub = "http://" + (envFfHost != null ? envFfHost : "selenium") + ':' + (envFfPort != null ? envFfPort : 4444) + "/wd/hub";

            String baseUrlProtocol = "http";

            baseUrl = baseUrlProtocol + "://localhost:" + port;

            String localAddress = InetAddress.getLocalHost() != null ? InetAddress.getLocalHost().getHostAddress() : null;

            if (localAddress != null && localAddress.trim().length() > 0) {
                try (final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    localAddress = socket.getLocalAddress().getHostAddress();
                }

                if (baseUrl.matches("/localhost/"))
                    baseUrl = baseUrl.replaceAll("localhost", localAddress);
                else
                    baseUrl = baseUrlProtocol + "://" + localAddress + ":" + port;
                System.out.println("use new baseUrl: " + baseUrl);
            }

            driver = new RemoteWebDriver(new URL(seleniumHub), browserCapabilities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        driver.quit();
        //driver.close();
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


        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

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
