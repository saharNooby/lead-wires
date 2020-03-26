package me.saharnooby.plugins.leadwires.util;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author saharNooby
 * @since 20:46 24.03.2020
 */
public final class EntityId {

	private static final AtomicInteger nextId = new AtomicInteger(20_000_000);

	public static int next() {
		try {
			Field field = NMSUtil.getNMSClass("Entity").getDeclaredField("entityCount");

			field.setAccessible(true);

			if (field.getType() == Integer.TYPE) {
				int count = (int) field.get(null);
				field.set(null, count + 1);
				return count;
			} else {
				return ((AtomicInteger) field.get(null)).incrementAndGet();
			}
		} catch (Exception e) {
			return nextId.incrementAndGet();
		}
	}

}
