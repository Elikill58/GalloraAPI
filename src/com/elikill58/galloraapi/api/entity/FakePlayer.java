package com.elikill58.galloraapi.api.entity;

import java.util.UUID;

public interface FakePlayer extends Entity {


	/**
	 * Show the fake player to the specified online player
	 * 
	 * @param p THe player who will see the entity
	 * @return this
	 */
	public void show(Player p);

	/**
	 * Hide the fake player to the specified online player
	 * 
	 * @param p The player that will not see it
	 */
	public void hide(Player p);


	/**
	 *  Get the entity ID of the fake player.
	 *  Alone method to check entity
	 * 
	 * @return the entity ID
	 */
	public int getEntityId();
	
	/**
	 * Get Unique ID of the fake player
	 * 
	 * @return the player's uuid
	 */
	public UUID getUUID();
	
	@Override
	public default void sendMessage(String msg) {}

	@Override
	public default EntityType getType() {
		return EntityType.PLAYER;
	}
}
