package com.elikill58.galloraapi.universal.permissions;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.Adapter;

public abstract class BasePlatformPermissionChecker implements PermissionChecker {

	@Override
	public final boolean hasPermission(Player player, String permission) {
		String platformPerm = Adapter.getAdapter().getConfig().getString("Permissions." + permission + ".default");
		if (platformPerm == null) {
			return false;
		}

		return doPlatformCheck(player, platformPerm);
	}

	protected abstract boolean doPlatformCheck(Player player, String platformPerm);
}
