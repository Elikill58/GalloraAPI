package com.elikill58.galloraapi.bungee.impl.entity;

import java.util.UUID;

import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;

import net.md_5.bungee.api.ProxyServer;

public class BungeeOfflinePlayer implements OfflinePlayer {

	private final UUID uuid;
	
	public BungeeOfflinePlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isOnline() {
		return ProxyServer.getInstance().getPlayer(uuid) != null;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public String getName() {
		return UniversalUtils.getPlayerName(uuid);
	}

	@Override
	public Object getDefault() {
		return uuid;
	}
		
	
}
