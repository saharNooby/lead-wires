package me.saharnooby.plugins.leadwires.module.placement;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.util.FileStorage;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.block.Block;

import java.util.*;

/**
 * @author saharNooby
 * @since 10:30 30.03.2020
 */
final class PlacedLeadStorage extends FileStorage<UUID> {

	private final Set<UUID> leads = new HashSet<>();

	private final PlacedLeadCache cache = new PlacedLeadCache();

	public void addLead(@NonNull Wire wire) {
		if (this.leads.add(wire.getUuid())) {
			this.cache.addWire(wire);

			saveAsync();
		}
	}

	public void removeLead(@NonNull Wire wire) {
		if (this.leads.remove(wire.getUuid())) {
			this.cache.removeWire(wire);

			saveAsync();
		}
	}

	public Set<UUID> getInBlock(@NonNull Block block) {
		return this.cache.getInBlock(block);
	}

	public List<UUID> getAllLeads() {
		return Collections.unmodifiableList(Arrays.asList(this.leads.toArray(new UUID[0])));
	}

	@Override
	protected String getFileName() {
		return "placed-leads.txt";
	}

	@Override
	protected Collection<UUID> getAll() {
		return this.leads;
	}

	@Override
	protected void clear() {
		this.leads.clear();
		this.cache.clear();
	}

	@Override
	protected void addParsed(UUID object) {
		LeadWires.getApi().getWire(object).ifPresent(wire -> {
			this.leads.add(wire.getUuid());

			this.cache.addWire(wire);
		});
	}

	@Override
	protected UUID parse(String line) {
		return UUID.fromString(line);
	}

	@Override
	protected String serialize(UUID object) {
		return object.toString();
	}

}
