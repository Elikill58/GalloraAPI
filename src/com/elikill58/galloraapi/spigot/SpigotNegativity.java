package com.elikill58.galloraapi.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.galloraapi.universal.Gallora;

public class SpigotNegativity extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Gallora.init(new SpigotAdapter(this));
	}

	@Override
	public void onDisable() {
		Gallora.disable();
	}
}
