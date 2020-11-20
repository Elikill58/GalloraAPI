package com.elikill58.galloraapi.velocity;

import java.io.File;

import org.slf4j.Logger;

import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.Gallora;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "negativity", name = "Negativity", version = UniversalUtils.GALLORA_VERSION,
        description = "It's an Advanced AntiCheat Detection", authors = {"Elikill58", "RedNesto"})
public class VelocityNegativity extends VelocityPlugin {
	
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
	    Gallora.init(new VelocityAdapter(this));
	}

    @Subscribe
    public void onProxyDisable(ProxyShutdownEvent e) {
		Database.close();
	}

    public final File getDataFolder() {
        return new File("./plugins/Negativity");
    }
}
