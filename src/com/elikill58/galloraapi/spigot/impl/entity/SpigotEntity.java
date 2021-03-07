package com.elikill58.galloraapi.spigot.impl.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.elikill58.galloraapi.api.entity.AbstractEntity;
import com.elikill58.galloraapi.api.entity.BoundingBox;
import com.elikill58.galloraapi.api.entity.EntityType;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;
import com.elikill58.galloraapi.spigot.impl.location.SpigotLocation;
import com.elikill58.galloraapi.spigot.impl.location.SpigotWorld;
import com.elikill58.galloraapi.spigot.utils.PacketUtils;
import com.elikill58.galloraapi.universal.Version;

public class SpigotEntity<E extends Entity> extends AbstractEntity {

	protected final E entity;
	
	public SpigotEntity(E entity) {
		this.entity = entity;
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isOp() {
		return entity.isOp();
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(entity.getLocation());
	}

	@Override
	public double getEyeHeight() {
		return entity.getHeight();
	}

	@Override
	public EntityType getType() {
		return EntityType.get(entity == null ? null : entity.getType().name());
	}

	@Override
	public E getDefault() {
		return entity;
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(msg);
	}

	@Override
	public String getName() {
		return entity.getName();
	}
	
	@Override
	public Location getEyeLocation() {
		if(entity instanceof LivingEntity) {
			org.bukkit.Location eye = ((LivingEntity) entity).getEyeLocation();
			return new SpigotLocation(new SpigotWorld(eye.getWorld()), eye.getX(), eye.getY(), eye.getZ());
		}
		return null;
	}
	
	@Override
	public Vector getRotation() {
		org.bukkit.util.Vector vec = entity.getLocation().getDirection();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public int getEntityId() {
		return entity.getEntityId();
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		try {
			Object bb = PacketUtils.getBoundingBox(entity);
			Class<?> clss = bb.getClass();
			if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
				double minX = clss.getField("minX").getDouble(bb);
				double minY = clss.getField("minY").getDouble(bb);
				double minZ = clss.getField("minZ").getDouble(bb);
				
				double maxX = clss.getField("maxX").getDouble(bb);
				double maxY = clss.getField("maxY").getDouble(bb);
				double maxZ = clss.getField("maxZ").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			} else {
				double minX = clss.getField("a").getDouble(bb);
				double minY = clss.getField("b").getDouble(bb);
				double minZ = clss.getField("c").getDouble(bb);
				
				double maxX = clss.getField("d").getDouble(bb);
				double maxY = clss.getField("e").getDouble(bb);
				double maxZ = clss.getField("f").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
