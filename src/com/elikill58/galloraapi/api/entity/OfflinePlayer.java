package com.elikill58.galloraapi.api.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.galloraapi.api.block.Block;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;

public interface OfflinePlayer extends Entity {

	public UUID getUniqueId();
	
	public boolean isOnline();
	
	public boolean hasPlayedBefore();
	
	@Override
	public default boolean isOnGround() {
		return true;
	}
	
	@Override
	public default Location getLocation() {
		return null;
	}
	
	@Override
	public default EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public default Location getEyeLocation() {
		return null;
	}

	@Override
	public default Vector getRotation() {
		return null;
	}

	@Override
	public default void sendMessage(String msg) {}
	
	@Override
	public default int getEntityId() {
		return 0;
	}
	
	@Override
	public default double getEyeHeight() {
		return 0;
	}
	
	@Override
	public default List<Block> getTargetBlock(int maxDistance) {
		return new ArrayList<Block>();
	}
}
