package com.fatico.winthing.logging;

import ch.qos.logback.core.PropertyDefinerBase;
import com.fatico.winthing.Application;

public class FileLogEnabler extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        if (Application.debug()) {
            return "true";
        }

        return "false";
    }
}
