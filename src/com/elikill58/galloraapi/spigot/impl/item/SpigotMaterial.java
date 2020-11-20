package com.elikill58.galloraapi.spigot.impl.item;

import com.elikill58.galloraapi.api.item.Material;

public class SpigotMaterial extends Material {

	private final org.bukkit.Material type;
	
	public SpigotMaterial(org.bukkit.Material type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return type.isSolid();
	}

	@Override
	public String getId() {
		return type.name();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isTransparent() {
		return type.isTransparent();
	}

	@Override
	public Object getDefault() {
		return type;
	}
}
