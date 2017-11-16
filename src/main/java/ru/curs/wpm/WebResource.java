package ru.curs.wpm;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class WebResource {

    private final BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withDesiredCapabilities(DesiredCapabilities.chrome());

    WebResource(String url) {
        chrome.start();
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get(url);
    }

    String getContent() {
        return chrome.getWebDriver().getPageSource();
    }

    void close() {
        chrome.stop();
    }
}
