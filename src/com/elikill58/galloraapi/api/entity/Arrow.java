package com.elikill58.galloraapi.api.entity;

public interface Arrow extends Entity {
	
	/**
	 * Get the entity which shoot the arrow
	 * 
	 * @return entity which shoot
	 */
	public Entity getShooter();
}
