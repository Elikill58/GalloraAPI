package com.elikill58.galloraapi.sponge.impl.entity;

import com.elikill58.galloraapi.api.entity.Arrow;
import com.elikill58.galloraapi.api.entity.Entity;
import com.elikill58.galloraapi.api.entity.EntityType;

public class SpongeArrow extends SpongeEntity<org.spongepowered.api.entity.projectile.arrow.Arrow> implements Arrow {

	public SpongeArrow(org.spongepowered.api.entity.projectile.arrow.Arrow arrow) {
		super(arrow);
	}
	
	@Override
	public Entity getShooter() {
		return SpongeEntityManager.getProjectile(entity.getShooter());
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}
}
