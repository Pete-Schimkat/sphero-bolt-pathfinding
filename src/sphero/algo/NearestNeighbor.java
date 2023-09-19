package sphero.algo;

import sphero.common.Destination;
import sphero.common.Field;
import sphero.common.Path;

import java.util.List;

public class NearestNeighbor {


    List<Destination> calculate(Path[][] distances, Field start, List<Destination> destinations) {
        /*TODO:
        1. Mithilfe von allPairShortestPaths.allPairs die kürzesten Pfade zwischen allen Zielen berechnen
        2. Beim Startknoten Anfangen
        3. Zum nächstgelegenen Knoten gehen der noch nicht besucht wurde (Kürzester ausgehender Pfad)
        4. Schritt von diesem Knoten aus Wiederholen
        5. Alle gewählten Pfade in der richtigen Reihenfolge zu einer Liste zusammenfügen
        6. Liste zurückgeben


        Rückgabe-Format von ALlPairShortestPaths:
        Path-Matrix (Path hat Attribut getCost() als Distanz); Start hat Index 0; Einsatzorte die folgenden
        Zeilen: "Von"
        Spalten: "Nach"
        Wie hier z.B.

                Start           Ort1                Ort2    ...

        Start   0               (Start->Ort1)       (Start->Ort2)

        Ort1   (Ort1->Start)     0                  (Ort1->Ort2)

        Ort2   (Ort2->Start)     (Ort2->Ort1)       0
        */
        return null;
    }
}
