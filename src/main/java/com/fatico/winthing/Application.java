package com.fatico.winthing;

import com.fatico.winthing.gui.WindowGUI;
import com.fatico.winthing.messaging.Engine;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
	public static boolean APPDEBUG = false;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        try {
        	parseArgs(args);
        	
        	WindowGUI gui = WindowGUI.getInstance();
        	gui.tray();
        	 
        	final Injector injector = Guice.createInjector(new ApplicationModule());
        	final Engine engine = injector.getInstance(Engine.class);
        	engine.run();
        	
        } catch (final Throwable throwable) {
            logger.error("Critical error.", throwable);
            System.exit(1);
        }
    }
    
    public static void parseArgs(String[] args) {
    	for (String arg : args) {
    		if (arg.equals("-debug")) {
    			APPDEBUG = true;
    		}
    	}
    }
}
