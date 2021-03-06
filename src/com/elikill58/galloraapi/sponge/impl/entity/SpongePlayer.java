package com.elikill58.galloraapi.sponge.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

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
import com.elikill58.galloraapi.sponge.SpongeAdapter;
import com.elikill58.galloraapi.sponge.impl.SpongePotionEffectType;
import com.elikill58.galloraapi.sponge.impl.inventory.SpongeInventory;
import com.elikill58.galloraapi.sponge.impl.inventory.SpongePlayerInventory;
import com.elikill58.galloraapi.sponge.impl.item.SpongeItemStack;
import com.elikill58.galloraapi.sponge.impl.location.SpongeLocation;
import com.elikill58.galloraapi.sponge.impl.location.SpongeWorld;
import com.elikill58.galloraapi.sponge.utils.LocationUtils;
import com.elikill58.galloraapi.sponge.utils.Utils;
import com.elikill58.galloraapi.universal.Gallora;
import com.elikill58.galloraapi.universal.Version;
import com.elikill58.galloraapi.universal.support.ViaVersionSupport;
import com.flowpowered.math.vector.Vector3d;

public class SpongePlayer extends SpongeEntity<org.spongepowered.api.entity.living.player.Player> implements Player {

	private Version playerVersion;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player p) {
		super(p);
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return Gallora.viaVersionSupport ? ViaVersionSupport.getPlayerVersion(this) : Version.getVersion();
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(Text.of(msg));
	}

	@Override
	public boolean isOnGround() {
		return entity.isOnGround();
	}

	@Override
	public boolean isOp() {
		return entity.hasPermission("*");
	}

	@Override
	public boolean hasElytra() {
		return entity.get(Keys.IS_ELYTRA_FLYING).orElse(false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this.entity, (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) entity.getLocation().getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return (float) (double) entity.get(Keys.WALKING_SPEED).get();
	}

	@Override
	public double getHealth() {
		return entity.getOrCreate(HealthData.class).get().health().get();
	}

	@Override
	public float getFallDistance() {
		return entity.getOrCreate(FallDistanceData.class).get().fallDistance().get();
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.gameMode().get().getName());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		switch (gameMode) {
		case ADVENTURE:
			entity.gameMode().set(GameModes.ADVENTURE);
			break;
		case CREATIVE:
			entity.gameMode().set(GameModes.CREATIVE);
			break;
		case CUSTOM:
			entity.gameMode().set(GameModes.NOT_SET);
			break;
		case SPECTATOR:
			entity.gameMode().set(GameModes.SPECTATOR);
			break;
		case SURVIVAL:
			entity.gameMode().set(GameModes.SURVIVAL);
			break;
		}
	}

	@Override
	public void damage(double amount) {
		entity.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(entity.getLocation());
	}

	@Override
	public int getPing() {
		return entity.getConnection().getLatency();
	}

	@Override
	public World getWorld() {
		return new SpongeWorld(entity.getWorld());
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
		return playerVersion == Version.HIGHER ? (playerVersion = loadVersion()) : playerVersion;
	}

	@Override
	public void kick(String reason) {
		entity.kick(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return entity.get(Keys.EXPERIENCE_LEVEL).get();
	}
	
	@Override
	public double getFoodLevel() {
		return entity.get(Keys.FOOD_LEVEL).get();
	}

	@Override
	public boolean getAllowFlight() {
		return entity.get(Keys.CAN_FLY).orElse(false);
	}

	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(entity.getVehicle().orElse(null));
	}
	
	@Override
	public ItemStack getItemInHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = entity.getItemInHand(HandTypes.MAIN_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isFlying() {
		return entity.getOrCreate(FlyingData.class).get().flying().get();
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		(channelId.equalsIgnoreCase("fml") ? SpongeAdapter.fmlChannel : SpongeAdapter.channel).sendTo(entity, (chan) -> chan.writeByteArray(writeMessage));
	}

	@Override
	public boolean isSleeping() {
		return entity.getOrCreate(SleepingData.class).get().sleeping().get();
	}

	@Override
	public boolean isSneaking() {
		return entity.getOrCreate(SneakingData.class).get().sneaking().get();
	}

	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(entity);
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		return entity.getOrCreate(PotionEffectData.class).get().asList().stream().filter((pe) -> pe.getType().getName().equalsIgnoreCase(type.name()))
				.findAny().isPresent();
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<PotionEffect> list = new ArrayList<PotionEffect>();
		entity.getOrCreate(PotionEffectData.class).get().asList().forEach((pe) -> list.add(new PotionEffect(PotionEffectType.fromName(pe.getType().getName()))));
		return list;
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		org.spongepowered.api.effect.potion.PotionEffect effect = null;
		for(org.spongepowered.api.effect.potion.PotionEffect pe : entity.getOrCreate(PotionEffectData.class).get().asList())
			if(PotionEffectType.fromName(pe.getType().getName()) == type)
				effect = pe;
		return effect == null ? Optional.empty() : Optional.of(new PotionEffect(type, effect.getDuration(), effect.getAmplifier()));
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		Utils.removePotionEffect(entity.getOrCreate(PotionEffectData.class).get(), SpongePotionEffectType.getEffect(type));
	}

	@Override
	public String getIP() {
		return entity.getConnection().getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return entity.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.getOrCreate(SneakingData.class).get().sneaking().set(b);
	}

	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		PotionEffectData potionEffects = entity.getOrCreate(PotionEffectData.class).orElse(null);
		potionEffects.addElement(org.spongepowered.api.effect.potion.PotionEffect.builder().potionType(SpongePotionEffectType.getEffect(type))
					.amplifier(amplifier).duration(duration).build());
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return entity.getOrCreate(SprintData.class).get().sprinting().get();
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void teleport(Location loc) {
		entity.setLocation((org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) loc.getDefault());
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.getVehicle().isPresent();
	}

	@Override
	public float getFlySpeed() {
		return (float) (double) entity.get(Keys.FLYING_SPEED).get();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.getOrCreate(SprintData.class).get().sprinting().set(b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		entity.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		if (loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER"))
			return true;
		return false;
	}

	@Override
	public ItemStack getItemInOffHand() {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = entity.getItemInHand(HandTypes.OFF_HAND);
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		Vector3d vel = entity.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(entity, entity.getInventory());
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory().isPresent() && entity.getOpenInventory().get().getArchetype().equals(InventoryArchetypes.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return entity.getOpenInventory().isPresent() ? null : new SpongeInventory(entity.getOpenInventory().get());
	}

	@Override
	public void openInventory(Inventory inv) {
		entity.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}

	@Override
	public void closeInventory() {
		Task.builder().execute(() -> entity.closeInventory()).submit(SpongeAdapter.getPlugin());
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.offer(Keys.CAN_FLY, b);
	}

	@Override
	public void showPlayer(Player p) {
		// TODO implement showPlayer
	}
	
	@Override
	public void hidePlayer(Player p) {
		// TODO implement hidePlayer
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public Location getEyeLocation() {
		Vector3d vec = entity.getProperty(EyeLocationProperty.class).map(EyeLocationProperty::getValue).orElse(entity.getRotation());
		return new SpongeLocation(new SpongeWorld(entity.getWorld()), vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public Vector getRotation() {
		Vector3d vec = entity.getRotation();
		return new Vector(vec.getX(), vec.getY(), vec.getZ());
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return entity.getConnection().getVirtualHost();
	}
}
