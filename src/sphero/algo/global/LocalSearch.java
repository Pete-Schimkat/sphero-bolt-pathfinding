package sphero.algo.global;

import sphero.algo.local.AllPairShortestPaths;
import sphero.common.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Heuristische Suche nach einer möglichst optimalen Route
 * Startet mit einer NearestNeighbor- oder Zufallslösung und verbessert diese so oft wie möglich
 * Kann den besten Wert aus beliebig vielen Iterationen mit jeweils neuen Zufallslösungen auswählen
 *
 * @see sphero.common.RoutingAlgorithm
 */
public class LocalSearch implements RoutingAlgorithm {


    protected final AllPairShortestPaths allPairShortestPaths;
    protected final int iterations;

    /**
     * Zu iterations: Höhere Anzahl an Durchläufen reduziert die Streuung, erhöht aber die Rechenzeit
     *
     * @param allPairShortestPaths Dependency-Injection einer AllPairShortestPaths-Instanz
     * @param iterations           Anzahl an Zufalls-Durchläufen
     */
    public LocalSearch(AllPairShortestPaths allPairShortestPaths, int iterations) {
        this.allPairShortestPaths = allPairShortestPaths;
        this.iterations = iterations;
    }

    /**
     * Führt iterations viele Durchläufe des Algorithmus aus
     * Die Last wird auf alle CPU-Kerne verteilt
     *
     * @see sphero.common.RoutingAlgorithm
     */
    @Override
    public RoutingResult calculate(Configuration config) {
        Path[][] distances = allPairShortestPaths.allPairs(config.getOrderedLocations(), config.getMap());
        if(distances == null) return new RoutingResult("Mindestens ein Ort auf der Karte ist unerreichbar.");
        Solution s = runParallelized(config, distances);
        List<Path> route = toRoute(distances, s.best_tour);
        return new RoutingResult(route, s.best);
    }

