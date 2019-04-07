package com.fatico.winthing.logging;

import com.fatico.winthing.gui.WindowGui;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsoleLogger extends ConsoleAppender<ILoggingEvent> {
    private static final int LOG_SIZE = 50;
    private static ConcurrentLinkedQueue<String> events = new ConcurrentLinkedQueue<String>();

    public static String getEvents() {
        if (events.size() == 0) {
            return "";
        }

        String[] logs = new String[events.size()];
        events.toArray(logs);
        String result = Arrays.toString(logs);

        return result;
    }

    protected void append(ILoggingEvent event) {
        super.append(event);

        if (events.size() > LOG_SIZE) {
            events.remove();
        }

        byte[] data = encoder.encode(event);
        events.add(new String(data, Charset.forName("UTF-8")));

        WindowGui gui = WindowGui.getInstance();
        gui.reload();
    }
}
