package com.fatico.winthing.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fatico.winthing.Application;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsoleLogger extends ConsoleAppender<ILoggingEvent> {
    private static final int LOG_SIZE = 50;
    private static final ConcurrentLinkedQueue<String> events = new ConcurrentLinkedQueue<String>();

    public static String getEvents() {
        if (events.size() == 0) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("");
        Iterator<String> iterator = events.iterator();
        while (iterator.hasNext()) {
            joiner.add(iterator.next());
        }

        String result = joiner.toString();

        return result;
    }

    protected void append(ILoggingEvent event) {
        super.append(event);

        if (events.size() > LOG_SIZE) {
            events.remove();
        }

        byte[] data = encoder.encode(event);
        events.add(new String(data, Charset.forName("UTF-8")));

        Application.getApp().reload();
    }
}
