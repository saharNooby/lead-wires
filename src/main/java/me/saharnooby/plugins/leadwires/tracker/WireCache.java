package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.chunk.ChunkCoord;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.World;

import java.util.*;

/**
 * @author saharNooby
 * @since 17:10 26.03.2020
 */
public final class WireCache {

	private final Map<String, Map<ChunkCoord, Set<Wire>>> byWorldAndChunk = new HashMap<>();

	public Set<Wire> getWiresInChunk(@NonNull World world, @NonNull ChunkCoord coord) {
		Map<ChunkCoord, Set<Wire>> byChunk = this.byWorldAndChunk.get(world.getName());

		return byChunk == null ? Collections.emptySet() : byChunk.getOrDefault(coord, Collections.emptySet());
	}

	public void add(@NonNull Wire wire) {
		Map<ChunkCoord, Set<Wire>> byChunk = this.byWorldAndChunk.computeIfAbsent(wire.getWorld(), k -> new HashMap<>());
		byChunk.computeIfAbsent(TrackerUtil.chunkA(wire), k -> new HashSet<>()).add(wire);
		byChunk.computeIfAbsent(TrackerUtil.chunkB(wire), k -> new HashSet<>()).add(wire);
	}

	public void remove(@NonNull Wire wire) {
		Map<ChunkCoord, Set<Wire>> byChunk = this.byWorldAndChunk.get(wire.getWorld());

		if (byChunk == null) {
			return;
		}

		removeWireFromChunkMap(byChunk, wire, TrackerUtil.chunkA(wire));
		removeWireFromChunkMap(byChunk, wire, TrackerUtil.chunkB(wire));
	}

	private static void removeWireFromChunkMap(@NonNull Map<ChunkCoord, Set<Wire>> byChunk, @NonNull Wire wire, @NonNull ChunkCoord chunk) {
		Set<Wire> wires = byChunk.get(chunk);

		if (wires == null) {
			return;
		}

		wires.remove(wire);

		if (wires.isEmpty()) {
			byChunk.remove(chunk);
		}
	}

}
