package com.fatico.winthing;

import com.fatico.winthing.messaging.MessagingModule;
import com.fatico.winthing.windows.WindowsModule;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Gson.class).in(Singleton.class);

        install(new MessagingModule());
        install(new WindowsModule());

        install(new com.fatico.winthing.systems.system.Module());
        install(new com.fatico.winthing.systems.keyboard.Module());
        install(new com.fatico.winthing.systems.desktop.Module());
    }

    @Provides
    @Singleton
    Config config() {
        return ConfigFactory.load();
    }

}
