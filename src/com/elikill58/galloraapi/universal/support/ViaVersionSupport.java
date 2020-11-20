package com.elikill58.galloraapi.universal.support;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.Version;

import us.myles.ViaVersion.api.Via;

public class ViaVersionSupport {

	public static Version getPlayerVersion(Player p) {
		return Version.getVersionByProtocolID(Via.getAPI().getPlayerVersion(p.getUniqueId()));
	}
}
