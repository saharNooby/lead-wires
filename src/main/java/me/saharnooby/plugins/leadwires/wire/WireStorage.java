package me.saharnooby.plugins.leadwires.wire;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.util.FileStorage;

import java.util.*;

/**
 * @author saharNooby
 * @since 21:09 24.03.2020
 */
public final class WireStorage extends FileStorage<Wire> {

	private final Map<UUID, Wire> wires = new HashMap<>();

	public Map<UUID, Wire> getWires() {
		return Collections.unmodifiableMap(this.wires);
	}

	public boolean containsWire(@NonNull UUID uuid) {
		return this.wires.containsKey(uuid);
	}

	public Optional<Wire> getWire(@NonNull UUID uuid) {
		return Optional.ofNullable(this.wires.get(uuid));
	}

	public void addWire(@NonNull Wire wire) {
		if (this.wires.containsKey(wire.getUuid())) {
			throw new IllegalArgumentException("Wire " + wire.getUuid() + " already exists");
		}

		this.wires.put(wire.getUuid(), wire);
	}

	public Optional<Wire> removeWire(@NonNull UUID uuid) {
		return Optional.ofNullable(this.wires.remove(uuid));
	}

	@Override
	protected String getFileName() {
		return "wires.txt";
	}

	@Override
	protected Collection<Wire> getAll() {
		return this.wires.values();
	}

	@Override
	protected void clear() {
		this.wires.clear();
	}

	@Override
	protected void addParsed(Wire object) {
		this.wires.put(object.getUuid(), object);
	}

	@Override
	protected Wire parse(String line) {
		return new Wire(line);
	}

	@Override
	protected String serialize(Wire object) {
		return object.toSerializedString();
	}

}
