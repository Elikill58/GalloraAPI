package com.elikill58.galloraapi.sponge;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.google.inject.Inject;

@Plugin(id = "negativity", name = "Negativity", version = UniversalUtils.GALLORA_VERSION, description = "It's an Advanced AntiCheat Detection", authors = { "Elikill58", "RedNesto" }, dependencies = {
		@Dependency(id = "packetgate") })
public class SpongeNegativity extends SpongePlugin {

	public static SpongeNegativity INSTANCE;

	@Inject
	private PluginContainer plugin;
	@Inject
	public Logger logger;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	public PluginContainer getContainer() {
		return plugin;
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		INSTANCE = this;
		
		Adapter.setAdapter(new SpongeAdapter(this));
		
		plugin.getLogger().info("Negativity v" + plugin.getVersion().get() + " loaded.");
	}

	@Listener
	public void onGameReload(GameReloadEvent event) {
		Adapter.getAdapter().reload();
	}

	public Path getDataFolder() {
		return configDir;
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}
}
