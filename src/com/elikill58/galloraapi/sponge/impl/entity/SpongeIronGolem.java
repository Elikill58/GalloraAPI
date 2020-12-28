package com.elikill58.galloraapi.sponge.impl.entity;

import com.elikill58.galloraapi.api.entity.Entity;
import com.elikill58.galloraapi.api.entity.EntityType;
import com.elikill58.galloraapi.api.entity.IronGolem;

public class SpongeIronGolem extends SpongeEntity<org.spongepowered.api.entity.living.golem.IronGolem> implements IronGolem {
	
	public SpongeIronGolem(org.spongepowered.api.entity.living.golem.IronGolem golem) {
		super(golem);
	}

	@Override
	public Entity getTarget() {
		return SpongeEntityManager.getEntity(entity.getTarget().orElse(null));
	}
	
	@Override
	public EntityType getType() {
		return EntityType.IRON_GOLEM;
	}
}
