package sphero.algo.global;

import sphero.algo.local.AllPairShortestPaths;
import sphero.common.Configuration;
import sphero.common.Location;
import sphero.common.Path;
import sphero.common.RoutingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Heuristische Suche nach einer möglichst optimalen Route unter Berücksichtigung von Deadlines
 * Startet mit Zufallslösung und verbessert diese so oft wie möglich
 * Kann den besten Wert aus beliebig vielen Iterationen mit jeweils neuen Zufallslösungen auswählen
 *
 * @see sphero.common.RoutingAlgorithm
 * @see sphero.algo.global.LocalSearch
 */
public class LocalSearch_TimeCritical extends LocalSearch {

    private boolean check = false;

    /**
     * Zu iterations: Höhere Anzahl an Durchläufen reduziert die Streuung, erhöht aber die Rechenzeit
     *
     * @param allPairShortestPaths Dependency-Injection einer AllPairShortestPaths-Instanz
     * @param iterations           Anzahl an Zufalls-Durchläufen
     */
    public LocalSearch_TimeCritical(AllPairShortestPaths allPairShortestPaths, int iterations) {
        super(allPairShortestPaths, iterations);
    }

    @Override
    public RoutingResult calculate(Configuration config) {
        RoutingResult res = super.calculate(config);
        if(res.isValid() && check){
            int x = violatedDeadlines(res.getRoute());
            if(x != 0) return new RoutingResult("Keine Route gefunden, die alle Anforderungen erfüllt");
        }
        return res;
    }

    /**
     * Erstellt eine zufällige Tour, die bei der Start-Position beginnt
     *
     * @param configuration Routing-Szenario für die Tour
     * @return zufällige Tour als Array
     */
    protected static Location[] randomOrder(Configuration configuration) {
        List<Location> copy = new ArrayList<>(configuration.getOrderedLocations());
        Collections.shuffle(copy);
        Location[] arr = new Location[copy.size()];
        copy.toArray(arr);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getType() == Location.Type.START) {
                Location start = arr[i];
                arr[i] = arr[0];
                arr[0] = start;
            }
        }
        return arr;
    }

    /**
     * Konvertiert eine Tour zu einer Route
     * (Tour ist ein Array ohne konkreten Start; Route eine Liste die an der Start-Poisition beginnt und endet)
     *
     * @param matrix Path-Matrix von AllPairShortestPaths
     * @param tour   Tour
     * @return Route
     */
    protected static List<Path> toRoute(Path[][] matrix, Location[] tour) {
        List<Path> route = new ArrayList<>(tour.length + 1);
        int start = 0;
        for (int i = 0; i < tour.length; i++) {
            if (tour[i].getType() == Location.Type.START) {
                start = i;
                break;
            }
        }
        for (int i = 0; i < tour.length; i++) {
            Path p = matrix[tour[(start + i) % tour.length].getId()][tour[(start + i + 1) % tour.length].getId()];
            route.add(p);
        }
        return route;
    }

    /**
     * Führt einen Durchlauf des heuristischen Algorithmus durch
     * Sucht ein lokales Minimum
     * Schreibt die neue, verbesserte Tour direkt in die übergebene Tour
     *
     * @param distances Path-Matrix von AllPairShortestPaths
     * @param tour      gegebene Tour als Startlösung
     * @return Wert, um den die alte Lösung verbessert wurde
     */
    @Override
    protected double minimize(Path[][] distances, Location[] tour) {
        int i = 0;
        double res = 0;
        double diff = 1;
        while (diff != 0 && i < 100) {
            i++;
            diff = improve_twoOpt(distances, tour);
            res -= diff;
        }
        return res;
    }

    /**
     * Führt die 2-Opt-Heuristik auf einer gegebenen Tour aus
     *
     * @param distances Path-Matrix von AllPairShortestPaths
     * @param tour      zu verbessernde Tour
     * @return Größe der Verbesserung durch 2-Opt
     */
    @Override
    protected double improve_twoOpt(Path[][] distances, Location[] tour) {
        double diff = 0;
        double cost = cost(distances, tour);
        Location[] res = tour;
        for (int i = 0; i < tour.length - 1; i++) {
            for (int j = i + 1; j < tour.length; j++) {
                Location[] alt = reversed(tour, i + 1, j);
                double alt_cost = cost(distances, alt);
                if (cost > alt_cost) {
                    diff = cost - alt_cost;
                    res = alt;
                    cost = alt_cost;
                }
            }
        }
        System.arraycopy(res, 0, tour, 0, tour.length);
        return diff;
    }

    /**
     * Erstellt eine Kopie der Tour und kehrt das gegebene Subarray darin um
     *
     * @param tour Ziel-Array
     * @param i    Start des Subarrays (inklusiv)
     * @param j    Ende des Subarrays (inklusiv)
     */
    protected Location[] reversed(Location[] tour, int i, int j) {
        Location[] copy = new Location[tour.length];
        System.arraycopy(tour, 0, copy, 0, i);
        if (j < tour.length - 1) System.arraycopy(tour, j + 1, copy, j + 1, tour.length - j - 1);
        for (int a = j, b = i; a >= i; a--, b++) {
            copy[a] = tour[b];
        }
        return copy;
    }

    /**
     * Berechnet die Kosten einer Route unter Berücksichtigung von Straf-Kosten für verpasste Deadlines
     *
     * @param route Route
     * @return Kosten
     */
    @Override
    protected double cost(List<Path> route) {
        double distance = 0;
        double time = 0;
        double penalty = 0;
        for (Path p : route) {
            distance += p.getDistance();
            time += p.getTravelTime();
            if (p.getTo().isTimeCritical() && p.getTo().getDeadline() < time) {
                penalty += 100000;
            }
        }
        double cost = distance + penalty;
        return cost;
    }

    /**
     * Berechnet die Kosten einer Tour unter Berücksichtigung von Straf-Kosten für verpasste Deadlines
     *
     * @param matrix Path-Matrix von AllPairShortestPaths
     * @param tour   Tour
     * @return Kosten
     */
    @Override
    protected double cost(Path[][] matrix, Location[] tour) {
        double distance = 0;
        double time = 0;
        double penalty = 0;
        for (int i = 0; i < tour.length; i++) {
            Location a = tour[i];
            Location b = tour[(i + 1) % tour.length];
            distance += matrix[a.getId()][b.getId()].getDistance();
            time += matrix[a.getId()][b.getId()].getTravelTime();
            if (b.isTimeCritical() && b.getDeadline() < time) {
                penalty += 100000 * time / b.getDeadline();
            }
        }
        double cost = distance + penalty;
        return cost;
    }

    /**
     * Berechnet die Anzahl der verpassten Deadlines auf einer Route
     *
     * @param route route
     * @return Anzahl verpasster Deadlines
     */
    protected int violatedDeadlines(List<Path> route) {
        double time = 0;
        int c = 0;
        for (Path p : route) {
            time += p.getTravelTime();
            if (p.getTo().isTimeCritical() && p.getTo().getDeadline() < time) {
                c++;
            }
        }
        return c;
    }


}
