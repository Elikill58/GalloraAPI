package com.elikill58.galloraapi.universal;

import java.util.StringJoiner;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.events.EventManager;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.galloraapi.universal.permissions.Perm;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;

public class Gallora {

	public static boolean protocolSupportSupport = false, viaVersionSupport = false;

	public static void init(Adapter ada) {
		Adapter.setAdapter(ada);
		ada.getLogger().info("Thanks for using GalloraAPI <3");

		DefaultConfigValue.init();
		Database.init();
		Perm.init();
		TranslatedMessages.init();
		NegativityAccountStorage.init();
		EventManager.load();
		UniversalUtils.init();
		
		StringJoiner supportedPluginName = new StringJoiner(", ");
		
		if (ada.hasPlugin("ViaVersion")) {
			viaVersionSupport = true;
			supportedPluginName.add("ViaVersion");
		}
		
		if (ada.hasPlugin("ProtocolSupport")) {
			protocolSupportSupport = true;
			supportedPluginName.add("ProtocolSupport");
		}
		
		if (supportedPluginName.length() > 0) {
			ada.getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}
	}
	
	public static void disable() {
		Adapter ada = Adapter.getAdapter();
		ada.getOnlinePlayersUUID().forEach(GalloraPlayer::removeFromCache);;
		ada.disable();
		Database.close();
	}
}
