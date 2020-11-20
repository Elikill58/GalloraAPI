package com.elikill58.galloraapi.spigot.impl.location;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.galloraapi.api.block.Block;
import com.elikill58.galloraapi.api.entity.Entity;
import com.elikill58.galloraapi.api.location.Difficulty;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.spigot.impl.block.SpigotBlock;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotEntityManager;

public class SpigotWorld extends World {

	private final org.bukkit.World w;
	
	public SpigotWorld(org.bukkit.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getName();
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return new SpigotBlock(w.getBlockAt(x, y, z));
	}

	@Override
	public Block getBlockAt(Location loc) {
		return new SpigotBlock(w.getBlockAt(loc.getBlockZ(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.getEntities().forEach((e) -> list.add(SpigotEntityManager.getEntity(e)));
		return list;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().name());
	}

	@Override
	public Object getDefault() {
		return w;
	}
}