    /**
     * Hilfs-Methode für calculate() zur parallelen Ausführung
     *
     * @return beste gefundene Lösung
     */
    protected Solution runParallelized(Configuration config, Path[][] distances) {
        Solution s = new Solution();
        try {
            int threads = Runtime.getRuntime().availableProcessors();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            CountDownLatch cdl = new CountDownLatch(iterations);
            for (int i = 0; i < iterations; i++) {
                //pool.submit(() -> {
                    Location[] tour = randomOrder(config);
                    minimize(distances, tour);
                    double cost = cost(distances, tour);
                    s.update(tour, cost);
                    cdl.countDown();
                //});
            }
            cdl.await();
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Hilfsklasse für thread-sichere Aktualisierung der bisher besten Lösung
     */
    protected static class Solution {
        Location[] best_tour = null; //beste gefundene Lösung
        double best = Double.MAX_VALUE; //deren Kosten

        /**
         * Aktuallisiert die beste Lösung, falls die neue Lösung besser ist
         */
        synchronized void update(Location[] tour, double cost) {
            if (cost < best) {
                best_tour = tour;
                best = cost;
            }
        }
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
    protected double minimize(Path[][] distances, Location[] tour) {
        int i = 0;
        double res = 0;
        double diff = 1;
        for (int j = 0; j < 10 && diff != 0; j++) {
            while (diff != 0 && i < 100) {
                i++;
                diff = improve_twoOpt(distances, tour);
                res -= diff;
            }
            i = 0;
            diff = improve_relocate(distances, tour);
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
    protected double improve_twoOpt(Path[][] distances, Location[] tour) {
        double diff = 0;

        for (int i = 0; i < tour.length - 1; i++) { //Kante a
            Location a_from = tour[i];
            Location a_to = tour[i + 1];
            if(a_from == null ||a_to == null) break;
            for (int j = i + 1; j < tour.length; j++) { //Kante b
                Location b_from = tour[j];
                Location b_to = tour[(j + 1) % tour.length];
                if(b_from == null ||b_to == null) break;
                double dist = distances[a_from.getId()][a_to.getId()].getCost() + distances[b_from.getId()][b_to.getId()].getCost();
                double dist_alternative = distances[a_from.getId()][b_from.getId()].getCost() + distances[a_to.getId()][b_to.getId()].getCost();
                if (dist > dist_alternative) { //2-Opt Verbesserung gefunden
                    a_to = b_from;
                    diff = dist - dist_alternative;
                    reverse(tour, i + 1, j);
                }
            }
        }
        return diff;
    }

    /**
     * Kehrt die Reihenfolge des Subarrays um
     *
     * @param tour Ziel-Array
     * @param i    Start des Subarrays (inklusiv)
     * @param j    Ende des Subarrays (inklusiv)
     */
    protected void reverse(Location[] tour, int i, int j) {
        while (i < j) {
            Location temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;
            i++;
            j--;
        }
    }

    /**
     * Führt die Relocate-Heuristik auf einer gegebenen Tour aus
     *
     * @param distances Path-Matrix von AllPairShortestPaths
     * @param tour      zu verbessernde Tour
     * @return Größe der Verbesserung durch Relocate
     */
    protected double improve_relocate(Path[][] distances, Location[] tour) {
        double diff = 0;
        for (int i = 0; i < tour.length; i++) { //Betrachteter Knoten
            Location a_l = i == 0 ? tour[tour.length - 1] : tour[i - 1];
            Location m = tour[i];
            Location a_r = i == tour.length - 1 ? tour[0] : tour[i + 1];

            double dist_a = distances[a_l.getId()][m.getId()].getCost() + distances[m.getId()][a_r.getId()].getCost();
            double dist_a_alt = distances[a_l.getId()][a_r.getId()].getCost();

            double best_delta = Double.MIN_VALUE;
            int best_j = -1;
            for (int j = 0; j < tour.length - 1; j++) { //Verbindung, auf der der Knoten eingefügt werden kann
                if (Math.abs(i - j) <= 1) continue;
                Location b_l = tour[j];
                Location b_r = tour[j + 1];

                double dist_b = distances[b_l.getId()][b_r.getId()].getCost();
                double dist_b_alt = distances[b_l.getId()][m.getId()].getCost() + distances[m.getId()][b_r.getId()].getCost();
                double delta = (dist_a + dist_b) - (dist_a_alt + dist_b_alt);
                if (delta > 0 && delta > best_delta) { //Verbesserung gefunden
                    best_delta = delta;
                    best_j = j;
                }
            }
            if (best_j != -1) {
                diff -= best_delta;
                insert(tour, i, best_j + 1);
            }
        }
        return diff;
    }

    /**
     * Fügt eine Location aus dem Array an einer anderen Stelle ein
     * Teile des Arrays werden dabei verschoben
     *
     * @param tour Ziel-Array
     * @param from vorheriger Index der Location
     * @param to   neue Index
     */
    protected void insert(Location[] tour, int from, int to) {
        if (from < to) {
            Location temp = tour[from];
            System.arraycopy(tour, from + 1, tour, from, to - from);
            tour[to] = temp;
        } else {
            Location temp = tour[from];
            System.arraycopy(tour, to, tour, to + 1, from - to);
            tour[to] = temp;
        }
    }

    /**
     * Berechnet die Kosten einer Route
     *
     * @param route Route
     * @return Kosten
     */
    protected double cost(List<Path> route) {
        double sum = 0;
        for (Path p : route) {
            sum += p.getCost();
        }
        return sum;
    }

    /**
     * Berechnet die Kosten einer Tour
     *
     * @param matrix Path-Matrix von AllPairShortestPaths
     * @param tour   Tour
     * @return Kosten
     */
    protected double cost(Path[][] matrix, Location[] tour) {
        double sum = 0;
        for (int i = 0; i < tour.length - 1; i++) {
            Location a = tour[i];
            Location b = tour[i + 1];
            sum += matrix[a.getId()][b.getId()].getCost();
        }
        sum += matrix[tour[tour.length - 1].getId()][tour[0].getId()].getCost();
        return sum;
    }


    /**
     * Erstellt eine zufällige Tour
     *
     * @param configuration Routing-Szenario für die Tour
     * @return zufällige Tour als Array
     */
    protected static Location[] randomOrder(Configuration configuration) {
        List<Location> copy = new ArrayList<>(configuration.getOrderedLocations());
        Collections.shuffle(copy);
        Location[] arr = new Location[copy.size()];
        copy.toArray(arr);
       /* for (int i = 0; i < arr.length; i++) {
            if(arr[i].getType() == Place.Type.START){
                Place start = arr[i];
                arr[i] = arr[0];
                arr[0] = start; //TODO remove later
            }
        } */
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
        if(tour.length < 2) return new ArrayList<>();
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


}
