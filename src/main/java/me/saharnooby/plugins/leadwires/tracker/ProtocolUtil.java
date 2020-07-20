package me.saharnooby.plugins.leadwires.tracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 10:52 25.03.2020
 */
public final class ProtocolUtil {

	public static void spawn(@NonNull Player player, @NonNull SentWire sent) {
		//System.out.println("Spawn " + sent.getWire().getUuid() + " to " + player.getName() + " [" + new Exception().getStackTrace()[2] + "]");

		Wire wire = sent.getWire();

		spawnEntity(player, sent.getIdA(), TrackerUtil.posA(wire));
		spawnEntity(player, sent.getIdB(), TrackerUtil.posB(wire));

		attachEntities(player, sent.getIdA(), sent.getIdB());
	}

	private static void spawnEntity(@NonNull Player player, int id, @NonNull Vector loc) {
		PacketContainer spawn = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		spawn.getIntegers().write(0, id);
		spawn.getIntegers().write(1, getSilverfishId());

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

		if (!metaIsSeparate) {
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			WrappedDataWatcher.WrappedDataWatcherObject object = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
			watcher.setObject(object, (byte) 0x20, true);
			spawn.getDataWatcherModifier().write(0, watcher);
		}

		sendPacket(player, spawn);

		if (metaIsSeparate) {
			PacketContainer meta = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
			meta.getIntegers().write(0, id);

			List<Object> list;

			if (NMSUtil.getMinorVersion() < 15) {
				list = Collections.singletonList(new WrappedWatchableObject(0, (byte) 0x20).getHandle());
			} else {
				WrappedDataWatcher.WrappedDataWatcherObject object = new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
				list = Collections.singletonList(new WrappedWatchableObject(object, (byte) 0x20).getHandle());
			}
			meta.getModifier().write(1, new ArrayList<>(list));

			sendPacket(player, meta);
		}
	}

	private static int getSilverfishId() {
		switch (NMSUtil.getMinorVersion()) {
			case 16:
				return 71;
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
		//System.out.println("Despawn " + wire.getWire().getUuid() + " to " + player.getName());

		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().write(0, new int[] {wire.getIdA(), wire.getIdB()});
		sendPacket(player, packet);
	}

	private static void sendPacket(@NonNull Player player, @NonNull PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Failed to send packet " + packet.getType() + " to " + player.getName(), e);
		}
	}

}
