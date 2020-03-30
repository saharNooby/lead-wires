package me.saharnooby.plugins.leadwires.wire;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author saharNooby
 * @since 21:09 24.03.2020
 */
public final class WireStorage {

	private final Map<UUID, Wire> wires = new HashMap<>();

	private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "LeadWires Storage Saver Thread"));

	public Map<UUID, Wire> getWires() {
		return Collections.unmodifiableMap(this.wires);
	}

	public boolean containsWire(@NonNull UUID uuid) {
		return this.wires.containsKey(uuid);
	}

	public Optional<Wire> getWire(@NonNull UUID uuid) {
		return Optional.ofNullable(this.wires.get(uuid));
	}

	public void addWire(@NonNull Wire wire) {
		if (this.wires.containsKey(wire.getUuid())) {
			throw new IllegalArgumentException("Wire " + wire.getUuid() + " already exists");
		}

		this.wires.put(wire.getUuid(), wire);
	}

	public Optional<Wire> removeWire(@NonNull UUID uuid) {
		return Optional.ofNullable(this.wires.remove(uuid));
	}

	public void load() throws IOException {
		this.wires.clear();

		File file = getFile();

		if (!file.exists()) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					try {
						Wire wire = new Wire(line);
						this.wires.put(wire.getUuid(), wire);
					} catch (Exception e) {
						LeadWires.getInstance().getLogger().log(Level.WARNING, "Failed to parse wire '" + line + "'", e);
					}
				}
			}
		}
	}

	public void saveAsync() {
		List<Wire> wires = new ArrayList<>(this.wires.values());

		this.executor.submit(() -> {
			try {
				save(wires);
			} catch (IOException e) {
				LeadWires.getInstance().getLogger().log(Level.SEVERE, "Failed to save wire storage", e);
			}
		});
	}

	private void save(@NonNull List<Wire> wires) throws IOException {
		File file = getFile();

		makeDirFor(file);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (Wire wire : wires) {
				writer.write(wire.toSerializedString());
				writer.newLine();
			}
		}
	}

	public void close() {
		this.executor.shutdown();

		try {
			this.executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private File getFile() {
		return new File(LeadWires.getInstance().getDataFolder(), "wires.txt");
	}

	private static void makeDirFor(@NonNull File file) throws IOException {
		File parent = file.getParentFile();

		if (!parent.exists() && !parent.mkdirs()) {
			throw new IOException("Failed to create dir " + parent);
		}
	}

}
