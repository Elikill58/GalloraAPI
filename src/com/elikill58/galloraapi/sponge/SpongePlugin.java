package com.elikill58.galloraapi.sponge;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

public abstract class SpongePlugin {

	public abstract PluginContainer getContainer();

	public abstract Path getDataFolder();

	public abstract Logger getLogger();
}
