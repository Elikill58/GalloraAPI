package com.elikill58.galloraapi.spigot.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.event.inventory.InventoryType;

import com.elikill58.galloraapi.api.GameMode;
import com.elikill58.galloraapi.api.entity.Entity;
import com.elikill58.galloraapi.api.entity.EntityType;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.inventory.Inventory;
import com.elikill58.galloraapi.api.inventory.PlayerInventory;
import com.elikill58.galloraapi.api.item.ItemStack;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.api.potion.PotionEffect;
import com.elikill58.galloraapi.api.potion.PotionEffectType;
import com.elikill58.galloraapi.spigot.SpigotAdapter;
import com.elikill58.galloraapi.spigot.impl.inventory.SpigotInventory;
import com.elikill58.galloraapi.spigot.impl.inventory.SpigotPlayerInventory;
import com.elikill58.galloraapi.spigot.impl.item.SpigotItemStack;
import com.elikill58.galloraapi.spigot.impl.location.SpigotLocation;
import com.elikill58.galloraapi.spigot.impl.location.SpigotWorld;
import com.elikill58.galloraapi.spigot.utils.PacketUtils;
import com.elikill58.galloraapi.universal.Gallora;
import com.elikill58.galloraapi.universal.Version;
import com.elikill58.galloraapi.universal.support.ProtocolSupportSupport;
import com.elikill58.galloraapi.universal.support.ViaVersionSupport;

public class SpigotPlayer extends SpigotEntity<org.bukkit.entity.Player> implements Player {

	private Version playerVersion;
	
	public SpigotPlayer(org.bukkit.entity.Player p) {
		super(p);
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return (Gallora.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(this) : (Gallora.protocolSupportSupport ? ProtocolSupportSupport.getPlayerVersion(this) : Version.getVersion()));
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}
	
	@Override
	public boolean isOnGround() {
		return ((org.bukkit.entity.Entity) entity).isOnGround();
	}

	@Override
	public boolean isOp() {
		return entity.isOp();
	}

	@Override
	public boolean hasElytra() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && entity.isGliding();
	}
	
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return SpigotAdapter.isCraftBukkit ? true : this.entity.hasLineOfSight((org.bukkit.entity.Entity) entity.getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return entity.getWalkSpeed();
	}

	@Override
	public double getHealth() {
		return ((Damageable) entity).getHealth();
	}

	@Override
	public float getFallDistance() {
		return entity.getFallDistance();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.getGameMode().name());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		entity.setGameMode(org.bukkit.GameMode.valueOf(gameMode.name()));
	}

	@Override
	public void damage(double amount) {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.damage(amount));
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(entity.getLocation());
	}

	@Override
	public int getPing() {
		try {
			Object entityPlayer = PacketUtils.getEntityPlayer(entity);
			return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public World getWorld() {
		return new SpigotWorld(entity.getWorld());
	}

	@Override
	public String getName() {
		return entity.getName();
	}

	@Override
	public boolean hasPermission(String perm) {
		return entity.hasPermission(perm);
	}

	@Override
	public Version getPlayerVersion() {
		return playerVersion.equals(Version.HIGHER) ? (playerVersion = loadVersion()) : playerVersion;
	}

	@Override
	public void kick(String reason) {
		entity.kickPlayer(reason);
	}

	@Override
	public int getLevel() {
		return entity.getLevel();
	}
	
	@Override
	public double getFoodLevel() {
		return entity.getFoodLevel();
	}

	@Override
	public boolean getAllowFlight() {
		return entity.getAllowFlight();
	}

	@Override
	public Entity getVehicle() {
		return entity.isInsideVehicle() ? SpigotEntityManager.getEntity(entity.getVehicle()) : null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemInHand() {
		return new SpigotItemStack(entity.getItemInHand());
	}

	@Override
	public boolean isFlying() {
		return entity.isFlying();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		entity.sendPluginMessage(SpigotAdapter.getPlugin(), channelId, writeMessage);
	}

	@Override
	public boolean isSleeping() {
		return entity.isSleeping();
	}

	@Override
	public boolean isSneaking() {
		return entity.isSneaking();
	}

	@Override
	public double getEyeHeight() {
		return entity.getEyeHeight();
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.getActivePotionEffects().stream().filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name()))
				.findAny().isPresent();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<>();
		entity.getActivePotionEffects()
				.forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()), pe.getDuration(), pe.getAmplifier())));
		return list;
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		for(PotionEffect pe : getActivePotionEffect())
			if(pe.getType().equals(type))
				return Optional.of(pe);
		return Optional.empty();
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.removePotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()));
	}

	@Override
	public String getIP() {
		return entity.getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return entity.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.setSneaking(b);
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.getByName(type.name()),
				duration, amplifier));
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return entity.isSprinting();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@Override
	public void teleport(Location loc) {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.teleport((org.bukkit.Location) loc.getDefault()));
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.isInsideVehicle();
	}

	@Override
	public float getFlySpeed() {
		return entity.getFlySpeed();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.setSprinting(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.getNearbyEntities(x, y, z).forEach((entity) -> list.add(SpigotEntityManager.getEntity(entity))));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (Version.getVersion().isNewerOrEquals(Version.V1_13))
			return entity.isSwimming() || entity.hasPotionEffect(org.bukkit.potion.PotionEffectType.DOLPHINS_GRACE);
		else {
			if (!entity.isSprinting())
				return false;
			Location loc = getLocation().clone();
			if (loc.getBlock().getType().getId().contains("WATER"))
				return true;
			if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
				return true;
			return false;
		}
	}

	@Override
	public ItemStack getItemInOffHand() {
		return Version.getVersion().isNewerOrEquals(Version.V1_9) && entity.getInventory().getItemInOffHand() != null ? new SpigotItemStack(entity.getInventory().getItemInOffHand()) : null;
	}

	@Override
	public boolean isDead() {
		return entity.getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		org.bukkit.util.Vector vel = entity.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpigotPlayerInventory(entity.getInventory());
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory() != null && entity.getOpenInventory().getTopInventory() != null && entity.getOpenInventory().getTopInventory().getType().equals(InventoryType.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return entity.getOpenInventory() == null || entity.getOpenInventory().getTopInventory() == null ? null
				: new SpigotInventory(entity.getOpenInventory().getTopInventory());
	}

	@Override
	public void openInventory(Inventory inv) {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.openInventory((org.bukkit.inventory.Inventory) inv.getDefault()));
	}

	@Override
	public void closeInventory() {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.closeInventory());
	}

	@Override
	public void updateInventory() {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> entity.updateInventory());
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.setAllowFlight(b);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void showPlayer(Player p) {
		entity.showPlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void hidePlayer(Player p) {
		entity.hidePlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new org.bukkit.util.Vector(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return entity.getAddress();
	}
}
