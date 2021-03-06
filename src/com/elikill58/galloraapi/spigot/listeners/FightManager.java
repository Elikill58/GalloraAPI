package com.elikill58.galloraapi.spigot.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.Potion;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.spigot.utils.Utils;

@SuppressWarnings("deprecation")
public class FightManager implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		// Ignoring Citizen NPC
		if(e.getDamager().hasMetadata("NPC") || e.getEntity().hasMetadata("NPC"))
			return;
		if(e.getDamager() instanceof Player)
			GalloraPlayer.getCached(e.getDamager().getUniqueId()).fight();
		if(e.getEntity() instanceof Player)
			GalloraPlayer.getCached(e.getEntity().getUniqueId()).fight();
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC"))
			GalloraPlayer.getCached(e.getEntity().getUniqueId()).fight();
	}

	@EventHandler
	public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerFillBucket(PlayerBucketFillEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 8);
	}

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent e) {
		manageFightBetweenTwoPlayers(e.getPlayer(), 10);
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent e) {
		String name = e.getPlayer().getItemInHand().getType().name();
		if(name.contains("SWORD") || name.contains("AXE") || name.contains("APPLE") || name.contains("BOW") || name.contains("POTION"))
			manageFightBetweenTwoPlayers(e.getPlayer(), 15);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if(!e.getEntity().getType().equals(EntityType.SPLASH_POTION) || !(e.getEntity() instanceof Potion))
			return;
		Location loc = e.getEntity().getLocation();
		switch(((Potion) e.getEntity()).getType()) {
		case NIGHT_VISION:
		case INVISIBILITY:
		case WATER:
		case WATER_BREATHING:
			return;
		case FIRE_RESISTANCE:
		case INSTANT_HEAL:
		case REGEN:
		case SPEED:
		case STRENGTH:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 18 && loc.distance(p.getLocation()) > 4)
						GalloraPlayer.getCached(p.getUniqueId()).fight();
			break;
		case INSTANT_DAMAGE:
		case POISON:
		case WEAKNESS:
		case SLOWNESS:
			for(Player p : Utils.getOnlinePlayers())
				if(loc.getWorld().equals(p.getLocation().getWorld()))
					if(loc.distance(p.getLocation()) < 9)
						GalloraPlayer.getCached(p.getUniqueId()).fight();
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		GalloraPlayer.getCached(e.getEntity().getUniqueId()).unfight();
	}
	
	@EventHandler
	public void blowUp(EntityExplodeEvent e) {
		if(!e.getEntityType().equals(EntityType.PRIMED_TNT))
			return;
		Location loc = e.getLocation();
		Utils.getOnlinePlayers().stream().filter((p) -> loc.getWorld().equals(p.getWorld()) && loc.distance(p.getLocation()) < 5).forEach((p) -> GalloraPlayer.getCached(p.getUniqueId()).fight());
	}
	
	private void manageFightBetweenTwoPlayers(Player p, int maxDistance) {
		if(!p.getWorld().getPVP())
			return;
		GalloraPlayer np = GalloraPlayer.getCached(p.getUniqueId());
		for(Player pl : Utils.getOnlinePlayers()) {
			GalloraPlayer npOther = GalloraPlayer.getCached(p.getUniqueId());
			if(npOther.isInFight && np.isInFight)
				continue;
			if(pl.getLocation().getWorld().equals(p.getLocation().getWorld())) {
				if(pl.getLocation().distance(p.getLocation()) < maxDistance) {
					np.fight();
					npOther.fight();
				}
			}
		}
	}
}
