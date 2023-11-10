package me.saharnooby.plugins.leadwires.chunk;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkUnloadSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.RespawnSentEvent;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * @author saharNooby
 * @since 10:35 25.03.2020
 */
public final class LoadedChunkTracker extends PacketAdapter implements Listener {

	private final Thread mainThread;

	private final Map<Player, Set<ChunkCoord>> loaded = new HashMap<>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public LoadedChunkTracker(@NonNull Plugin plugin) {
		super(plugin, getPacketTypesToListen());

		this.mainThread = Thread.currentThread();

		// Add all online players guessing their loaded chunks.
		// This is needed in order to handle /reload correctly.
		addOnlinePlayers();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onPacketSending(PacketEvent e) {
		if (e.isCancelled()) {
			return;
		}

		Player player = e.getPlayer();
		PacketContainer packet = e.getPacket();
		PacketType type = packet.getType();

		List<ChunkCoord> loaded = null;
		ChunkCoord unloaded = null;

		this.lock.writeLock().lock();
		try {
			Set<ChunkCoord> set = this.loaded.computeIfAbsent(player, k -> new HashSet<>());

			if (type == PacketType.Play.Server.MAP_CHUNK) {
				ChunkCoord coord = new ChunkCoord(packet.getIntegers().read(0), packet.getIntegers().read(1));

				if (NMSUtil.getMinorVersion() < 9 && isUnloadPacket(packet)) {
					unloaded = coord;
					set.remove(coord);
				} else {
					loaded = Collections.singletonList(coord);
					set.add(coord);
				}
			} else if (type == PacketType.Play.Server.MAP_CHUNK_BULK) {
				int[] xs = packet.getIntegerArrays().read(0);
				int[] zs = packet.getIntegerArrays().read(1);
				loaded = new ArrayList<>(xs.length);
				for (int i = 0; i < xs.length; i++) {
					ChunkCoord coord = new ChunkCoord(xs[i], zs[i]);
					loaded.add(coord);
					set.add(coord);
				}
			} else if (type == PacketType.Play.Server.RESPAWN) {
				set.clear();
			} else {
                ChunkCoord coord;
                if (NMSUtil.getMinorVersion() >= 20 && NMSUtil.getReleaseVersion() > 1) {
                    coord = new ChunkCoord(packet.getStructures().read(0).getIntegers().read(0), packet.getStructures().read(0).getIntegers().read(1));
                } else {
                    coord = new ChunkCoord(packet.getIntegers().read(0), packet.getIntegers().read(1));
                }
				unloaded = coord;
				set.remove(coord);
			}
		} finally {
			this.lock.writeLock().unlock();
		}

		boolean isAsync = Thread.currentThread() != this.mainThread;

		if (loaded != null) {
			loaded.forEach(coord -> Bukkit.getPluginManager().callEvent(new ChunkSentEvent(isAsync, player, coord)));
		}

		if (unloaded != null) {
			Bukkit.getPluginManager().callEvent(new ChunkUnloadSentEvent(isAsync, player, unloaded));
		}

		if (type == PacketType.Play.Server.RESPAWN) {
			Bukkit.getPluginManager().callEvent(new RespawnSentEvent(isAsync, player));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.lock.writeLock().lock();
		try {
			this.loaded.remove(e.getPlayer());
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	public Set<ChunkCoord> getLoaded(@NonNull Player player) {
		this.lock.readLock().lock();
		try {
			return new HashSet<>(this.loaded.getOrDefault(player, Collections.emptySet()));
		} finally {
			this.lock.readLock().unlock();
		}
	}

	private void addOnlinePlayers() {
		this.lock.writeLock().lock();
		try {
			int dist = Math.max(1, Bukkit.getViewDistance());

			for (Player player : Bukkit.getOnlinePlayers()) {
				Set<ChunkCoord> set = this.loaded.computeIfAbsent(player, k -> new HashSet<>());

				Chunk center = player.getLocation().getChunk();
				for (int x = -dist; x <= dist; x++) {
					for (int z = -dist; z <= dist; z++) {
						set.add(new ChunkCoord(center.getX() + x, center.getZ() + z));
					}
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("deprecation")
	private static PacketType[] getPacketTypesToListen() {
		return Stream.of(
				PacketType.Play.Server.MAP_CHUNK,
				PacketType.Play.Server.MAP_CHUNK_BULK,
				PacketType.Play.Server.UNLOAD_CHUNK,
				PacketType.Play.Server.RESPAWN
		).filter(PacketType::isSupported).toArray(PacketType[]::new);
	}

	// https://wiki.vg/index.php?title=Protocol&oldid=7368#Chunk_Data
	private static boolean isUnloadPacket(@NonNull PacketContainer packet) {
		boolean groundUp = packet.getBooleans().read(0);

		if (!groundUp) {
			return false;
		}

		Object data = packet.getModifier().read(2);

		try {
			int bitmask = (int) data.getClass().getField("b").get(data) & 0xFFFF;

			return bitmask == 0;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

}
