package ru.curs.wpm;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main {

    public static void main(String args[]) {


        WebResourcesMonitor webResourcesMonitor = new WebResourcesMonitor(6);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);

        Function<String, String> getResourceContent = (url) -> {
            String result = webResourcesMonitor.getResourceContent(url);
            System.out.println(url);
            return result;
        };

        List<String> urls = Arrays.asList(
                "https://time100.ru/online",
                "https://ria.ru/",
                "https://www.ligastavok.ru/",
                "http://online-bookmakers.ru/",
                "https://betcity.ru/ru/",
                "https://www.fonbet.ru/#!/"
        );

        urls.forEach(
                (url) -> executor.scheduleAtFixedRate(
                        () -> getResourceContent.apply(url),
                        0,
                        1,
                        TimeUnit.SECONDS
                )
        );

        while (true) {}
    }


}
