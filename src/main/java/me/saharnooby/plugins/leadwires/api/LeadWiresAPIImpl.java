package me.saharnooby.plugins.leadwires.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.tracker.WireTracker;
import me.saharnooby.plugins.leadwires.wire.Wire;
import me.saharnooby.plugins.leadwires.wire.WireStorage;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.*;

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
	public void addThickWire(@NonNull List<UUID> uuids, Location a, Location b) {
		// The lead consists of two long bent rectangles, each angled at 45 degrees. It gives leads "X" shape.
		// Width of the rectangle is 0.025 (as set in Minecraft client code). So, width of the wire itself is 0.025 / sqrt(2), or 0.0176.
		// To place a thick wire, we can place three wires in the shape of a triangle:
		// ><><
		//  ><
		// Center of the triangle will be the initial location specified by the player.
		// The resulting wire will appear as 2x thicker that regular.

		double size = 0.025 / Math.sqrt(2);
		double half = size / 2;

		Vector dir = b.toVector().subtract(a.toVector()).setY(0).normalize();
		// A vector that is perpendicular to the horizontal direction of the wire.
		double px = -dir.getZ();
		double pz = dir.getX();

		// Bottom wire
		addWire(uuids.get(0), add(a, 0, -half, 0), add(b, 0, -half, 0));
		// Top wires
		addWire(uuids.get(1), add(a, -half * px, half, -half * pz), add(b, -half * px, half, -half * pz));
		addWire(uuids.get(2), add(a, half * px, half, half * pz), add(b, half * px, half, half * pz));
	}

	// Calls add() on a copy of the location.
	private static Location add(@NonNull Location loc, double x, double y, double z) {
		return loc.clone().add(x, y, z);
	}

	@Override
	public List<UUID> addThickWire(@NonNull Location a, @NonNull Location b) {
		List<UUID> list = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
		addThickWire(list, a, b);
		return list;
	}

	@Override
	public void removeWire(@NonNull UUID uuid) {
		this.storage.removeWire(uuid).ifPresent(wire -> {
			this.storage.saveAsync();
			this.tracker.onWireRemoved(wire);
		});
	}

}
