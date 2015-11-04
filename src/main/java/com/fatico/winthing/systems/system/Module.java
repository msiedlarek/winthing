package com.fatico.winthing.systems.system;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

public class Module extends PrivateModule {

    @Override
    protected void configure() {
        bind(SystemService.class).in(Singleton.class);
        bind(SystemController.class).asEagerSingleton();
        expose(SystemService.class);
    }

}
