package ru.curs.wpm;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class WebResource {

    public static  BrowserWebDriverContainer getContainer (){
        return  new BrowserWebDriverContainer()
                .withDesiredCapabilities(DesiredCapabilities.chrome());
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

    void close() {
        chrome.stop();
    }
}
