package com.elikill58.galloraapi.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class ElytraListeners implements Listener {
	
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e) {
		//if(!e.isGliding() && e.getEntity() instanceof Player)
			//GalloraPlayer.getCached(e.getEntity().getUniqueId()).TIME_INVINCIBILITY = System.currentTimeMillis() + 800;
	}
}
