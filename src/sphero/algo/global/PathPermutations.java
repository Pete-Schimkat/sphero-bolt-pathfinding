package sphero.algo.global;

import sphero.algo.local.AllPairShortestPaths;
import sphero.common.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Durchsucht den gesamten Lösungsraum um die optimale Route zu finden
 * Ab ca. n > 15 nicht mehr praktikabel
 * Zeitkomplexität: O(n*n!)
 *
 * @see sphero.common.RoutingAlgorithm
 */
public class PathPermutations implements RoutingAlgorithm {

    private final AllPairShortestPaths allPairShortestPaths;

    /**
     * @param allPairShortestPaths Dependency-Injection einer AllPairShortestPaths-Instanz
     */
    public PathPermutations(AllPairShortestPaths allPairShortestPaths) {
        this.allPairShortestPaths = allPairShortestPaths;
    }

    /**
     * @see sphero.common.RoutingAlgorithm
     */
    @Override
    public RoutingResult calculate(Configuration config) {
        Path[][] paths = allPairShortestPaths.allPairs(config.getOrderedLocations(), config.getMap());
        if(paths == null) return new RoutingResult("Mindestens ein Ort auf der Karte ist unerreichbar.");
        CalculationContext context = new CalculationContext(config.getOrderedLocations(), paths);
        return new RoutingResult(context.getShortestRoute(), context.getDistance());
    }

    /**
     * Speichert alle Kontext-Daten für eine Routen-Berechnung
     */
    private static class CalculationContext {
        private final List<Location> locationList;
        private final Path[][] paths;

        private final boolean[] used; //Markiert die bereits besuchten Orte einer (Teil-)Lösung
        private final int[] currentRoute; //geordnetes Array der Orte auf der Route

        private final int[] min_route; //Kürzeste, bisher gefundene, Route
        private double min_distance = Double.MAX_VALUE; //Distanz von min_route

        /**
         * @param locationList Liste der Orte auf der Karte
         * @param paths        Path-Matrix von AllPairshortestPaths
         */
        public CalculationContext(List<Location> locationList, Path[][] paths) {
            this.locationList = locationList;
            this.paths = paths;
            used = new boolean[locationList.size()];
            used[0] = true;
            currentRoute = new int[locationList.size()];
            min_route = new int[locationList.size()];
            recurse(0, 1, 0); //Beginne Berechnung am Start-Feld
        }

        /**
         * Erweitert die aktuelle Teillösung um eine Location
         * Ruft sich selbst rekursiv auf, bis eine Lösung gefunden wurde
         * Aktualisiert die kürzeste Route, falls die gefundene Lösung besser ist
         *
         * @param v        Ausgangsknoten
         * @param i        Routen-Index
         * @param distance Zwischendistanz
         */
        void recurse(int v, int i, double distance) {
            if (distance >= min_distance) return; //Einfaches Bounding
            if (i == paths.length) { //Kürzere Route gefunden
                distance += paths[v][0].getCost();
                if (distance >= min_distance) return;
                min_distance = distance;
                System.arraycopy(currentRoute, 0, min_route, 0, i);
                return;
            }

            for (int w = 1; w < paths.length; w++) { //Alle Destinations durchlaufen
                if (!used[w]) { //Destination noch nicht besucht?
                    used[w] = true; //Besuche Destintion
                    currentRoute[i] = w;
                    recurse(w, i + 1, distance + paths[v][w].getCost()); //Erweitere Route um nächstes Ziel
                    used[w] = false;
                }
            }
        }

        /**
         * Konvertiert die optimale Route in das geforderte Format
         *
         * @return Kürzeste Route
         */
        public List<Path> getShortestRoute() {
            if(min_route.length < 2) return new ArrayList<>();
            List<Path> route = new ArrayList<>(locationList.size());
            for (int i = 0; i < min_route.length; i++) {
                Path p = paths[min_route[i]][min_route[(i + 1)%min_route.length]];
                route.add(p);
            }
            return route;
        }

        /**
         * @return Distanz der kürzesten Route
         */
        public double getDistance() {
            return min_distance;
        }
    }

}
