package me.saharnooby.plugins.leadwires.wire;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.UUID;

import static java.lang.Double.parseDouble;

/**
 * A wire placed in a world between two points.
 * @author saharNooby
 * @since 21:09 24.03.2020
 */
@Data
public final class Wire {

	/**
	 * The unique id of the wire.
	 */
	private final UUID uuid;
	/**
	 * Name of the world containing the wire.
	 */
	private final String world;
	/**
	 * First point of the wire.
	 */
	private final Vector a;
	/**
	 * Second point of the wire.
	 */
	private final Vector b;

	/**
	 * Constructs a wire from UUID and two points. Both locations should be in the same world.
	 * If this is not the case, an {@link IllegalArgumentException} will be thrown.
	 * @param uuid UUID of the wire.
	 * @param a First point.
	 * @param b Second point.
	 */
	public Wire(@NonNull UUID uuid, @NonNull Location a, @NonNull Location b) {
		if (a.getWorld() != b.getWorld()) {
			throw new IllegalArgumentException("Different wolrds");
		}

		this.uuid = uuid;
		this.world = a.getWorld().getName();
		this.a = new Vector(a);
		this.b = new Vector(b);
	}

	Wire(@NonNull String data) {
		String[] split = data.split(",");
		this.uuid = UUID.fromString(split[0]);
		this.world = split[1];
		this.a = new Vector(parseDouble(split[2]), parseDouble(split[3]), parseDouble(split[4]));
		this.b = new Vector(parseDouble(split[5]), parseDouble(split[6]), parseDouble(split[7]));
	}

	String toSerializedString() {
		return this.uuid + "," + this.world + "," +
				this.a.getX() + "," + this.a.getY() + "," + this.a.getZ() + "," +
				this.b.getX() + "," + this.b.getY() + "," + this.b.getZ();
	}

}
