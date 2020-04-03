package me.saharnooby.plugins.leadwires.module.placement;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.block.Block;

import java.util.*;

/**
 * @author saharNooby
 * @since 12:59 30.03.2020
 */
final class PlacedLeadCache {

	private final Map<String, Map<Vector, Set<UUID>>> byWorldAndBlock = new HashMap<>();

	public Set<UUID> getInBlock(@NonNull Block block) {
		Map<Vector, Set<UUID>> byBlock = this.byWorldAndBlock.get(block.getWorld().getName());

		if (byBlock == null) {
			return Collections.emptySet();
		}

		return byBlock.getOrDefault(new Vector(block.getX(), block.getY(), block.getZ()), Collections.emptySet());
	}

	public void addWire(@NonNull Wire wire) {
		Map<Vector, Set<UUID>> byBlock = this.byWorldAndBlock.computeIfAbsent(wire.getWorld(), k -> new HashMap<>());
		byBlock.computeIfAbsent(toBlock(wire.getA()), k -> new HashSet<>()).add(wire.getUuid());
		byBlock.computeIfAbsent(toBlock(wire.getB()), k -> new HashSet<>()).add(wire.getUuid());
	}

	public void removeWire(@NonNull Wire wire) {
		Map<Vector, Set<UUID>> byBlock = this.byWorldAndBlock.get(wire.getWorld());

		if (byBlock == null) {
			return;
		}

		Set<UUID> set = byBlock.get(toBlock(wire.getA()));

		if (set != null && set.remove(wire.getUuid()) && set.isEmpty()) {
			byBlock.remove(toBlock(wire.getA()));
		}

		set = byBlock.get(toBlock(wire.getB()));

		if (set != null && set.remove(wire.getUuid()) && set.isEmpty()) {
			byBlock.remove(toBlock(wire.getB()));
		}

		if (byBlock.isEmpty()) {
			this.byWorldAndBlock.remove(wire.getWorld());
		}
	}

	public void clear() {
		this.byWorldAndBlock.clear();
	}

	private static Vector toBlock(@NonNull Vector vec) {
		return new Vector(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}

}
