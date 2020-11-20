package com.elikill58.galloraapi.spigot;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.FakePlayer;
import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.inventory.Inventory;
import com.elikill58.galloraapi.api.inventory.NegativityHolder;
import com.elikill58.galloraapi.api.item.ItemBuilder;
import com.elikill58.galloraapi.api.item.ItemRegistrar;
import com.elikill58.galloraapi.api.item.Material;
import com.elikill58.galloraapi.api.json.JSONObject;
import com.elikill58.galloraapi.api.json.parser.JSONParser;
import com.elikill58.galloraapi.api.json.parser.ParseException;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.api.plugin.ExternalPlugin;
import com.elikill58.galloraapi.api.yaml.config.Configuration;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotOfflinePlayer;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotPlayer;
import com.elikill58.galloraapi.spigot.impl.inventory.SpigotInventory;
import com.elikill58.galloraapi.spigot.impl.item.SpigotItemBuilder;
import com.elikill58.galloraapi.spigot.impl.item.SpigotItemRegistrar;
import com.elikill58.galloraapi.spigot.impl.location.SpigotLocation;
import com.elikill58.galloraapi.spigot.impl.plugin.SpigotExternalPlugin;
import com.elikill58.galloraapi.spigot.utils.PacketUtils;
import com.elikill58.galloraapi.spigot.utils.Utils;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Platform;
import com.elikill58.galloraapi.universal.account.NegativityAccountManager;
import com.elikill58.galloraapi.universal.account.SimpleAccountManager;
import com.elikill58.galloraapi.universal.logger.JavaLoggerAdapter;
import com.elikill58.galloraapi.universal.logger.LoggerAdapter;
import com.elikill58.galloraapi.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.galloraapi.universal.translation.TranslationProviderFactory;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;

public class SpigotAdapter extends Adapter {

	private final JavaPlugin pl;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(SpigotNegativity::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private final SpigotItemRegistrar itemRegistrar;
	private Configuration config;

	public SpigotAdapter(JavaPlugin pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(
				pl.getDataFolder().toPath().resolve("lang"), "Negativity", "CheatHover");
		this.logger = new JavaLoggerAdapter(pl.getLogger());
		this.itemRegistrar = new SpigotItemRegistrar();
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.SPIGOT;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void debug(String msg) {
		if (UniversalUtils.DEBUG)
			pl.getLogger().info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) {
		return pl.getResource("assets/negativity/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		SpigotNegativity.trySendProxyPing();
	}

	@Override
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
	@Override
	public String getPluginVersion() {
		return pl.getDescription().getVersion();
	}

	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Object data = new JSONParser().parse(response);
				if (data instanceof JSONObject) {
					JSONObject json = (JSONObject) data;
					Object isMcleaks = json.get("isMcleaks");
					if (isMcleaks != null) {
						return Boolean.getBoolean(isMcleaks.toString());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (org.bukkit.entity.Player temp : Utils.getOnlinePlayers())
			list.add(GalloraPlayer.getPlayer(temp.getUniqueId(), () -> new SpigotPlayer(temp)).getPlayer());
		return list;
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}

	@Override
	public double[] getTPS() {
		if(SpigotNegativity.isCraftBukkit) {
			return new double[] {20, 20, 20};
		} else {
			try {
				Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer");
				Object server = mcServer.getMethod("getServer").invoke(mcServer);
				return (double[]) server.getClass().getField("recentTps").get(server);
			} catch (Exception e) {
				getLogger().warn("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
				e.printStackTrace();
				return new double[] {20, 20, 20};
			}
		}
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return new SpigotLocation(w, x, y, z);
	}

	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpigotInventory(inventoryName, size, holder);
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		new ClickableText().addRunnableHoverEvent(message, hover, command).sendToPlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new SpigotItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new SpigotItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpigotItemBuilder(owner);
	}

	@Override
	public Player getPlayer(String name) {
		org.bukkit.entity.Player p = Bukkit.getPlayer(name);
		if(p == null)
			return null;
		return new SpigotPlayer(p);
	}

	@Override
	public Player getPlayer(UUID uuid) {
		org.bukkit.entity.Player p = Bukkit.getPlayer(uuid);
		if(p == null)
			return null;
		return new SpigotPlayer(p);
	}

	@SuppressWarnings("deprecation")
	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(name);
		if(p == null)
			return null;
		return new SpigotOfflinePlayer(p);
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		org.bukkit.OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
		if(p == null)
			return null;
		return new SpigotOfflinePlayer(p);
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		// TODO implement fake player on adapter
		return null;
	}
	
	@Override
	public boolean hasPlugin(String name) {
		return Bukkit.getPluginManager().getPlugin(name) != null;
	}
	
	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpigotExternalPlugin(Bukkit.getPluginManager().getPlugin(name));
	}
	
	@Override
	public void runSync(Runnable call) {
		Bukkit.getScheduler().runTask(pl, call);
	}
}