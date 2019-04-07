package com.fatico.winthing.logging;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fatico.winthing.gui.WindowGUI;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

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
		
		WindowGUI gui = WindowGUI.getInstance();
		gui.reload();
	}
}
