package com.elikill58.galloraapi.sponge.impl.entity;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import com.elikill58.galloraapi.api.entity.AbstractEntity;
import com.elikill58.galloraapi.api.entity.EntityType;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;
import com.elikill58.galloraapi.sponge.impl.location.SpongeLocation;
import com.elikill58.galloraapi.sponge.impl.location.SpongeWorld;
import com.flowpowered.math.vector.Vector3d;

public class SpongeEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	private final SpongeLocation loc;
	
	public SpongeEntity(E e) {
		this.entity = e;
		this.loc = new SpongeLocation(e.getLocation());
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public double getEyeHeight() {
		// TODO implement getEyeHeight
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getType().getId());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		if (entity instanceof MessageReceiver) {
			((MessageReceiver) entity).sendMessage(Text.of(msg));
		}
	}

	@Override
	public String getName() {
		return entity.get(Keys.DISPLAY_NAME).orElse(Text.of(entity.getType().getName())).toPlain();
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = entity.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(entity.getRotation());
		return new SpongeLocation(new SpongeWorld(entity.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = entity.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return 0;
	}
}
