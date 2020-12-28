package com.elikill58.galloraapi.sponge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.FakePlayer;
import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.inventory.Inventory;
import com.elikill58.galloraapi.api.inventory.NegativityHolder;
import com.elikill58.galloraapi.api.item.ItemBuilder;
import com.elikill58.galloraapi.api.item.ItemRegistrar;
import com.elikill58.galloraapi.api.item.Material;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.api.plugin.ExternalPlugin;
import com.elikill58.galloraapi.api.timers.Scheduler;
import com.elikill58.galloraapi.api.yaml.config.Configuration;
import com.elikill58.galloraapi.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.galloraapi.sponge.impl.entity.SpongeFakePlayer;
import com.elikill58.galloraapi.sponge.impl.entity.SpongeOfflinePlayer;
import com.elikill58.galloraapi.sponge.impl.entity.SpongePlayer;
import com.elikill58.galloraapi.sponge.impl.inventory.SpongeInventory;
import com.elikill58.galloraapi.sponge.impl.item.SpongeItemBuilder;
import com.elikill58.galloraapi.sponge.impl.item.SpongeItemRegistrar;
import com.elikill58.galloraapi.sponge.impl.location.SpongeLocation;
import com.elikill58.galloraapi.sponge.impl.plugin.SpongeExternalPlugin;
import com.elikill58.galloraapi.sponge.impl.timers.SpongeScheduler;
import com.elikill58.galloraapi.sponge.listeners.BlockListeners;
import com.elikill58.galloraapi.sponge.listeners.EntityListeners;
import com.elikill58.galloraapi.sponge.listeners.FightManager;
import com.elikill58.galloraapi.sponge.listeners.InventoryListeners;
import com.elikill58.galloraapi.sponge.listeners.PlayersListeners;
import com.elikill58.galloraapi.sponge.packets.NegativityPacketManager;
import com.elikill58.galloraapi.sponge.utils.Utils;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Platform;
import com.elikill58.galloraapi.universal.ProxyCompanionManager;
import com.elikill58.galloraapi.universal.account.NegativityAccountManager;
import com.elikill58.galloraapi.universal.account.SimpleAccountManager;
import com.elikill58.galloraapi.universal.logger.LoggerAdapter;
import com.elikill58.galloraapi.universal.logger.Slf4jLoggerAdapter;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessage;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessagesManager;
import com.elikill58.galloraapi.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.galloraapi.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.galloraapi.universal.translation.TranslationProviderFactory;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public class SpongeAdapter extends Adapter {

	public static SpongeAdapter getAdapter() {
		return (SpongeAdapter) adapter;
	}

	public static boolean hasPacketGate = false;
	public static RawDataChannel channel = null, fmlChannel = null;
	
	private final LoggerAdapter logger;
	private final SpongePlugin plugin;
	private Configuration config;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Server(this::sendPluginMessage);
	private final TranslationProviderFactory translationProviderFactory;
	private final SpongeItemRegistrar itemRegistrar;
	private NegativityPacketManager packetManager;
	private final SpongeScheduler scheduler;

	public SpongeAdapter(SpongePlugin pl) {
		this.plugin = pl;
		this.logger = new Slf4jLoggerAdapter(pl.getLogger());
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().resolve("messages"), "Negativity", "CheatHover");
		this.itemRegistrar = new SpongeItemRegistrar();
		this.packetManager = new NegativityPacketManager(pl);
		this.scheduler = new SpongeScheduler(pl);

		EventManager eventManager = Sponge.getEventManager();
		eventManager.registerListeners(pl, new FightManager());
		eventManager.registerListeners(pl, new BlockListeners());
		eventManager.registerListeners(pl, new EntityListeners());
		eventManager.registerListeners(pl, new InventoryListeners());
		eventManager.registerListeners(pl, new PlayersListeners());

		try {
			Class.forName("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher");
			SpongeForgeSupport.isOnSpongeForge = true;
		} catch (ClassNotFoundException e1) {
			SpongeForgeSupport.isOnSpongeForge = false;
		}

		channel = Sponge.getChannelRegistrar().createRawChannel(pl, GalloraMessagesManager.CHANNEL_ID);
		channel.addListener(new ProxyCompanionListener());
		if (Sponge.getChannelRegistrar().isChannelAvailable("FML|HS")) {
			fmlChannel = Sponge.getChannelRegistrar().getOrCreateRaw(pl, "FML|HS");
			fmlChannel.addListener(new FmlRawDataListener());
		}
	}
	
	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.SPONGE;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder().toFile();
	}

	@Override
	public void debug(String msg) {
		if(UniversalUtils.DEBUG)
			logger.info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) throws IOException {
		Asset asset = plugin.getContainer().getAsset(name).orElse(null);
		return asset == null ? null : asset.getUrl().openStream();
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		trySendProxyPing();
	}

	@Override
	public String getVersion() {
		return Sponge.getPlatform().getMinecraftVersion().getName();
	}
	
	@Override
	public String getPluginVersion() {
		return plugin.getContainer().getVersion().orElse(UniversalUtils.GALLORA_VERSION);
	}

	@Override
	public void reloadConfig() {
		this.config = UniversalUtils.loadConfig(new File(getDataFolder(), "config.yml"), "config.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				ConfigurationNode rootNode = GsonConfigurationLoader.builder()
						.setSource(() -> new BufferedReader(new StringReader(response)))
						.build()
						.load();
				return rootNode.getNode("isMcleaks").getBoolean(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		for (org.spongepowered.api.entity.living.player.Player temp : Sponge.getServer().getOnlinePlayers())
			list.add(temp.getUniqueId());
		return list;
	}

	@Override
	public double[] getTPS() {
		return new double[] {getLastTPS()};
	}

	@Override
	public double getLastTPS() {
		return Sponge.getServer().getTicksPerSecond();
	}

	@Override
	public ItemRegistrar getItemRegistrar() {
		return itemRegistrar;
	}

	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return new SpongeLocation(w, x, y, z);
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover,
			String command) {
		((org.spongepowered.api.entity.living.player.Player) p.getDefault()).sendMessage(Text.builder(message).onHover(TextActions.showText(Text.of(hover))).build());
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		for (org.spongepowered.api.entity.living.player.Player temp : Sponge.getServer().getOnlinePlayers())
			list.add(SpongeEntityManager.getPlayer(temp));
		return list;
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return new SpongeItemBuilder(type);
	}
	
	@Override
	public ItemBuilder createItemBuilder(String type) {
		return new SpongeItemBuilder(itemRegistrar.get(type.split(":")[0]));
	}
	
	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return new SpongeItemBuilder(owner);
	}

	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return new SpongeInventory(inventoryName, size, holder);
	}

	@Override
	public Player getPlayer(String name) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(name).orElse(null));
	}

	@Override
	public Player getPlayer(UUID uuid) {
		return SpongeEntityManager.getPlayer(Sponge.getServer().getPlayer(uuid).orElse(null));
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		Optional<User> optUser = Sponge.getServiceManager().provide(UserStorageService.class).get().get(name);
	    return optUser.isPresent() ? new SpongeOfflinePlayer(optUser.get()) : null;
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		Optional<User> optUser = Sponge.getServiceManager().provide(UserStorageService.class).get().get(uuid);
	    return optUser.isPresent() ? new SpongeOfflinePlayer(optUser.get()) : null;
	}
	
	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		return new SpongeFakePlayer(loc, name);
	}

	@Override
	public boolean hasPlugin(String name) {
		return Sponge.getPluginManager().isLoaded(name);
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new SpongeExternalPlugin(Sponge.getPluginManager().getPlugin(name).orElse(null));
	}
	
	@Override
	public void runSync(Runnable call) {
		Task.builder().execute(call).submit(plugin);
	}
	
	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	
	@Override
	public void disable() {
		
	}
	

	public static SpongePlugin getPlugin() {
		return getAdapter().plugin;
	}

	public void sendProxyPing(org.spongepowered.api.entity.living.player.Player player) {
		ProxyCompanionManager.searchedCompanion = true;
		channel.sendTo(player, (buffer) -> {
			try {
				buffer.writeBytes(GalloraMessagesManager.writeMessage(new ProxyPingMessage(GalloraMessagesManager.PROTOCOL_VERSION)));
			} catch (IOException ex) {
				SpongeAdapter.getAdapter().getLogger().error("Could not write ProxyPingMessage. " + ex.getMessage());
			}
		});
	}

	public void sendPluginMessage(byte[] rawMessage) {
		org.spongepowered.api.entity.living.player.Player player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			channel.sendTo(player, payload -> payload.writeBytes(rawMessage));
		} else {
			logger.error("Could not send plugin message to proxy because there are no player online.");
		}
	}

	public void trySendProxyPing() {
		Iterator<org.spongepowered.api.entity.living.player.Player> onlinePlayers = Sponge.getServer().getOnlinePlayers().iterator();
		if (onlinePlayers.hasNext()) {
			sendProxyPing(onlinePlayers.next());
		}
	}

	private static class FmlRawDataListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf channelBuf, RemoteConnection connection, Type side) {
			if (!(connection instanceof PlayerConnection)) {
				return;
			}

			org.spongepowered.api.entity.living.player.Player player = ((PlayerConnection) connection).getPlayer();
			byte[] rawData = channelBuf.readBytes(channelBuf.available());
			HashMap<String, String> playerMods = GalloraPlayer.getPlayer(player.getUniqueId(), () -> new SpongePlayer(player)).MODS;
			playerMods.clear();
			playerMods.putAll(Utils.getModsNameVersionFromMessage(new String(rawData, StandardCharsets.UTF_8)));
		}
	}

	private static class ProxyCompanionListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {
			byte[] rawData = data.readBytes(data.available());
			GalloraMessage message;
			try {
				message = GalloraMessagesManager.readMessage(rawData);
			} catch (IOException e) {
				SpongeAdapter.getAdapter().getLogger().error("Failed to read proxy companion message." + e.getMessage());
				return;
			}

			if (message instanceof ProxyPingMessage) {
				ProxyPingMessage pingMessage = (ProxyPingMessage) message;
				ProxyCompanionManager.foundCompanion(pingMessage.getProtocol());
			}
		}
	}
}
