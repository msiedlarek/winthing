package com.fatico.winthing.systems.keyboard;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

public class Module extends PrivateModule {

    @Override
    protected void configure() {
        bind(KeyboardService.class).in(Singleton.class);
        bind(KeyboardController.class).asEagerSingleton();
        expose(KeyboardService.class);
    }

}
