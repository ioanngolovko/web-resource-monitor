package ru.curs.wpm;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

class ResourceWatcher implements Runnable {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final String url;
    final WebResourcesMonitor wrm;
    String oldValue;

    ResourceWatcher(String url, WebResourcesMonitor wrm) {
        this.url = url;
        this.wrm = wrm;

    }

    @Override
    public void run() {
        String html = wrm.getResourceContent(url);
        if (oldValue == null || !oldValue.equals(html)) {
            System.out.printf("%s: resource changed on %s%n",
                    sdf.format(new Date()), url
            );
        }
        oldValue = html;
    }
}

public class Main {


    public static void main(String args[]) {
        System.out.println("Hello, world");

        WebResourcesMonitor webResourcesMonitor = new WebResourcesMonitor(6);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

        Function<String, String> getResourceContent = (url) -> {
            String result = webResourcesMonitor.getResourceContent(url);
            System.out.println(url);
            return result;
        };

        Stream<ResourceWatcher> watchers = Arrays.asList(
                "https://time100.ru/online",
                "https://ria.ru/",
                "https://www.ligastavok.ru/",
                "http://online-bookmakers.ru/",
                "https://betcity.ru/ru/",
                "https://www.fonbet.ru/#!/"
        ).stream().map(s->new ResourceWatcher(s, webResourcesMonitor));

        watchers.forEach(
                (watcher) -> executor.scheduleAtFixedRate(
                        watcher,
                        0,
                        1,
                        TimeUnit.SECONDS
                )
        );

        while (true) {
        }
    }


}
