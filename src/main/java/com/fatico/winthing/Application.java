package com.fatico.winthing;

import com.fatico.winthing.gui.WindowGui;
import com.fatico.winthing.messaging.Engine;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private boolean debug = false;
    private static Application app = new Application();
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private void parseArgs(String[] args) {
        for (String arg : args) {
            if (arg.equals("-debug")) {
                debug = true;
            }
        }
    }

    public static boolean debug() {
        return app.debug;
    }

    public static void quit() {
        logger.info("Application terminated.");
        System.exit(0);
    }

    public static void main(final String[] args) {
        try {
            app.parseArgs(args);
            WindowGui gui = WindowGui.getInstance();
            gui.tray();

            final Injector injector = Guice.createInjector(new ApplicationModule());
            final Engine engine = injector.getInstance(Engine.class);
            engine.run();

        } catch (final Throwable throwable) {
            logger.error("Critical error.", throwable);
            System.exit(1);
        }
    }
}
