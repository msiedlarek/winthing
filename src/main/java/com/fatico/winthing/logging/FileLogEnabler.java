package com.fatico.winthing.logging;

import com.fatico.winthing.Application;

import ch.qos.logback.core.PropertyDefinerBase;

public class FileLogEnabler extends PropertyDefinerBase {

	@Override
	public String getPropertyValue() {
		if (Application.APPDEBUG) {
			return "true";
		}
		
		return "false";
	}
}
