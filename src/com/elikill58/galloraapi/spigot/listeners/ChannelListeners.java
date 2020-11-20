package com.elikill58.galloraapi.spigot.listeners;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.spigot.SpigotAdapter;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotPlayer;
import com.elikill58.galloraapi.universal.ProxyCompanionManager;
import com.elikill58.galloraapi.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessage;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessagesManager;
import com.elikill58.galloraapi.universal.pluginMessages.ProxyPingMessage;

public class ChannelListeners implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] data) {
		if (channel.equalsIgnoreCase(SpigotAdapter.channelFmlName) && data[0] == 2) {
			GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p)).MODS.putAll(getModData(data));
			return;
		}

		if (!channel.toLowerCase(Locale.ROOT).contains("negativity")) {
			return;
		}

		GalloraMessage message;
		try {
			message = GalloraMessagesManager.readMessage(data);
			if (message == null) {
				String warnMessage = String.format("Received unknown plugin message. Channel %s send to %s.", channel, p);
				SpigotAdapter.getPlugin().getLogger().warning(warnMessage);
				return;
			}
		} catch (IOException e) {
			SpigotAdapter.getPlugin().getLogger().log(Level.SEVERE, "Could not read plugin message.", e);
			return;
		}

		if (message instanceof ProxyPingMessage) {
			ProxyPingMessage pingMessage = (ProxyPingMessage) message;
			ProxyCompanionManager.foundCompanion(pingMessage.getProtocol());
		} else if (message instanceof ClientModsListMessage) {
			ClientModsListMessage modsMessage = (ClientModsListMessage) message;
			GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
			np.MODS.clear();
			np.MODS.putAll(modsMessage.getMods());
		} else {
			SpigotAdapter.getPlugin().getLogger().warning("Received unexpected plugin message " + message.getClass().getName());
		}
	}

	private HashMap<String, String> getModData(byte[] data) {
		HashMap<String, String> mods = new HashMap<>();
		boolean store = false;
		String tempName = null;
		for (int i = 2; i < data.length; store = !store) {
			int end = i + data[i] + 1;
			byte[] range = Arrays.copyOfRange(data, i + 1, end);
			String string = new String(range);
			if (store)
				mods.put(tempName, string);
			else
				tempName = string;
			i = end;
		}
		return mods;
	}
}
