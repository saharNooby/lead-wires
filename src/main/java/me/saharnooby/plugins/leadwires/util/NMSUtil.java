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

		if (name.equals("org.bukkit.craftbukkit.CraftServer")) {
			String bukkitVersion = Bukkit.getBukkitVersion(); // z.B. 1.21-R0.1-SNAPSHOT
			String[] versionParts = bukkitVersion.split("-")[0].split("\\."); // z.B. [1, 21]

			VERSION = bukkitVersion.split("-")[0];
			MINOR = Integer.parseInt(versionParts[1]);
			RELEASE = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;
		} else {
			VERSION = name.substring(23, name.lastIndexOf('.'));
			Pattern pattern = Pattern.compile("v1_(\\d{1,2})_R(\\d{1,2})");
			Matcher matcher = pattern.matcher(VERSION);
			if (!matcher.matches()) {
				throw new IllegalStateException("Invalid server version \"" + VERSION + "\", server class is \"" + name + "\"");
			}
			MINOR = Integer.parseInt(matcher.group(1));
			RELEASE = Integer.parseInt(matcher.group(2));
		}
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
	 * @return Release version, for example '2' from 'v1_16_R2'.
	 */
	public static int getReleaseVersion() {
		return RELEASE;
	}

	public static Class<?> getNMSClass(@NonNull String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft." + (NMSUtil.getMinorVersion() >= 17 ? "" : "server." + getVersion() + '.') + name);
	}

}
