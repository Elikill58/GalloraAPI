package com.elikill58.galloraapi.velocity;

import java.io.File;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.ProxyServer;

public abstract class VelocityPlugin {

    public abstract ProxyServer getServer();

	public abstract File getDataFolder();

	public abstract Logger getLogger();
}
