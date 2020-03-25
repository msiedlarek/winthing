package com.fatico.winthing.systems.radeon;

import com.fatico.winthing.systems.radeon.jna.AtiAdl;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

public class Module extends PrivateModule {

    @Override
    protected void configure() {
        bind(AtiAdl.class).toInstance(AtiAdl.INSTANCE);
        bind(RadeonService.class).in(Singleton.class);
        bind(RadeonController.class).asEagerSingleton();
        expose(RadeonService.class);
    }

}
