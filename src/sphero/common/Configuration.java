package sphero.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Eine Konfiguration aggregiert alle Daten zur Routen-Berechnung
 */
public class Configuration {
    private final Map map;
    private final Location start;
    private final List<Location> locationList;

    /**
     * Erstellt eine Configuration mit dem angegebenen Builder
     *
     * @param builder - ConfigurationBuilder mit den Konfigurations-Daten
     */
    public Configuration(ConfigurationBuilder builder) {
        map = builder.getMap();
        start = builder.getStart();
        List<Location> tempList = builder.getLocations();
        tempList.sort(Comparator.comparing(Location::getId));
        locationList = Collections.unmodifiableList(tempList);
        Location.resetCounter();
    }

    /**
     * @return Karte auf der sich der Sphero bewegen soll
     */
    public Map getMap() {
        return map;
    }

    /**
     * @return Start-Position des Spheros
     */
    public Location getStart() {
        return start;
    }

    /**
     * @return Liste der Orte auf der Karte, geordnet nach ID
     */
    public List<Location> getOrderedLocations() {
        return locationList;
    }
}
