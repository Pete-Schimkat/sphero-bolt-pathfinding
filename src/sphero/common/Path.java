package sphero.common;

import java.util.List;

/**
 * Ein Pfad ist eine Verbindung zwischen zwei Orten auf der Karte
 * Ein Pfad besteht aus einer geordneten Liste aus Feldern
 */
public class Path implements Comparable<Path> {

    private final List<Field> fields;
    private final Location from;
    private final Location to;
    private final double cost;
    private final double distance;
    private double time;

    /**
     * Erstellt einen neuen Pfad
     *
     * @param fields   Alle Felder, aus denen der Pfad besteht, in der richtigen Reihenfolge
     * @param distance Distanz des Pfades
     * @param from     Startfeld
     * @param to       Zielfeld
     */
    public Path(List<Field> fields, Location from, Location to, double distance) {
        this.fields = fields;
        this.from = from;
        this.to = to;
        this.distance = distance;
        cost = distance;
        estimateTime();
    }

    /**
     * Liefert Liste von Feldern in der entsprechenden Reihenfolge
     * Vom Startfeld (exklusiv) zum Zielfeld (inklusiv)
     *
     * @return Liste der Felder
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @return Kosten des Pfades
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return Distanz des Pfades
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return Zeitaufwand des Pfades
     */
    public double getTravelTime() {
        return time;
    }

    /**
     * @return Start-Ort des Pfades
     */
    public Location getFrom() {
        return from;
    }

    /**
     * @return Ziel-Ort des Pfades
     */
    public Location getTo() {
        return to;
    }

    @Override
    public int compareTo(Path o) {
        return (int) (this.cost - o.cost);
    }


    /**
     * Provisorische Abschätzung der Weg-Zeit (nur zu Simulationszwecken)
     */
    private void estimateTime() {
        if(fields == null || fields.isEmpty()){
            time = 0;
            return;
        }
        double sum = 0;
        Field last = null;
        boolean flag = true;
        int x_vec = 0;
        int y_vec = 0;
        for (Field f : fields) {
            if (flag) {
                flag = false;
                last = f;
                continue;
            }
            int x_diff = Math.abs(f.getX() - last.getX());
            int y_diff = Math.abs(f.getY() - last.getY());
            if (x_vec == x_diff && y_vec == y_diff) {
                sum += 1;
            } else {
                sum += 5;
            }
        }
        time = sum + to.getTimeImpact();
    }


}
