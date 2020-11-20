package com.elikill58.galloraapi.universal.permissions;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.Adapter;

public class DefaultPermissionChecker implements PermissionChecker {

	@Override
	public boolean hasPermission(Player player, String permission) {
		return player.hasPermission(Adapter.getAdapter().getConfig().getString("Permissions." + permission + ".default", permission));
	}

}
