package com.elikill58.galloraapi.universal.permissions;

import com.elikill58.galloraapi.api.entity.Player;

public interface PermissionChecker {

	boolean hasPermission(Player player, String permission);
}
