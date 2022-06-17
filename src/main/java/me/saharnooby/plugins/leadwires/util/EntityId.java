package me.saharnooby.plugins.leadwires.util;

import me.saharnooby.plugins.leadwires.LeadWires;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * @author saharNooby
 * @since 20:46 24.03.2020
 */
public final class EntityId {

	private static final AtomicInteger nextId = new AtomicInteger(20_000_000);

	private static boolean errorPrinted;

	public static int next() {
		try {
			boolean v17 = NMSUtil.getMinorVersion() >= 17;

			Class<?> entityClass = NMSUtil.getNMSClass(v17 ? "world.entity.Entity" : "Entity");

			Field field = null;

			if (v17) {
				for (Field f : entityClass.getDeclaredFields()) {
					if (f.getType() == AtomicInteger.class && (f.getModifiers() & Modifier.STATIC) != 0) {
						field = f;

						break;
					}
				}

				if (field == null) {
					throw new IllegalStateException("AtomicInteger field not found in Entity class");
				}
			} else {
				field = entityClass.getDeclaredField("entityCount");
			}

			field.setAccessible(true);

			if (field.getType() == Integer.TYPE) {
				int count = (int) field.get(null);
				field.set(null, count + 1);
				return count;
			} else {
				return ((AtomicInteger) field.get(null)).incrementAndGet();
			}
		} catch (Exception e) {
			if (!errorPrinted) {
				LeadWires.getInstance().getLogger().log(Level.WARNING, "Failed to get new entity id with reflection, this may or may not cause bugs", e);

				errorPrinted = true;
			}

			return nextId.incrementAndGet();
		}
	}

}
