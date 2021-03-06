package com.elikill58.galloraapi.spigot.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.galloraapi.spigot.SpigotAdapter;
import com.elikill58.galloraapi.spigot.listeners.ChannelListeners;
import com.elikill58.galloraapi.universal.Version;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

	public static List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object craftServer = server.getClass().getField("server").get(server);
			Object getted = craftServer.getClass().getMethod("getOnlinePlayers").invoke(craftServer);
			if (getted instanceof Player[])
				for (Player obj : (Player[]) getted)
					list.add(obj);
			else if (getted instanceof List)
				for (Object obj : (List<?>) getted)
					list.add((Player) obj);
			else
				System.out.println("Unknow getOnlinePlayers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Nullable
	public static Player getFirstOnlinePlayer() {
		List<Player> onlinePlayers = getOnlinePlayers();
		return onlinePlayers.isEmpty() ? null : onlinePlayers.iterator().next();
	}

	public static Effect getEffect(String effect) {
		Effect m = null;
		try {
			m = (Effect) Effect.class.getField(effect).get(Effect.class);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e1) {
			m = null;
		}
		return m;
	}
	
	public static Block getTargetBlock(Player p, int distance) {
		Material[] transparentItem = new Material[] {};
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				return (Block) p.getClass().getMethod("getTargetBlockExact", int.class).invoke(p, distance);
			} else {
				try {
					return (Block) p.getClass().getMethod("getTargetBlock", Set.class, int.class).invoke(p, (Set<Material>) Sets.newHashSet(transparentItem), distance);
				} catch (NoSuchMethodException e) {}
				try {
					HashSet<Byte> hashSet = new HashSet<>();
					for(Material m : transparentItem)
						hashSet.add((byte) m.getId());
					return (Block) p.getClass().getMethod("getTargetBlock", HashSet.class, int.class).invoke(p, hashSet, distance);
				} catch (NoSuchMethodException e) {}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	
	public static ItemStack getItemFromString(String s) {
		Preconditions.checkNotNull(s, "Error while creating item. The material is null.");
		try {
			String[] splitted = s.toUpperCase().split(":");
			String key = splitted[0];
			Material temp = null;
			try {
				temp = Material.valueOf(key);
			} catch (IllegalArgumentException e) {}
			if(temp == null && UniversalUtils.isInteger(key)) {
				try {
					temp = (Material) Material.class.getDeclaredMethod("getMaterial", int.class).invoke(null, Integer.parseInt(key));
				} catch (Exception e) {
					// method not found because of too recent version
				}
			}
			if(temp == null) {
				SpigotAdapter.getPlugin().getLogger().warning("Error while creating item. Cannot find item for " + s + ".");
				return null;
			}
			return splitted.length > 1 ? new ItemStack(temp, 1, Byte.parseByte(s.split(":")[1])) : new ItemStack(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void loadChannelInOut(JavaPlugin plugin, Messenger messenger, String channel, ChannelListeners event) {
		if (!messenger.getOutgoingChannels().contains(channel))
			messenger.registerOutgoingPluginChannel(plugin, channel);
		if (!messenger.getIncomingChannels().contains(channel))
			messenger.registerIncomingPluginChannel(plugin, channel, event);
	}
}
