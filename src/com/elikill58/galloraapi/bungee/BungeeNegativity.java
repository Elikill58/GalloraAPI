package com.elikill58.galloraapi.bungee;

import java.io.File;
import java.util.LinkedHashMap;

import com.elikill58.galloraapi.api.yaml.config.YamlConfiguration;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.Stats;
import com.elikill58.galloraapi.universal.Stats.StatsType;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeNegativity extends Plugin {

	@Override
	public void onEnable() {
		Adapter.setAdapter(new BungeeAdapter(this));

		new Metrics(this);

		getProxy().registerChannel(NegativityMessagesManager.CHANNEL_ID);
		PluginManager pluginManager = getProxy().getPluginManager();
		pluginManager.registerListener(this, new BungeeListeners());
		pluginManager.registerCommand(this, new BNegativityCommand());
		pluginManager.registerListener(this, new BNegativityCommand.TabCompleter());

		NegativityAccountStorage.setDefaultStorage("database");

		Stats.loadStats();
		Stats.updateStats(StatsType.ONLINE, 1 + "");
		try {
			Stats.updateStats(StatsType.PORT, ((LinkedHashMap<?, ?>) YamlConfiguration.load(new File(getDataFolder().getParentFile().getParentFile(), "config.yml"))
							.getList("listeners").get(0)).get("query_port") + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
	}
}
