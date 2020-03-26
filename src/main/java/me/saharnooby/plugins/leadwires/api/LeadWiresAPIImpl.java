package me.saharnooby.plugins.leadwires.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.tracker.WireTracker;
import me.saharnooby.plugins.leadwires.wire.Wire;
import me.saharnooby.plugins.leadwires.wire.WireStorage;
import org.bukkit.Location;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 12:12 25.03.2020
 */
@RequiredArgsConstructor
public final class LeadWiresAPIImpl implements LeadWiresAPI {

	private final WireStorage storage;
	private final WireTracker tracker;

	@Override
	public Map<UUID, Wire> getWires() {
		return this.storage.getWires();
	}

	@Override
	public Optional<Wire> getWire(@NonNull UUID uuid) {
		return this.storage.getWire(uuid);
	}

	@Override
	public void addWire(@NonNull UUID uuid, @NonNull Location a, @NonNull Location b) {
		Wire wire = new Wire(uuid, a, b);
		this.storage.addWire(wire);
		this.storage.saveAsync();
		this.tracker.onWireAdded(wire);
	}

	@Override
	public UUID addWire(@NonNull Location a, @NonNull Location b) {
		UUID uuid = UUID.randomUUID();
		addWire(uuid, a, b);
		return uuid;
	}

	@Override
	public void removeWire(@NonNull UUID uuid) {
		this.storage.removeWire(uuid).ifPresent(wire -> {
			this.storage.saveAsync();
			this.tracker.onWireRemoved(wire);
		});
	}

}
