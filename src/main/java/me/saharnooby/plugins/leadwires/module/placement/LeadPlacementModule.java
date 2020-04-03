package me.saharnooby.plugins.leadwires.module.placement;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.module.Module;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author saharNooby
 * @since 10:26 30.03.2020
 */
@Getter
public final class LeadPlacementModule extends Module {

	private final ModuleConfig config;
	
	private PlacedLeadStorage storage;

	public LeadPlacementModule(@NonNull ConfigurationSection config) {
		this.config = new ModuleConfig(config);
	}

	@Override
	public void onEnable() {
		this.storage = new PlacedLeadStorage();

		try {
			this.storage.load();
		} catch (IOException e) {
			LeadWires.getInstance().getLogger().log(Level.SEVERE, "Failed to load placed leads storage", e);
		}

		if (this.config.isEnabled()) {
			registerListener(new LeadPlaceListener(this));
		}

		registerListener(new LeadBreakListener(this));

		repeatTask(() -> {
			for (UUID uuid : this.storage.getAllLeads()) {
				LeadWires.getApi().getWire(uuid).ifPresent(this::checkWire);
			}
		}, 0, 5 * 20);
	}

	@Override
	public void onDisable() {
		this.storage.close();
	}

	public void checkBlocksLater(@NonNull Collection<Block> blocks) {
		runTask(() -> blocks.forEach(this::checkBlock));
	}

	private void checkBlock(@NonNull Block block) {
		for (UUID uuid : this.storage.getInBlock(block).toArray(new UUID[0])) {
			LeadWires.getApi().getWire(uuid).ifPresent(this::checkWire);
		}
	}

	private void checkWire(@NonNull Wire wire) {
		World world = Bukkit.getWorld(wire.getWorld());

		if (world == null) {
			return;
		}

		if (isPointValid(world, wire.getA()) && isPointValid(world, wire.getB())) {
			return;
		}

		Vector vec = wire.getA().add(wire.getB());
		Location dropLoc = new Location(world, vec.getX() / 2, vec.getY() / 2, vec.getZ() / 2);

		if (!world.isChunkLoaded(dropLoc.getBlockX() >> 4, dropLoc.getBlockZ() >> 4)) {
			return;
		}

		this.storage.removeLead(wire);

		LeadWires.getApi().removeWire(wire.getUuid());

		world.dropItem(dropLoc, new ItemStack(Material.valueOf(NMSUtil.getMinorVersion() < 13 ? "LEASH" : "LEAD")));
	}

	private boolean isPointValid(@NonNull World world, @NonNull Vector vec) {
		if (!world.isChunkLoaded(vec.getChunkX(), vec.getChunkZ())) {
			return true;
		}

		return this.config.getAllowedBlocks().contains(world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getType().name());
	}

	public void sendMessage(@NonNull CommandSender sender, @NonNull String key, @NonNull Object... args) {
		sender.sendMessage(String.format(this.config.getMessages().getOrDefault(key, key), args));
	}

}
