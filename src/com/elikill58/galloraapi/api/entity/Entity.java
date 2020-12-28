package com.elikill58.galloraapi.api.entity;

import java.util.List;

import com.elikill58.galloraapi.api.block.Block;
import com.elikill58.galloraapi.api.commands.CommandSender;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;

public interface Entity extends CommandSender {

	public List<Block> getTargetBlock(int maxDistance);
	
	public boolean isOnGround();
	public boolean isOp();
	
	public Location getLocation();
	
	public double getEyeHeight();
	
	public Location getEyeLocation();
	
	public Vector getRotation();
	
	public EntityType getType();
	
	public int getEntityId();

}
