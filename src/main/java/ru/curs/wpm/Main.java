package ru.curs.wpm;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {


    public static void main(String args[]) throws InterruptedException {
        System.out.println("Hello, world");

        IWebResourcesMonitor webResourcesMonitor = new WebResourcesMonitor(6);

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
