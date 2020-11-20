package com.elikill58.galloraapi.universal.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.commands.CommandSender;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.account.NegativityAccount;

public class Perm {

	public static final String ADMIN = "admin";
	public static final String CHECK = "check";
	public static final String SHOW_ALERT = "showAlert";
	public static final String SHOW_REPORT = "showReport";
	public static final String VERIF = "verif";
	public static final String MANAGE_CHEAT = "manageCheat";
	public static final String RELOAD = "reload";
	public static final String REPORT_WAIT = "report_wait";
	public static final String REPORT = "report";
	public static final String BAN = "ban";
	public static final String UNBAN = "unban";
	public static final String MOD = "mod";
	public static final String LANG = "lang";
	public static final String BYPASS_ALL = "bypass.all";
	public static final String BYPASS_BAN = "bypass.ban";

	public static final String PLATFORM_CHECKER = "platform";

	private static String checkerId = PLATFORM_CHECKER;
	private static final Map<String, PermissionChecker> checkers = new HashMap<>();

	public static boolean hasPerm(CommandSender sender, String perm) {
		if(sender instanceof Player) {
			PermissionChecker checker = getActiveChecker();
			return checker != null && checker.hasPermission((Player) sender, perm);
		} else
			return true;
	}

	public static boolean hasPerm(GalloraPlayer np, String perm) {
		PermissionChecker checker = getActiveChecker();
		return checker != null && checker.hasPermission(np.getPlayer(), perm);
	}

	public static boolean hasPerm(NegativityAccount np, String perm) {
		PermissionChecker checker = getActiveChecker();
		return checker != null && checker.hasPermission(Adapter.getAdapter().getPlayer(np.getPlayerId()), perm);
	}

	public static boolean hasPerm(UUID uuid, String perm) {
		PermissionChecker checker = getActiveChecker();
		return checker != null && checker.hasPermission(Adapter.getAdapter().getPlayer(uuid), perm);
	}

	@Nullable
	public static PermissionChecker getActiveChecker() {
		return checkers.get(checkerId);
	}

	public static void registerChecker(String checkerId, PermissionChecker checker) {
		checkers.put(checkerId, checker);
	}

	public static void init() {
		checkerId = Adapter.getAdapter().getConfig().getString("Permissions.checker");
		
		registerChecker(PLATFORM_CHECKER, new DefaultPermissionChecker());
	}
}
