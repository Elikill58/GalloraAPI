package com.elikill58.galloraapi.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.universal.PacketType;
import com.elikill58.galloraapi.universal.account.NegativityAccount;

public class GalloraPlayer {

	private static final Map<UUID, GalloraPlayer> PLAYERS = new HashMap<>();
	public static final ArrayList<UUID> INJECTED = new ArrayList<>();

	private final Player p;
	public boolean isInFight = false, isFreeze = false, isUsingSlimeBlock = false;
	
	public HashMap<String, String> MODS = new HashMap<>();
	public HashMap<PacketType, Integer> PACKETS = new HashMap<>();
	
	public GalloraPlayer(Player p) {
		this.p = p;
	}
	
	public UUID getUUID() {
		return p.getUniqueId();
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public boolean isBedrockPlayer() {
		return false;
	}
	
	public NegativityAccount getAccount() {
		return NegativityAccount.get(getUUID());
	}

	public void fight() {
		isInFight = true;
	}
	
	public void unfight() {
		isInFight = false;
	}
	
	public void destroy() {
		
	}
	
	

	/**
	 * Get the Gallora Player or create a new one
	 * 
	 * @param p the player which we are looking for it's NegativityPlayer
	 * @return the Gallora player
	 */
	public static GalloraPlayer getPlayer(Player p) {
		return PLAYERS.computeIfAbsent(p.getUniqueId(), id -> new GalloraPlayer(p));
	}

	/**
	 * Get the Gallora Player or create a new one
	 * 
	 * @param uuid the player UUID
	 * @param call a creator of a new player
	 * @return the Gallora player
	 */
	public static GalloraPlayer getPlayer(UUID uuid, Callable<Player> call) {
		return PLAYERS.computeIfAbsent(uuid, id -> {
			try {
				return new GalloraPlayer(call.call());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	/**
	 * Get the Gallora player in cache of the given UUID
	 * 
	 * @param playerId the player UUID
	 * @return the Gallora player
	 */
	public static GalloraPlayer getCached(UUID playerId) {
		return PLAYERS.get(playerId);
	}
	
	/**
	 * Get all uuid and their Gallora players
	 * 
	 * @return Gallora players
	 */
	public static Map<UUID, GalloraPlayer> getAllPlayers(){
		return PLAYERS;
	}

	/**
	 * Remove the player from cache
	 * 
	 * @param playerId the player UUID
	 */
	public static void removeFromCache(UUID playerId) {
		GalloraPlayer cached = PLAYERS.remove(playerId);
		if (cached != null) {
			cached.destroy();
		}
	}
}
