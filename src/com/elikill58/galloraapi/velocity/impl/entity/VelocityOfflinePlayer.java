package com.elikill58.galloraapi.velocity.impl.entity;

import java.util.UUID;

import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.elikill58.galloraapi.velocity.VelocityAdapter;

public class VelocityOfflinePlayer implements OfflinePlayer {

	private final UUID uuid;
	
	public VelocityOfflinePlayer(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isOnline() {
		return VelocityAdapter.getAdapter().getServer().getPlayer(uuid) != null;
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
