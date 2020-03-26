package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkUnloadSentEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author saharNooby
 * @since 10:21 25.03.2020
 */
@RequiredArgsConstructor
public final class WireTrackerListener implements Listener {

	private final WireTracker tracker;

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		this.tracker.onWorldChanged(e.getPlayer());
	}

	@EventHandler
	public void onChunkSent(ChunkSentEvent e) {
		doInMainThread(e, () -> this.tracker.onChunkSent(e.getPlayer(), e.getCoord()));
	}

	@EventHandler
	public void onChunkUnloadSent(ChunkUnloadSentEvent e) {
		doInMainThread(e, () -> this.tracker.onChunkUnloadSent(e.getPlayer(), e.getCoord()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.tracker.removePlayer(e.getPlayer());
	}

	private static void doInMainThread(@NonNull Event event, @NonNull Runnable task) {
		if (event.isAsynchronous()) {
			Bukkit.getScheduler().runTask(LeadWires.getInstance(), task);
		} else {
			task.run();
		}
	}

}
