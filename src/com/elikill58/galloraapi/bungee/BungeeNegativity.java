package com.elikill58.galloraapi.bungee;

import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeNegativity extends Plugin {

	@Override
	public void onEnable() {
		Adapter.setAdapter(new BungeeAdapter(this));

		getProxy().registerChannel(NegativityMessagesManager.CHANNEL_ID);
		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BungeeListeners());

		NegativityAccountStorage.setDefaultStorage("database");
	}

	@Override
	public void onDisable() {
		Database.close();
	}
}
