package ru.curs.wpm;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class WebResource implements AutoCloseable {

    public static BrowserWebDriverContainer getContainer() {

        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");

        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return new BrowserWebDriverContainer()
                .withDesiredCapabilities(desiredCapabilities);
    }

    private final BrowserWebDriverContainer chrome = getContainer();

    WebResource(String url) {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get(url);
    }

    String getContent() {
        return chrome.getWebDriver().getPageSource();
    }

    @Override
    public void close() {
        chrome.stop();
    }
}
