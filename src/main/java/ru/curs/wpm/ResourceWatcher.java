package ru.curs.wpm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

class ResourceWatcher implements Callable<Void> {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final String url;
    final IWebResourcesMonitor wrm;
    String oldValue;

    ResourceWatcher(String url, IWebResourcesMonitor wrm) {
        this.url = url;
        this.wrm = wrm;
    }

    @Override
    public Void call() throws InterruptedException {
        while (true) {
            long start = System.nanoTime();
            String html = wrm.getResourceContent(url);
            long end = System.nanoTime();
            boolean changed = oldValue == null || !oldValue.equals(html);
            System.out.printf("%s: (%d) %s %s%n",
                    sdf.format(new Date()),
                    (int) ((end - start) / 1_000_000L),
                    changed ? "!=" : "==",
                    url
            );
            oldValue = html;
            Thread.sleep(1000);
        }
    }
}
