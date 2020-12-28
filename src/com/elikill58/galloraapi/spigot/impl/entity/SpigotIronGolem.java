package com.elikill58.galloraapi.spigot.impl.entity;

import com.elikill58.galloraapi.api.entity.Entity;
import com.elikill58.galloraapi.api.entity.EntityType;
import com.elikill58.galloraapi.api.entity.IronGolem;

public class SpigotIronGolem extends SpigotEntity<org.bukkit.entity.IronGolem> implements IronGolem {
	
	public SpigotIronGolem(org.bukkit.entity.IronGolem golem) {
		super(golem);
	}

	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}

	@Override
	public Entity getTarget() {
		return entity.getTarget() == null ? null : SpigotEntityManager.getEntity((org.bukkit.entity.Entity) entity.getTarget());
	}
}
