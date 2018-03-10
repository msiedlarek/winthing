package com.fatico.winthing;

import com.fatico.winthing.gui.WinThingTray;
import com.fatico.winthing.messaging.Engine;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        try {
            final Injector injector = Guice.createInjector(new ApplicationModule());
            final Engine engine = injector.getInstance(Engine.class);
            final WinThingTray systemTray = injector.getInstance(WinThingTray.class);
            systemTray.initalise();
            engine.run();
        } catch (final Throwable throwable) {
            logger.error("Critical error.", throwable);
            System.exit(1);
        }
    }

}
