package me.saharnooby.plugins.leadwires.util;

import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NMSUtil {

	private static final String VERSION;
	private static final int MINOR;
	private static final int RELEASE;

	static {
		String name = Bukkit.getServer().getClass().getName();

		VERSION = name.substring(23, name.lastIndexOf('.'));

		Pattern pattern = Pattern.compile("v1_(\\d{1,2})_R(\\d{1,2})");

		Matcher matcher = pattern.matcher(VERSION);

		if (!matcher.matches()) {
			throw new IllegalStateException("Invalid server version \"" + VERSION + "\", server class is \"" + name + "\"");
		}

		MINOR = Integer.parseInt(matcher.group(1));
		RELEASE = Integer.parseInt(matcher.group(2));
	}

	/**
	 * @return Server version, for example 'v1_14_R1'.
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * @return Minor server version, for example '14' from 'v1_14_R1'.
	 */
	public static int getMinorVersion() {
		return MINOR;
	}

	/**
	 * @return Reelase version, for example '2' from 'v1_16_R2'.
	 */
	public static int getReleaseVersion() {
		return RELEASE;
	}

	public static Class<?> getNMSClass(@NonNull String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + getVersion() + '.' + name);
	}

	public static Class<?> getCraftClass(@NonNull String name) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + getVersion() + '.' + name);
	}

}
