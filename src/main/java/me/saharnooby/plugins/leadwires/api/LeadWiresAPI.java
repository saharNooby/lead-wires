package me.saharnooby.plugins.leadwires.api;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * An API for the {@link me.saharnooby.plugins.leadwires.LeadWires} plugin.
 * <p>This API is not thread-safe and must be called only from the tick thread.
 * @author saharNooby
 * @since 10:04 26.03.2020
 */
public interface LeadWiresAPI {

	/**
	 * @return List of all existing wires in all worlds.
	 */
	Map<UUID, Wire> getWires();

	/**
	 * Finds a wire by an UUID.
	 * @param uuid UUID of the wire.
	 * @return Optional containing the found wire, or empty optional, if the wire does not exist.
	 */
	Optional<Wire> getWire(@NonNull UUID uuid);

	/**
	 * Creates a new wire from an UUID and two points in the same world.
	 * If wire with such UUID already exists, an {@link IllegalArgumentException} will be thrown.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param uuid UUID of the wire.
	 * @param a First point.
	 * @param b Second point.
	 */
	void addWire(@NonNull UUID uuid, @NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new wire from two points in the same world.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param a First point.
	 * @param b Second point.
	 * @return Generated UUID of the new wire.
	 */
	UUID addWire(@NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new thick wire from two points in the same world.
	 * Thick wires consist of 3 regular wires and appear as two times bigger.
	 * If any wire with such UUID already exists, an {@link IllegalArgumentException} will be thrown.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param uuids List of UUIDs of the new wires, must contain exactly three non-null elements.
	 * @param a First point.
	 * @param b Second point.
	 */
	void addThickWire(@NonNull List<UUID> uuids, @NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new wire from an UUID and two points in the same world.
	 * Thick wires consist of 3 regular wires and appear as two times bigger.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param a First point.
	 * @param b Second point.
	 * @return List of three generated UUIDs of each new wire.
	 */
	List<UUID> addThickWire(@NonNull Location a, @NonNull Location b);

	/**
	 * Removes a wire by its UUID.
	 * If the wire does not exist, this method does nothing.
	 * @param uuid UUID of the wire.
	 */
	void removeWire(@NonNull UUID uuid);

}
