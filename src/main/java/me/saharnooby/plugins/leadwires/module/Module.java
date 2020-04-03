package me.saharnooby.plugins.leadwires.module;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author saharNooby
 * @since 10:20 30.03.2020
 */
public abstract class Module {

	private final List<Listener> listeners = new ArrayList<>();
	private final Set<Integer> tasks = new HashSet<>();

	public final void enable() {
		onEnable();
	}

	public final void disable() {
		this.listeners.forEach(HandlerList::unregisterAll);

		this.tasks.forEach(Bukkit.getScheduler()::cancelTask);

		onDisable();
	}

	protected void onEnable() {

	}

	protected void onDisable() {

	}

	protected void registerListener(@NonNull Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, LeadWires.getInstance());

		this.listeners.add(listener);
	}

	protected void repeatTask(@NonNull Runnable task, int delay, int period) {
		this.tasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(LeadWires.getInstance(), task, delay, period));
	}

	protected void runTask(@NonNull Runnable task) {
		int[] id = {0};

		id[0] = Bukkit.getScheduler().runTask(LeadWires.getInstance(), () -> {
			this.tasks.remove(id[0]);

			task.run();
		}).getTaskId();

		this.tasks.add(id[0]);
	}

}
