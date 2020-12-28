package com.elikill58.galloraapi.api;

import com.elikill58.galloraapi.api.location.World;

public interface GalloraObject {
	
	/**
	 * Get default object which is abstracted by Negativity structure
	 * For example, when we use {@link World}, beside there is the Spigot/Sponge... world.
	 * We can get the default platform object, in the example, the platform world.
	 * 
	 * @return default object
	 */
	public Object getDefault();
}
