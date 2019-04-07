package com.fatico.winthing.systems.system;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class SystemCommander {
    public static final String ConfigFile = "winthing.ini";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean isEnabled = false;
    private Map<String, String> whitelist = new HashMap<String, String>();

    public boolean isEnabled() {
        return isEnabled;
    }

    public String[] getList() {
        if (whitelist.keySet().size() == 0) {
            return null;
        }

        String[] keys = new String[whitelist.keySet().size()];
        whitelist.keySet().toArray(keys);

        return keys;
    }

    public String getCommand(String key) {
        return whitelist.get(key);
    }

    public int count() {
        return whitelist.size();
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public void parseConfig() {
        String path = System.getProperty("user.dir") + File.separator + ConfigFile;
        File fp = new File(path);
        if (!fp.exists()) {
            logger.warn("No whitelist found. Every command is allowed to execute on this device!");
            return;
        }

        try {
            StringJoiner joiner = new StringJoiner(", ");

            ConfigParseOptions options = ConfigParseOptions.defaults();
            options.setSyntax(ConfigSyntax.CONF);

            Config cfg = ConfigFactory.parseFile(fp, options);
            Set<String> map = cfg.root().keySet();
            for (String key : map) {
                whitelist.put(key, cfg.getString(key));
                joiner.add(key);
            }

            logger.info("Found whitelist of allowed commands to execute, using it...");
            logger.info("Allowed commands: [" + joiner.toString() + "]");

            isEnabled = true;
        } catch (Exception e) {
            logger.error("Unable to process whitelist file", e);
        }
    }
}
