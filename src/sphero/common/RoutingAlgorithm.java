package sphero.common;

public interface RoutingAlgorithm {

    /**
     * Führ einen Algorithmus zur Routen-Berechnung aus
     *
     * @param config Karten-Konfigurationen mit den Informationen zum Routing-Szenario
     * @return RouteObserver-Objekt zum Abruf des Routing-Ergebnisses
     */
    RoutingResult calculate(Configuration config);
}
