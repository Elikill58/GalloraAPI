package com.elikill58.galloraapi.api.plugin;

import com.elikill58.galloraapi.api.GalloraObject;

public abstract class ExternalPlugin implements GalloraObject {
	
	/**
	 * Check if the plugin is enabled
	 * 
	 * @return true if the plugin is enabled
	 */
	public abstract boolean isEnabled();
}
