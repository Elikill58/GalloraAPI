package com.elikill58.galloraapi.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotFakePlayer;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotPlayer;
import com.elikill58.galloraapi.spigot.listeners.BlockListeners;
import com.elikill58.galloraapi.spigot.listeners.ChannelListeners;
import com.elikill58.galloraapi.spigot.listeners.CommandsListeners;
import com.elikill58.galloraapi.spigot.listeners.ElytraListeners;
import com.elikill58.galloraapi.spigot.listeners.EntityListeners;
import com.elikill58.galloraapi.spigot.listeners.FightManager;
import com.elikill58.galloraapi.spigot.listeners.InventoryListeners;
import com.elikill58.galloraapi.spigot.listeners.PlayersListeners;
import com.elikill58.galloraapi.spigot.packets.NegativityPacketManager;
import com.elikill58.galloraapi.spigot.utils.Utils;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.ProxyCompanionManager;
import com.elikill58.galloraapi.universal.Version;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.galloraapi.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.galloraapi.universal.utils.ReflectionUtils;

public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean hasBypass = false, isCraftBukkit = false;
	public static String CHANNEL_NAME_FML = "";
	private NegativityPacketManager packetManager;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this));
		Version v = Version.getVersion(Utils.VERSION);
		if (v.equals(Version.HIGHER))
			getLogger().warning("Unknow server version " + Utils.VERSION + " ! Some problems can appears.");
		else
			getLogger().info("Detected server version: " + v.name().toLowerCase() + " (" + Utils.VERSION + ")");
		
		packetManager = new NegativityPacketManager(this);
		new File(getDataFolder().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();
		if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists()) {
			getLogger().info("------ Negativity Information ------");
			getLogger().info("");
			getLogger().info(" > Thanks for downloading Negativity :)");
			getLogger().info("I'm trying to make the better anti-cheat has possible.");
			getLogger().info("If you get error/false positive, or just have suggestion, you can contact me via:");
			getLogger().info("Discord: @Elikill58#0743, @Elikill58 on twitter or in all other web site like Spigotmc ...");
			getLogger().info("");
			getLogger().info("------ Negativity Information ------");
			getConfig().options().copyDefaults();
			saveDefaultConfig();
		}
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			isCraftBukkit = false;
		} catch (ClassNotFoundException e) {
			isCraftBukkit = true;
		}
		SpigotFakePlayer.loadClass();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersListeners(), this);
		pm.registerEvents(new FightManager(), this);
		pm.registerEvents(new InventoryListeners(), this);
		pm.registerEvents(new BlockListeners(), this);
		pm.registerEvents(new EntityListeners(), this);
		if(v.isNewerOrEquals(Version.V1_9))
			pm.registerEvents(new ElytraListeners(), this);

		Messenger messenger = getServer().getMessenger();
		ChannelListeners channelListeners = new ChannelListeners();
		if (v.isNewerOrEquals(Version.V1_13)) {
			CHANNEL_NAME_FML = "negativity:fml";
		} else {
			CHANNEL_NAME_FML = "FML|HS";
		}
		loadChannelInOut(messenger, NegativityMessagesManager.CHANNEL_ID, channelListeners);
		loadChannelInOut(messenger, CHANNEL_NAME_FML, channelListeners);
		
		for (Player p : Utils.getOnlinePlayers())
			GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));

		loadCommand();
		
		NegativityAccountStorage.setDefaultStorage("file");
	}
	
	private void loadChannelInOut(Messenger messenger, String channel, ChannelListeners event) {
		if (!messenger.getOutgoingChannels().contains(channel))
			messenger.registerOutgoingPluginChannel(this, channel);
		if (!messenger.getIncomingChannels().contains(channel))
			messenger.registerIncomingPluginChannel(this, channel, event);
	}

	private void loadCommand() {
		CommandsListeners command = new CommandsListeners();
		PluginCommand negativity = getCommand("negativity");
		negativity.setExecutor(command);
		negativity.setTabCompleter(command);
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			GalloraPlayer.removeFromCache(p.getUniqueId());
		}
		Database.close();
		packetManager.getPacketManager().clear();
	}
	
	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}

	public static SpigotNegativity getInstance() {
		return INSTANCE;
	}

	public static void sendProxyPing(Player player) {
		ProxyCompanionManager.searchedCompanion = true;
		try {
			byte[] pingMessage = NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION));
			player.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, pingMessage);
		} catch (IOException ex) {
			SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not write ProxyPingMessage.", ex);
		}
	}

	public static void trySendProxyPing() {
		Iterator<? extends Player> onlinePlayers = Utils.getOnlinePlayers().iterator();
		if (onlinePlayers.hasNext()) {
			sendProxyPing(onlinePlayers.next());
		}
	}

	private Object getKnownCommands(Object object) {
		try {
			Field objectField = object.getClass().getDeclaredField("knownCommands");
			objectField.setAccessible(true);
			return objectField.get(object);
		} catch (NoSuchFieldException e) {
			Class<?> clazz = object.getClass();
			try {
				return clazz.getMethod("getKnownCommands").invoke(object);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void unRegisterBukkitCommand(PluginCommand cmd) {
		try {
			Object result = ReflectionUtils.getPrivateField(this.getServer().getPluginManager(), "commandMap");
			HashMap<?, ?> knownCommands = (HashMap<?, ?>) getKnownCommands((SimpleCommandMap) result);
			if (knownCommands.containsKey(cmd.getName()))
				knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases())
				if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName()))
					knownCommands.remove(alias);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPluginMessage(byte[] rawMessage) {
		Player player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			player.sendPluginMessage(getInstance(), NegativityMessagesManager.CHANNEL_ID, rawMessage);
		} else {
			getInstance().getLogger().severe("Could not send plugin message to proxy because there are no player online.");
		}
	}
}
