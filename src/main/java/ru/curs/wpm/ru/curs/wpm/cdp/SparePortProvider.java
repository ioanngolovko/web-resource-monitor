package ru.curs.wpm.ru.curs.wpm.cdp;

import java.io.IOException;
import java.net.ServerSocket;

public class SparePortProvider {

    public static final SparePortProvider INSTANCE = new SparePortProvider();

    private static final int LOW_PORT = 8090;
    private static final int HI_PORT = 12900;
    private int nextPort = LOW_PORT;

    public synchronized int getPort() {
        for (int port = nextPort; port <= HI_PORT; port++) {
            if (tryPort(port)) return port;
        }
        for (int port = LOW_PORT; port < nextPort; port++) {
            if (tryPort(port)) return port;
        }
        throw new IllegalStateException("No availiable ports found");
    }

    private boolean tryPort(int port) {
        try {
            try (ServerSocket s = new ServerSocket(port)) {
                nextPort = port + 1;
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
    }

}

