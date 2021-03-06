package com.elikill58.galloraapi.api.location;

import java.util.List;

import com.elikill58.galloraapi.api.GalloraObject;
import com.elikill58.galloraapi.api.block.Block;
import com.elikill58.galloraapi.api.entity.Entity;

public abstract class World implements GalloraObject {

	/**
	 * Get the world name
	 * 
	 * @return the world name
	 */
	public abstract String getName();

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block
	 */
	public abstract Block getBlockAt(int x, int y, int z);

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param loc the block location
	 * @return the founded block
	 */
	public abstract Block getBlockAt(Location loc);
	
	/**
	 * Get all entities on this world
	 * 
	 * @return collection of world entities
	 */
	public abstract List<Entity> getEntities();

	/**
	 * Get the world difficulty
	 * 
	 * @return the world difficulty
	 */
	public abstract Difficulty getDifficulty();
}
