package com.elikill58.galloraapi.api.entity;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.elikill58.galloraapi.api.GameMode;
import com.elikill58.galloraapi.api.inventory.Inventory;
import com.elikill58.galloraapi.api.inventory.PlayerInventory;
import com.elikill58.galloraapi.api.item.ItemStack;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.Vector;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.api.potion.PotionEffect;
import com.elikill58.galloraapi.api.potion.PotionEffectType;
import com.elikill58.galloraapi.universal.Version;

public interface Player extends OfflinePlayer {

	/**
	 * Get the player IP
	 * 
	 * @return player IP
	 */
	@Nullable
	public String getIP();
	
	/**
	 * Know if the player is dead
	 * 
	 * @return true if the player is dead
	 */
	public boolean isDead();
	
	/**
	 * Know if the player is sleeping
	 * 
	 * @return true is the player is sleeping
	 */
	public boolean isSleeping();
	/**
	 * Know if the player is swimming
	 * (compatible with 1.8 and lower)
	 * 
	 * @return true if it's swimming
	 */
	public boolean isSwimming();
	/**
	 * Check if the player is using elytra (flying with it)
	 * (compatible with 1.8 and lower)
	 * 
	 * @return true if is elytra flying
	 */
	public boolean hasElytra();
	/**
	 * Check if the player has the specified permission
	 * 
	 * @param perm the needed permission
	 * @return true if the player has permission
	 */
	public boolean hasPermission(String perm);
	/**
	 * Check if player can see the specified entity
	 * 
	 * @param entity the entity to see
	 * @return true if the player can see it
	 */
	public boolean hasLineOfSight(Entity entity);
	
	/**
	 * Check if the player is flying
	 * 
	 * @return true is the player fly
	 */
	public boolean isFlying();
	/**
	 * Check if the player is authorized to fly
	 * 
	 * @return true if the player can fly
	 */
	public boolean getAllowFlight();
	/**
	 * Edit the authorization to fly
	 * 
	 * @param b true if the player is allowed to fly
	 */
	public void setAllowFlight(boolean b);

	/**
	 * Get current player latency
	 * 
	 * @return the player ping
	 */
	public int getPing();
	/**
	 * Get player XP level
	 * 
	 * @return the player level
	 */
	public int getLevel();
	
	/**
	 * Get player fly speed
	 * 
	 * @return the speed when player fly
	 */
	public float getFlySpeed();
	/**
	 * Get player walk speed
	 * 
	 * @return the speed when player walk
	 */
	public float getWalkSpeed();
	/**
	 * Get the player fall distance when player fall
	 * 
	 * @return the player fall distance
	 */
	public float getFallDistance();
	
	/**
	 * Get the player health
	 * 
	 * @return the health
	 */
	public double getHealth();
	
	/**
	 * Get the current player food level
	 * 
	 * @return the food level
	 */
	public double getFoodLevel();
	
	/**
	 * Get player gamemode
	 * 
	 * @return the Gamemode
	 */
	public GameMode getGameMode();
	
	/**
	 * Set the player gamemode
	 * Warn: support only default gamemode. Not modded server.
	 * 
	 * @param gameMode the new player gamemode
	 */
	public void setGameMode(GameMode gameMode);

	/**
	 * Damage player according to damage amount
	 * 
	 * @param amount the quantity of damage
	 */
	public void damage(double amount);
	/**
	 * Kick player with the specified reason
	 * 
	 * @param reason the reason of kick
	 */
	public void kick(String reason);
	/**
	 * Teleport player to specified location
	 * 
	 * @param loc location destination
	 */
	public void teleport(Location loc);
	/**
	 * Teleport player to specified entity
	 * 
	 * @param et entity destination
	 */
	public void teleport(Entity et);

	public boolean isSneaking();
	public void setSneaking(boolean b);
	
	public boolean isSprinting();
	public void setSprinting(boolean b);

	/**
	 * Get player world
	 * 
	 * @return the world where the player is
	 */
	public World getWorld();
	
	/**
	 * Get player version
	 * (Compatible with ViaVersion and ProtocolSupport for multiple client version)
	 * 
	 * @return the version which player use on server
	 */
	public Version getPlayerVersion();

	/**
	 * Get the entity which is used as vehicle.
	 * It can be a wagon or a zombie.
	 * 
	 * @return the vehicle entity
	 */
	public Entity getVehicle();
	
	/**
	 * Check if player is in a vehicle.
	 * 
	 * @return true if it is in vehicle
	 */
	public boolean isInsideVehicle();

	/**
	 * Get the item in main hand
	 * Return null if there is not any item in hand
	 * 
	 * @return the item in hand
	 */
	public ItemStack getItemInHand();
	
	/**
	 * Get the item in second hand
	 * Compatible with 1.8 and lower.
	 * Return null if there is any item in second hand or if the server is on 1.8 or lower
	 * 
	 * @return the item in off hand
	 */
	public ItemStack getItemInOffHand();
	
	public boolean hasPotionEffect(PotionEffectType type);
	public List<PotionEffect> getActivePotionEffect();
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type);
	public default void addPotionEffect(PotionEffect pe) {
		addPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier());
	}
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier);
	public void removePotionEffect(PotionEffectType type);
	
	/**
	 * Send plugin message :
	 * bungee > spigot
	 * OR
	 * spigot > bungee
	 * On the specified channel
	 * 
	 * @param channelId the channel ID
	 * @param writeMessage the message to sent
	 */
	public void sendPluginMessage(String channelId, byte[] writeMessage);
	
	public List<Entity> getNearbyEntities(double x, double y, double z);

	public PlayerInventory getInventory();
	public Inventory getOpenInventory();
	public boolean hasOpenInventory();
	public void openInventory(Inventory inv);
	public void closeInventory();
	public void updateInventory();

	/**
	 * Show the specified player to itself
	 * 
	 * @param other the player to showed
	 */
	public void showPlayer(Player other);
	/**
	 * Hide the specified player to itself
	 * 
	 * @param other the player to hide
	 */
	public void hidePlayer(Player other);
	
	/**
	 * Get current player velocity
	 * 
	 * @return the player velocity
	 */
	public Vector getVelocity();
	/**
	 * Edit the player velocity
	 * 
	 * @param vel the new velocity
	 */
	public void setVelocity(Vector vel);

	/**
	 * Get the player address
	 * 
	 * @return the player inet address
	 */
	public InetSocketAddress getAddress();
	
	/**
	 * Check if it's a new player
	 * 
	 * @return true if the player has already played
	 */
	@Override
	public default boolean hasPlayedBefore() {
		return true;
	}
	
	static boolean isSamePlayer(Player player1, Player player2) {
		return player1.getUniqueId().equals(player2.getUniqueId());
	}
}
