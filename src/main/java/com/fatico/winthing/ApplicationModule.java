package com.fatico.winthing;

import com.fatico.winthing.messaging.MessagingModule;
import com.fatico.winthing.windows.WindowsModule;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;

public class ApplicationModule extends AbstractModule {
    public static final String ConfigFile = "winthing.conf";

    @Override
    protected void configure() {
        bind(Gson.class).in(Singleton.class);

        install(new MessagingModule());
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            install(new WindowsModule());
            install(new com.fatico.winthing.systems.system.Module());
            install(new com.fatico.winthing.systems.keyboard.Module());
            install(new com.fatico.winthing.systems.desktop.Module());
        }
    }

    @Provides
    @Singleton
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    Config config() {
        Config cfg = ConfigFactory.load();
        String path = System.getProperty("user.dir") + File.separator + ConfigFile;

        File fp = new File(path);
        if (fp.exists()) {
            ConfigParseOptions options = ConfigParseOptions.defaults();
            options.setSyntax(ConfigSyntax.CONF);

            cfg = ConfigFactory.parseFile(fp, options).withFallback(cfg);
        }

        return cfg;
    }
}
