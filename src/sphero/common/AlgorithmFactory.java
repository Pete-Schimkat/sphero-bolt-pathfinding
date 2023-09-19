package sphero.common;

import sphero.algo.global.LocalSearch;
import sphero.algo.local.AllPairShortestPaths;

public class AlgorithmFactory {

    /**
     * Erstellt eine neue Instanz einer RoutingAlgorithm-Implementation
     *
     * @param config Karten-Konfiguration zur Auswahl des passenden Algorithmus
     * @return Algoriothmus-Instanz
     */
    public static RoutingAlgorithm createInstance(Configuration config) {
        return new LocalSearch(new AllPairShortestPaths(), 30);
    }
}
