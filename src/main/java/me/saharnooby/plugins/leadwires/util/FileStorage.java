package me.saharnooby.plugins.leadwires.util;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Saves elements in a text file, line by line.
 * @author saharNooby
 * @since 10:49 30.03.2020
 */
public abstract class FileStorage<T> {

	private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "LeadWires Storage Saver Thread"));

	public void load() throws IOException {
		clear();

		File file = getFile();

		if (!file.exists()) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}

				try {
					addParsed(parse(line));
				} catch (Exception e) {
					LeadWires.getInstance().getLogger().log(Level.SEVERE, "Failed to parse an object '" + line + "'", e);
				}
			}
		}
	}

	public void saveAsync() {
		List<T> objects = new ArrayList<>(getAll());

		this.executor.submit(() -> {
			try {
				save(objects);
			} catch (IOException e) {
				LeadWires.getInstance().getLogger().log(Level.SEVERE, "Failed to save storage", e);
			}
		});
	}

	private void save(@NonNull List<T> objects) throws IOException {
		File file = getFile();

		makeDirFor(file);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (T object : objects) {
				writer.write(serialize(object));
				writer.newLine();
			}
		}
	}

	private File getFile() {
		return new File(LeadWires.getInstance().getDataFolder(), getFileName());
	}

	private static void makeDirFor(@NonNull File file) throws IOException {
		File parent = file.getParentFile();

		if (!parent.exists() && !parent.mkdirs()) {
			throw new IOException("Failed to create dir " + parent);
		}
	}

	public void close() {
		this.executor.shutdown();

		try {
			this.executor.awaitTermination(30, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract String getFileName();

	protected abstract Collection<T> getAll();

	protected abstract void clear();

	protected abstract void addParsed(T object);

	protected abstract T parse(String line);

	protected abstract String serialize(T object);

}
