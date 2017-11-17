package ru.curs.wpm.ru.curs.wpm.cdp;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.LinuxProcessManager;
import io.webfolder.cdp.ProcessManager;
import io.webfolder.cdp.WindowsProcessManager;
import io.webfolder.cdp.event.Events;
import io.webfolder.cdp.event.network.ResponseReceived;
import io.webfolder.cdp.exception.CdpException;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.network.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static java.io.File.pathSeparator;


public class ChromePageSession implements AutoCloseable {
    public static final String ERROR_STATUS = "error";
    private final boolean isWindows;
    private final Session session;
    private final Launcher launcher;
    private final String status;
    private boolean closed = false;

    private static final int WAIT_LOAD_TIMEOUT = 32000;
    public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36";

    public ChromePageSession(String targetUrl) throws InterruptedException {
        isWindows = ";".equals(pathSeparator);

        Launcher tempLauncher = null;
        SessionFactory sessionFactory = null;
        while (sessionFactory == null) {
            tempLauncher = getLauncher();
            List<String> params = new ArrayList<>();
            params.add("--headless");
            params.add("--disable-gpu");
            params.add(String.format("--user-agent=%s", USER_AGENT));
            try {
                if (!isWindows) {
                    params.add("--user-data-dir=/var/log/flute");
                }
                sessionFactory = tempLauncher.launch(params);
                System.out.printf("Connected to Chrome successfully for %s%n", targetUrl);
            } catch (CdpException e) {
                System.out.printf("Cannot connect to Chrome for %s, will retry: %s%n", targetUrl, e.getMessage());
                tempLauncher.getProcessManager().kill();
                sessionFactory = null;
            }
        }
        launcher = tempLauncher;
        session = sessionFactory.create();
        final CompletableFuture<String> futureStatus = new CompletableFuture<>();
        final CompletableFuture<Void> futureLoaded = new CompletableFuture<>();
        session.getCommand().getNetwork().enable();
        session.getCommand().getPage().enable();
        session.addEventListener((e, d) -> {
            if (Events.NetworkResponseReceived.equals(e)) {
                ResponseReceived rr = (ResponseReceived) d;
                Response response = rr.getResponse();
                futureStatus.complete(Integer.toString(response.getStatus()));
            } else if (Events.NetworkLoadingFailed.equals(e)) {
                if (futureStatus.complete(ERROR_STATUS))
                    System.out.printf("Browser failure: NetworkLoadingFailed for %s%n", targetUrl);
            } else if (Events.PageFrameStoppedLoading.equals(e)) {
                futureLoaded.complete(null);
            }
        });
        session.navigate(targetUrl);
        String result;
        try {
            futureLoaded.get(WAIT_LOAD_TIMEOUT, TimeUnit.MILLISECONDS);
            result = futureStatus.get(WAIT_LOAD_TIMEOUT, TimeUnit.MILLISECONDS).toString();
        } catch (ExecutionException | TimeoutException e) {
            System.out.printf("Browser failure: pageLoading for %s (%s)%n", targetUrl, e.getMessage());
            result = ERROR_STATUS;
        }
        status = result;

        if (ERROR_STATUS.equals(status))
            close();
    }

    public static <T> Optional<T> retry(Supplier<T> supplier, int count, int pause) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            T result = supplier.get();
            if (result != null) {
                if (i > 0) {
                    System.out.printf("Success after %d retries%n", i);
                }
                return Optional.of(result);
            }
            Thread.sleep(pause);
        }
        return Optional.empty();
    }

    private Launcher getLauncher() {
        //System.setProperty("chrome_binary", "/opt/google/chrome-unstable/chrome");
        ProcessManager processManager;
        if (isWindows) {
            //processManager = new WindowsProcessManager();
            processManager = null;
        } else {
            processManager = new LinuxProcessManager();
        }
        Launcher result = new Launcher(SparePortProvider.INSTANCE.getPort());
        if (processManager != null)
            result.setProcessManager(processManager);
        return result;
    }

    public void close() {
        if (!closed) {
            try {
                session.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                launcher.getProcessManager().kill();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            closed = true;
        }
    }


    public String getCode() throws InterruptedException {
        return retry(
                () -> session.evaluate("document.body.outerHTML"),
                5,
                1000).map(Object::toString).orElse("");
    }

    public String getURL() throws InterruptedException {
        return retry(
                () -> session.evaluate("window.location.href"),
                5,
                1000).map(Object::toString).orElse("");
    }

    public boolean isHealthy() {
        try {
            return session.isConnected() && launcher.launched();
        } catch (Throwable e) {
            return false;
        }
    }

    public String getStatus() {
        return status;
    }
}
