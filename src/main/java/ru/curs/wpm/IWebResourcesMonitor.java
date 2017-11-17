package ru.curs.wpm;

public interface IWebResourcesMonitor {
    String getResourceContent(String url) throws InterruptedException;
}
