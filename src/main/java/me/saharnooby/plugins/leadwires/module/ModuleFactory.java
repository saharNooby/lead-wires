package me.saharnooby.plugins.leadwires.module;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.module.placement.LeadPlacementModule;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author saharNooby
 * @since 10:25 30.03.2020
 */
public final class ModuleFactory {

	public static Optional<Module> create(@NonNull String id, @NonNull ConfigurationSection config) {
		//noinspection SwitchStatementWithTooFewBranches
		switch (id) {
			case "leadPlacement":
				return Optional.of(new LeadPlacementModule(config));
		}

		return Optional.empty();
	}

}
