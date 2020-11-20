package com.elikill58.galloraapi.velocity;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;

import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

@Plugin(id = "negativity", name = "Negativity", version = UniversalUtils.GALLORA_VERSION,
        description = "It's an Advanced AntiCheat Detection", authors = {"Elikill58", "RedNesto"})
public class VelocityNegativity {

	public static final LegacyChannelIdentifier NEGATIVITY_CHANNEL_ID = new LegacyChannelIdentifier(NegativityMessagesManager.CHANNEL_ID);
	
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityNegativity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public ProxyServer getServer() {
    	return server;
    }

    public Logger getLogger() {
    	return logger;
    }
    
	@Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    	getLogger().info("Loading Negativity");
	    server.getEventManager().register(this, new VelocityListeners());
	    server.getChannelRegistrar().register(NEGATIVITY_CHANNEL_ID);
	    
		Adapter.setAdapter(new VelocityAdapter(this));

		NegativityAccountStorage.setDefaultStorage("database");

    	getLogger().info("Negativity enabled");
	}

    @Subscribe
    public void onProxyDisable(ProxyShutdownEvent e) {
		Database.close();
	}

    public final InputStream getResourceAsStream(final String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }

    public final File getDataFolder() {
        return new File("./plugins/Negativity");
    }
}
