package com.elikill58.galloraapi.sponge.impl.entity;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.golem.IronGolem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.commands.CommandSender;
import com.elikill58.galloraapi.api.entity.Entity;

public class SpongeEntityManager {

	public static com.elikill58.galloraapi.api.entity.Player getPlayer(Player p){
		return GalloraPlayer.getPlayer(p.getUniqueId(), () -> new SpongePlayer(p)).getPlayer();
	}
	
	public static Entity getEntity(org.spongepowered.api.entity.Entity e) {
		if(e == null)
			return null;
		if(e.getType().equals(EntityTypes.PLAYER))
			return getPlayer((Player) e);
		else if(e.getType().equals(EntityTypes.IRON_GOLEM))
			return new SpongeIronGolem((IronGolem) e);
		else if(e.getType().getId().contains("ARROW"))
			return new SpongeArrow((Arrow) e);
		else
			return new SpongeEntity(e);
	}

	public static CommandSender getExecutor(CommandSource src) {
		if(src == null)
			return null;
		if(src instanceof Player)
			return new SpongePlayer((Player) src);
		return new SpongeCommandSender(src);
	}

	public static Entity getProjectile(ProjectileSource shooter) {
		if(shooter == null)
			return null;
		if(shooter instanceof Player)
			return getPlayer((Player) shooter);
		else if(shooter instanceof org.spongepowered.api.entity.Entity)
			return new SpongeEntity((org.spongepowered.api.entity.Entity) shooter);
		return null;
	}
}
