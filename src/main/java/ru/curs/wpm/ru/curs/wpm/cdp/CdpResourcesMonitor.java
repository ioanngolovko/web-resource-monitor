package ru.curs.wpm.ru.curs.wpm.cdp;

import ru.curs.wpm.IWebResourcesMonitor;

import java.util.concurrent.ConcurrentHashMap;

public class CdpResourcesMonitor implements IWebResourcesMonitor {

    private ConcurrentHashMap<String, ChromePageSession> monitors = new ConcurrentHashMap<>();

    @Override
    public String getResourceContent(String url) throws InterruptedException {
        ChromePageSession monitor = monitors.compute(url, (u, oldvalue) -> {
            if (oldvalue != null) {
                if (oldvalue.isHealthy())
                    return oldvalue;
                else {
                    System.out.printf("Session %s feels unhealthy. Close.%n", url);
                    oldvalue.close();
                }
            }
            try {
                return new ChromePageSession(url);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
        //get url to imitate real load
        monitor.getURL();
        return monitor.getCode();
    }
}
