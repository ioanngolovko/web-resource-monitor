package ru.curs.wpm;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ResourceWatcher implements Callable<Void> {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final String url;
    final WebResourcesMonitor wrm;
    String oldValue;

    ResourceWatcher(String url, WebResourcesMonitor wrm) {
        this.url = url;
        this.wrm = wrm;

    }


    @Override
    public Void call() throws InterruptedException {
        while (true) {
            String html = wrm.getResourceContent(url);
            if (oldValue == null || !oldValue.equals(html)) {
                System.out.printf("%s: resource changed on %s%n",
                        sdf.format(new Date()), url
                );
            } else {
                System.out.printf("%s: resource NOT changed on %s%n",
                        sdf.format(new Date()), url
                );
            }
            oldValue = html;
            Thread.sleep(1000);
        }
    }
}

public class Main {


    public static void main(String args[]) throws InterruptedException {
        System.out.println("Hello, world");

        WebResourcesMonitor webResourcesMonitor = new WebResourcesMonitor(6);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

        Function<String, String> getResourceContent = (url) -> {
            String result = webResourcesMonitor.getResourceContent(url);
            System.out.println(url);
            return result;
        };

        List<ResourceWatcher> watchers = Arrays.asList(
                "https://time100.ru/online",
                "https://ria.ru/",
                "https://www.ligastavok.ru/",
                "http://online-bookmakers.ru/",
                "https://betcity.ru/ru/",
                "https://www.fonbet.ru/#!/"
        ).stream().map(s -> new ResourceWatcher(s, webResourcesMonitor)).collect(Collectors.toList());


        Executors.newCachedThreadPool().invokeAll(watchers);

    }


}
