package sphero.common;

import java.util.Collections;
import java.util.List;

/**
 * Aggregiert Informationen über das Ergebnis des Routing Algorithmus
 */
public class RoutingResult {

    private final List<Path> route;
    private final double cost;

    private final boolean valid;
    private final String errorMessage;

    /**
     * Konstruktor für erfolgreiche Berechnungen
     * @param route gefundene, beste Route
     * @param cost deren Kosten
     */
    public RoutingResult(List<Path> route, double cost) {
        this.route = Collections.unmodifiableList(route);
        this.cost = cost;
        valid = true;
        errorMessage = null;
    }

    /**
     * Konstruktur für Berechnungen ohne zulässiges Ergebnis
     * @param errorMessage Fehlernachricht zur Ausgabe
     */
    public RoutingResult(String errorMessage){
        this.errorMessage = errorMessage;
        route = null;
        cost = -1;
        valid = false;
    }

    /**
     * @return geordnete Liste an Pfaden, die die einzelnen Einsatzorte verbinden
     */
    public List<Path> getRoute() {
        return route;
    }

    /**
     * @return Gesamt-Kosten der Route
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return Konnte eine zulässige Route gefunden werden
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return Fehler-Nachricht, falls die keine Route gefunden werden konnte
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
