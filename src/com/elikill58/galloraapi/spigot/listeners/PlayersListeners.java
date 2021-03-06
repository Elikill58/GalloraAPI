package com.elikill58.galloraapi.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.events.EventManager;
import com.elikill58.galloraapi.api.events.player.LoginEvent;
import com.elikill58.galloraapi.api.events.player.LoginEvent.Result;
import com.elikill58.galloraapi.api.events.player.PlayerChatEvent;
import com.elikill58.galloraapi.api.events.player.PlayerConnectEvent;
import com.elikill58.galloraapi.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.galloraapi.api.events.player.PlayerInteractEvent;
import com.elikill58.galloraapi.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.galloraapi.api.events.player.PlayerLeaveEvent;
import com.elikill58.galloraapi.api.events.player.PlayerMoveEvent;
import com.elikill58.galloraapi.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.galloraapi.api.events.player.PlayerTeleportEvent;
import com.elikill58.galloraapi.spigot.SpigotAdapter;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotPlayer;
import com.elikill58.galloraapi.spigot.impl.item.SpigotItemStack;
import com.elikill58.galloraapi.spigot.impl.location.SpigotLocation;
import com.elikill58.galloraapi.universal.ProxyCompanionManager;

public class PlayersListeners implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, e.getQuitMessage());
		EventManager.callEvent(event);
		e.setQuitMessage(event.getQuitMessage());
		Bukkit.getScheduler().runTaskLater(SpigotAdapter.getPlugin(), () -> GalloraPlayer.removeFromCache(p.getUniqueId()), 2);
	}
	
	@EventHandler
	public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
		Player p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		if(np.isFreeze && !p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR))
			e.setCancelled(true);
		PlayerMoveEvent event = new PlayerMoveEvent(SpigotEntityManager.getPlayer(p), new SpigotLocation(e.getFrom()), new SpigotLocation(e.getTo()));
		Bukkit.getScheduler().runTaskAsynchronously(SpigotAdapter.getPlugin(), () -> EventManager.callEvent(event));
		if(event.hasToSet()) {
			e.setTo((Location) event.getTo().getDefault());
			e.setFrom((Location) event.getFrom().getDefault());
		}
		if(e.isCancelled())
			return;
		if(p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("SLIME")) {
			np.isUsingSlimeBlock = true;
		} else if(np.isUsingSlimeBlock && (((org.bukkit.entity.Entity) p).isOnGround() && !p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().name().contains("AIR")))
			np.isUsingSlimeBlock = false;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(SpigotAdapter.getPlugin(), () -> {
			PlayerChatEvent event = new PlayerChatEvent(SpigotEntityManager.getPlayer(e.getPlayer()), e.getMessage(), e.getFormat());
			EventManager.callEvent(event);
			if(event.isCancelled())
				e.setCancelled(event.isCancelled());
		});
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player)
			EventManager.callEvent(new PlayerDamageByEntityEvent(SpigotEntityManager.getPlayer((Player) e.getEntity()), SpigotEntityManager.getEntity(e.getDamager())));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		EventManager.callEvent(new com.elikill58.galloraapi.api.events.player.PlayerDeathEvent(SpigotEntityManager.getPlayer(e.getEntity())));
	}
	
	@EventHandler
	public void onInteract(org.bukkit.event.player.PlayerInteractEvent e) {
		PlayerInteractEvent event = new PlayerInteractEvent(SpigotEntityManager.getPlayer(e.getPlayer()), Action.valueOf(e.getAction().name()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent e) {
		EventManager.callEvent(new com.elikill58.galloraapi.api.events.player.PlayerItemConsumeEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotItemStack(e.getItem())));
	}
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player) {
			PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(SpigotEntityManager.getPlayer((Player) e.getEntity()));
			EventManager.callEvent(event);
			if(event.isCancelled())
				e.setCancelled(event.isCancelled());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		LoginEvent event = new LoginEvent(e.getUniqueId(), e.getName(), Result.valueOf(e.getLoginResult().name()), e.getAddress(), e.getKickMessage());
		EventManager.callEvent(event);
		e.setKickMessage(event.getKickMessage());
		e.setLoginResult(AsyncPlayerPreLoginEvent.Result.valueOf(event.getLoginResult().name()));
	}
	
	@EventHandler
	public void onTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
		EventManager.callEvent(new PlayerTeleportEvent(SpigotEntityManager.getPlayer(e.getPlayer()), new SpigotLocation(e.getFrom()), new SpigotLocation(e.getTo())));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpigotPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, e.getJoinMessage());
		EventManager.callEvent(event);
		e.setJoinMessage(event.getJoinMessage());
		
		if(!ProxyCompanionManager.searchedCompanion) {
			ProxyCompanionManager.searchedCompanion = true;
			Bukkit.getScheduler().runTaskLater(SpigotAdapter.getPlugin(), () -> SpigotAdapter.getAdapter().sendProxyPing(p), 20);
		}
	}
}
