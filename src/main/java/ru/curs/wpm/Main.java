package ru.curs.wpm;

import ru.curs.wpm.ru.curs.wpm.cdp.CdpResourcesMonitor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main {


    public static void main(String args[]) throws InterruptedException {
        System.out.println("Hello, world");

        IWebResourcesMonitor webResourcesMonitor =
                /*/
                new WebResourcesMonitor(6);
                /*/
                new CdpResourcesMonitor();
        //*/
        List<ResourceWatcher> watchers = Arrays.asList(
                "https://www.marathonbet.com/su/live/popular",
                "https://www.marathonbet.com/su/live/22723",
                "https://www.marathonbet.com/su/popular/Football/?menu=11",
                "https://www.marathonbet.com/su/popular/Tennis/?menu=2398",
                //"https://time100.ru/online",
                //"https://ria.ru/",
                "https://www.ligastavok.ru/",
                //"http://online-bookmakers.ru/",
                //"https://betcity.ru/ru/",
                "https://www.fonbet.ru/#!/"
        ).stream().map(s -> new ResourceWatcher(s, webResourcesMonitor)).collect(Collectors.toList());
        Executors.newCachedThreadPool().invokeAll(watchers);
    }


}
