package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkUnloadSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.RespawnSentEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 10:21 25.03.2020
 */
@RequiredArgsConstructor
public final class WireTrackerListener implements Listener {

	private final WireTracker tracker;

	@EventHandler
	public void onChunkSent(ChunkSentEvent e) {
		doInMainThread(e, () -> {
			Set<UUID> sent = this.tracker.onChunkSent(e.getPlayer(), e.getCoord());

			if (sent.isEmpty()) {
				return;
			}

			if (LeadWires.getInstance().isEnableWireResend()) {
				// In case wires were not displayed because of any reason, we need to send them again later
				// If a wire was spawned immediately, this code does almost nothing (wires may blink when respawned)
				// This is not good, but I'm too lazy to find the original cause of wire disaappearing and this "fix" seems to work
				for (int delay : new int[] {30, 50}) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(LeadWires.getInstance(), () -> this.tracker.respawnWires(e.getPlayer(), sent), delay);
				}
			}
		});
	}

	@EventHandler
	public void onChunkUnloadSent(ChunkUnloadSentEvent e) {
		doInMainThread(e, () -> this.tracker.onChunkUnloadSent(e.getPlayer(), e.getCoord()));
	}

	@EventHandler
	public void onRespawnSent(RespawnSentEvent e) {
		this.tracker.onRespawnSent(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.tracker.onPlayerQuit(e.getPlayer());
	}

	private static void doInMainThread(@NonNull Event event, @NonNull Runnable task) {
		if (event.isAsynchronous()) {
			Bukkit.getScheduler().runTask(LeadWires.getInstance(), task);
		} else {
			task.run();
		}
	}

}
