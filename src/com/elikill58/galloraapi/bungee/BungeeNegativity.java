package com.elikill58.galloraapi.bungee;

import com.elikill58.galloraapi.universal.Gallora;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeNegativity extends Plugin {

	@Override
	public void onEnable() {
		Gallora.init(new BungeeAdapter(this));
	}

	@Override
	public void onDisable() {
		Gallora.disable();
	}
}
