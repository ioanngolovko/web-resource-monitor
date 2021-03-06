package ru.curs.wpm;

import java.util.concurrent.ConcurrentHashMap;

public class WebResourcesMonitor implements IWebResourcesMonitor {

    private final ConcurrentHashMap<String, WebResource> resourceMap;

    public WebResourcesMonitor(int initCapacity) {
        resourceMap = new ConcurrentHashMap<>(initCapacity);
    }

    @Override
    public String getResourceContent(final String url) {
        WebResource webResource = null;
        try {
            webResource = resourceMap.computeIfAbsent(url, (u) -> new WebResource(u));
            return webResource.getContent();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (webResource != null) {
                webResource.close();
                resourceMap.put(url, new WebResource(url));
            }
            throw e;
        }
    }
}
