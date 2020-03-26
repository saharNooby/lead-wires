package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.chunk.ChunkCoord;
import me.saharnooby.plugins.leadwires.chunk.LoadedChunkTracker;
import me.saharnooby.plugins.leadwires.util.EntityId;
import me.saharnooby.plugins.leadwires.wire.Wire;
import me.saharnooby.plugins.leadwires.wire.WireStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author saharNooby
 * @since 10:20 25.03.2020
 */
public final class WireTracker {

	private final WireStorage storage;
	private final LoadedChunkTracker chunkTracker;
	private final Map<Player, PlayerTrackerData> map = new HashMap<>();
	private final WireCache cache = new WireCache();

	public WireTracker(@NonNull WireStorage storage, @NonNull LoadedChunkTracker chunkTracker) {
		this.storage = storage;
		this.chunkTracker = chunkTracker;
		storage.getWires().values().forEach(this.cache::add);
	}

	public void onChunkSent(@NonNull Player player, @NonNull ChunkCoord coord) {
		PlayerTrackerData data = getData(player);

		Set<ChunkCoord> loaded = this.chunkTracker.getLoaded(player);

		// Send any wire that is not yet loaded and ends in the sent chunk.
		for (Wire wire : this.cache.getWiresInChunk(player.getWorld(), coord)) {
			if (!data.getSentWires().containsKey(wire.getUuid()) && TrackerUtil.containsBothEnds(loaded, wire)) {
				data.getSentWires().put(wire.getUuid(), sendWire(player, wire));
			}
		}
	}

	public void onChunkUnloadSent(@NonNull Player player, @NonNull ChunkCoord coord) {
		PlayerTrackerData data = getData(player);

		// Remove any wire that has any of its ends in the unloaded chunk.
		for (Iterator<SentWire> i = data.getSentWires().values().iterator(); i.hasNext(); ) {
			SentWire wire = i.next();

			if (TrackerUtil.hasAnyEndInChunk(wire.getWire(), coord)) {
				ProtocolUtil.despawn(player, wire);
				i.remove();
			}
		}
	}

	public void onRespawnSent(@NonNull Player player) {
		PlayerTrackerData data = getData(player);

		// Just cleanup all sent wires from the old world; new entities will be sent when chunks are sent.
		data.getSentWires().clear();
	}

	public void onPlayerQuit(@NonNull Player player) {
		this.map.remove(player);
	}

	public void onWireAdded(@NonNull Wire wire) {
		this.cache.add(wire);

		World world = Bukkit.getWorld(wire.getWorld());

		if (world == null) {
			return;
		}

		// Send the wire to any player that has both points loaded.
		for (Player player : world.getPlayers()) {
			PlayerTrackerData data = getData(player);

			if (!data.getSentWires().containsKey(wire.getUuid()) && TrackerUtil.containsBothEnds(this.chunkTracker.getLoaded(player), wire)) {
				data.getSentWires().put(wire.getUuid(), sendWire(player, wire));
			}
		}
	}

	public void onWireRemoved(@NonNull Wire wire) {
		this.cache.remove(wire);

		World world = Bukkit.getWorld(wire.getWorld());

		if (world == null) {
			return;
		}

		for (Player player : world.getPlayers()) {
			PlayerTrackerData data = getData(player);

			SentWire sent = data.getSentWires().remove(wire.getUuid());

			if (sent != null) {
				ProtocolUtil.despawn(player, sent);
			}
		}
	}

	// Full check.
	public void checkPlayer(@NonNull Player player) {
		PlayerTrackerData data = getData(player);

		Set<ChunkCoord> loaded = this.chunkTracker.getLoaded(player);

		for (Iterator<SentWire> i = data.getSentWires().values().iterator(); i.hasNext(); ) {
			SentWire wire = i.next();

			if (!TrackerUtil.containsBothEnds(loaded, wire.getWire()) || !this.storage.containsWire(wire.getWire().getUuid())) {
				ProtocolUtil.despawn(player, wire);
				i.remove();
			}
		}

		String world = player.getWorld().getName();

		for (Wire wire : this.storage.getWires().values()) {
			if (!wire.getWorld().equals(world)) {
				continue;
			}

			if (data.getSentWires().containsKey(wire.getUuid())) {
				continue;
			}

			if (TrackerUtil.containsBothEnds(loaded, wire)) {
				data.getSentWires().put(wire.getUuid(), sendWire(player, wire));
			}
		}
	}

	public void checkAllPlayers() {
		Bukkit.getOnlinePlayers().forEach(this::checkPlayer);
	}

	public void removeAllPlayers() {
		this.map.forEach((player, data) -> data.getSentWires().values().forEach(w -> ProtocolUtil.despawn(player, w)));
		this.map.clear();
	}

	private PlayerTrackerData getData(@NonNull Player player) {
		return this.map.computeIfAbsent(player, k -> new PlayerTrackerData());
	}

	private static SentWire sendWire(@NonNull Player player, @NonNull Wire wire) {
		SentWire sent = new SentWire(wire, EntityId.next(), EntityId.next());
		ProtocolUtil.spawn(player, sent);
		return sent;
	}

}
