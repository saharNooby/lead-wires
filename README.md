# lead-wires

A Spigot plugin that allows you to place "wires".

![Preview](https://saharnooby.me/download/preview.png?uuid=51da1969-48a3-42ef-82c1-6488a15af590)

[SpigotMC plugin page](https://www.spigotmc.org/resources/leadwires.76515/)

## Build

To build, you need:
- Maven
- JDK 8
- `buildSettings.properties` file with `outputDir` property set to path where you want the JAR to appear

`git clone` the repository, `cd` into its dir and run `mvn clean install`.

## Using as an API

The plugin allows you to create and remove wires programmatically, so you or your developers can implement some awesome ideas like electricity, power poles, etc.

### Adding as a dependency

First, you need to build this plugin using instructions above.

Then, you need to add the plugin as a Maven dependency:

```xml
<dependency>
    <groupId>me.saharnooby.plugins</groupId>
    <artifactId>lead-wires</artifactId>
    <version>1.1.1</version>
</dependency>
```

Alternatively, you can just download plugin from SpigotMC and link it yourself.

Add to your `plugin.yml`: `depend: ['LeadWires']` so Spigot will know to load your plugin after LeadWires.

To get an API instance, call `me.saharnooby.plugins.leadwires.LeadWires.getApi()`.

### API documentation

```java
/**
 * An API for the {@link me.saharnooby.plugins.leadwires.LeadWires} plugin.
 * <p>This API is not thread-safe and must be called only from the tick thread.
 * @author saharNooby
 * @since 10:04 26.03.2020
 */
public interface LeadWiresAPI {

	/**
	 * @return List of all existing wires in all worlds.
	 */
	Map<UUID, Wire> getWires();

	/**
	 * Finds a wire by an UUID.
	 * @param uuid UUID of the wire.
	 * @return Optional containing the found wire, or empty optional, if the wire does not exist.
	 */
	Optional<Wire> getWire(@NonNull UUID uuid);

	/**
	 * Creates a new wire from an UUID and two points in the same world.
	 * If wire with such UUID already exists, an {@link IllegalArgumentException} will be thrown.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param uuid UUID of the wire.
	 * @param a First point.
	 * @param b Second point.
	 */
	void addWire(@NonNull UUID uuid, @NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new wire from two points in the same world.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param a First point.
	 * @param b Second point.
	 * @return Generated UUID of the new wire.
	 */
	UUID addWire(@NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new thick wire from two points in the same world.
	 * Thick wires consist of 3 regular wires and appear as two times bigger.
	 * If any wire with such UUID already exists, an {@link IllegalArgumentException} will be thrown.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param uuids List of UUIDs of the new wires, must contain exactly three non-null elements.
	 * @param a First point.
	 * @param b Second point.
	 */
	void addThickWire(@NonNull List<UUID> uuids, @NonNull Location a, @NonNull Location b);

	/**
	 * Creates a new wire from an UUID and two points in the same world.
	 * Thick wires consist of 3 regular wires and appear as two times bigger.
	 * If worlds of the locations are different, an {@link IllegalArgumentException} will be thrown.
	 * @param a First point.
	 * @param b Second point.
	 * @return List of three generated UUIDs of each new wire.
	 */
	List<UUID> addThickWire(@NonNull Location a, @NonNull Location b);

	/**
	 * Removes a wire by its UUID.
	 * If the wire does not exist, this method does nothing.
	 * @param uuid UUID of the wire.
	 */
	void removeWire(@NonNull UUID uuid);

}
```