package me.saharnooby.plugins.leadwires.tracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 10:52 25.03.2020
 */
public final class ProtocolUtil {

	private static final int SILVERFISH_ID = getSilverfishId();

	public static void spawn(@NonNull Player player, @NonNull SentWire sent) {
		Wire wire = sent.getWire();

		spawnEntity(player, sent.getIdA(), TrackerUtil.posA(wire));
		spawnEntity(player, sent.getIdB(), TrackerUtil.posB(wire));

		attachEntities(player, sent.getIdA(), sent.getIdB());
	}

	private static void spawnEntity(@NonNull Player player, int id, @NonNull Vector loc) {
		PacketType packetType = NMSUtil.getMinorVersion() >= 19 ?
				PacketType.Play.Server.SPAWN_ENTITY :
				PacketType.Play.Server.SPAWN_ENTITY_LIVING;
		PacketContainer spawn = ProtocolLibrary.getProtocolManager().createPacket(packetType);
		spawn.getIntegers().write(0, id);

		if ((NMSUtil.getMinorVersion() >= 20 && NMSUtil.getReleaseVersion() > 4) || NMSUtil.getMinorVersion() >= 21) {
			spawn.getEntityTypeModifier().write(0, EntityType.SILVERFISH);
		} else {
			spawn.getIntegers().write(1, SILVERFISH_ID);
		}

		if (NMSUtil.getMinorVersion() < 9) {
			spawn.getIntegers().write(2, (int) (loc.getX() * 32.0D));
			spawn.getIntegers().write(3, (int) (loc.getY() * 32.0D));
			spawn.getIntegers().write(4, (int) (loc.getZ() * 32.0D));
		} else {
			spawn.getUUIDs().write(0, UUID.randomUUID());
			spawn.getDoubles().write(0, loc.getX());
			spawn.getDoubles().write(1, loc.getY());
			spawn.getDoubles().write(2, loc.getZ());
		}

		// Set entity flags to 0x20 (invisible)
		// For some reason adding meta in the spawn packet not works in 1.8 (no serializer found for Byte/byte), so make an exception.
		boolean metaIsSeparate = NMSUtil.getMinorVersion() >= 15 || NMSUtil.getMinorVersion() == 8;
		boolean metaHigher_1_19_3 = NMSUtil.getMinorVersion() > 19 || (NMSUtil.getMinorVersion() == 19 && NMSUtil.getReleaseVersion() >= 2);

		if (!metaIsSeparate) {
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			WrappedDataWatcher.WrappedDataWatcherObject object = getByteObject();
			watcher.setObject(object, (byte) 0x20, true);
			spawn.getDataWatcherModifier().write(0, watcher);
		}

		sendPacket(player, spawn);

		if (metaIsSeparate) {
			PacketContainer meta = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
			meta.getIntegers().write(0, id);

			if (metaHigher_1_19_3) {
				// 1.19.3 changes entity metadata packet
				// https://www.spigotmc.org/threads/unable-to-modify-entity-metadata-packet-using-protocollib-1-19-3.582442/
				WrappedDataWatcher.WrappedDataWatcherObject object = getByteObject();
				meta.getDataValueCollectionModifier().write(0, Collections.singletonList(new WrappedDataValue(
						object.getIndex(),
						object.getSerializer(),
						(byte) 0x20
				)));
			} else {
				List<Object> list;

				if (NMSUtil.getMinorVersion() < 15) {
					list = Collections.singletonList(new WrappedWatchableObject(0, (byte) 0x20).getHandle());
				} else {
					WrappedDataWatcher.WrappedDataWatcherObject object = getByteObject();
					list = Collections.singletonList(new WrappedWatchableObject(object, (byte) 0x20).getHandle());
				}

				meta.getModifier().write(1, new ArrayList<>(list));
			}

			sendPacket(player, meta);
		}
	}

	private static WrappedDataWatcher.WrappedDataWatcherObject getByteObject() {
		return new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
	}

	private static int getSilverfishId() {
		// https://wiki.vg/Entities
		// https://wiki.vg/Pre-release_protocol#Entity_Metadata
		final int latestId = 85;
		final int latestVersion = 20;

		int minorVersion = NMSUtil.getMinorVersion();

		if (minorVersion > latestVersion) {
			// May work on newer versions
			return latestId;
		}

		switch (minorVersion) {
			case latestVersion:
				return latestId;
			case 19:
				return 80;
			case 18:
			case 17:
				return 77;
			case 16:
				switch (NMSUtil.getReleaseVersion()) {
					case 3:
					case 2:
						return 72;
					default:
						return 71;
				}
			case 15:
				return 65;
			case 14:
				return 64;
			case 13:
				return 61;
			case 12:
			case 11:
			case 10:
			case 9:
			case 8:
			default:
				return 60;
		}
	}

	private static void attachEntities(@NonNull Player player, int attached, int holder) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ATTACH_ENTITY);

		if (NMSUtil.getMinorVersion() < 9) {
			packet.getIntegers().write(0, 1);
			packet.getIntegers().write(1, attached);
			packet.getIntegers().write(2, holder);
		} else {
			packet.getIntegers().write(0, attached);
			packet.getIntegers().write(1, holder);
		}

		sendPacket(player, packet);
	}

	public static void despawn(@NonNull Player player, @NonNull SentWire wire) {
		if (NMSUtil.getMinorVersion() >= 17) {
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);

			if (packet.getModifier().getField(0).getType().getName().equals("it.unimi.dsi.fastutil.ints.IntList")) {
				despawn_1_17_1(player, wire);
			} else {
				despawn_1_17(player, wire);
			}
		} else {
			despawn_1_16(player, wire);
		}
	}

	private static void despawn_1_17_1(@NonNull Player player, @NonNull SentWire wire) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getModifier().write(0, createEntityIdIntList(wire));
		sendPacket(player, packet);
	}

	private static void despawn_1_17(@NonNull Player player, @NonNull SentWire wire) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegers().write(0, wire.getIdA());
		sendPacket(player, packet);

		packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegers().write(0, wire.getIdB());
		sendPacket(player, packet);
	}

	private static void despawn_1_16(@NonNull Player player, @NonNull SentWire wire) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().write(0, new int[] {wire.getIdA(), wire.getIdB()});
		sendPacket(player, packet);
	}

	private static Object createEntityIdIntList(@NonNull SentWire wire) {
		try {
			//noinspection PrimitiveArrayArgumentToVarargsMethod
			return Class.forName("it.unimi.dsi.fastutil.ints.IntArrayList")
					.getConstructor(int[].class)
					.newInstance(new int[] {wire.getIdA(), wire.getIdB()});
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private static void sendPacket(@NonNull Player player, @NonNull PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			// ProtocolLib 5.0.0 will NOT throw InvocationTargetException
			throw new RuntimeException("Failed to send packet " + packet.getType() + " to " + player.getName(), e);
		}
	}

}
