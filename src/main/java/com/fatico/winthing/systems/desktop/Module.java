package com.fatico.winthing.systems.desktop;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

public class Module extends PrivateModule {

    @Override
    protected void configure() {
        bind(DesktopService.class).in(Singleton.class);
        bind(DesktopController.class).asEagerSingleton();
        expose(DesktopService.class);
    }

}
