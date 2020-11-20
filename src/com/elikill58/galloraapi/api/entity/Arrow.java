package com.elikill58.galloraapi.api.entity;

public abstract class Arrow extends Entity {
	
	/**
	 * Get the entity which shoot the arrow
	 * 
	 * @return entity which shoot
	 */
	public abstract Entity getShooter();
}
