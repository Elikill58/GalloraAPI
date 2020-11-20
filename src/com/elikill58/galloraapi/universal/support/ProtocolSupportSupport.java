package com.elikill58.galloraapi.universal.support;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.Version;

import protocolsupport.api.ProtocolSupportAPI;

public class ProtocolSupportSupport {

	public static Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(ProtocolSupportAPI.getProtocolVersion((org.bukkit.entity.Player) p.getDefault()).getId());
	}
}
