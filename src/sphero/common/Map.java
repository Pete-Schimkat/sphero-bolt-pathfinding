package sphero.common;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private final List<Field> allFields;

    private final int sizeX;
    private final int sizeY;
    private final double scalingFactor;
    //private final Field[][] grid;
    private final Field[] fieldsById;

    /**
     * Erstellt eine neue Karte
     *
     * @param sizeX         Anzahl Felder auf X-Achse
     * @param sizeY         Anzahl Felder auf Y-Achse
     * @param scalingFactor Skalierung der Felder(eine Positionseinheit entspricht so vielen Längeneinheiten in der Realität)
     */
    public Map(int sizeX, int sizeY, double scalingFactor) {
        assert sizeX > 0;
        assert sizeY > 0;
        assert scalingFactor > 0;
        allFields = new ArrayList<>();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        //this.grid = new Field[sizeY][sizeX];
        this.scalingFactor = scalingFactor;
        this.fieldsById = new Field[sizeX * sizeY];
        createFields();
    }

    /**
     * Erstellt eine neue Karrte
     * Zu Beginn existieren keine toten Felder, diese müssen noch entfernt werden
     *
     * @param sizeX         Anzahl Felder auf X-Achse
     * @param sizeY         Anzahl Felder auf Y-Achse
     * @param scalingFactor Skalierung der Felder(eine Positionseinheit entspricht so vielen Längeneinheiten in der Realität)
     * @param noEdges       Auf True setzen, falls bei der Erstellung der Map ein Graph ohne Kanten ersellt werden soll
     */
    public Map(int sizeX, int sizeY, double scalingFactor, boolean noEdges) {
        assert sizeX > 0;
        assert sizeY > 0;
        assert scalingFactor > 0;
        allFields = new ArrayList<>();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        //this.grid = new Field[sizeY][sizeX];
        this.scalingFactor = scalingFactor;
        this.fieldsById = new Field[sizeX * sizeY];
        createFields();
        if (!noEdges) connectFields();
    }

    /**
     * Erstellt ein Gitter aus Field-Objekten
     */
    private void createFields() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Field field = new Field(coordinatesToId(x, y), x, y);
                fieldsById[field.getId()] = field;
                //grid[y][x] = field;
                allFields.add(field);
            }
        }
    }

    /**
     * Verbindet alle Felder mit ihren Nachbarn
     */
    private void connectFields() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Field f = getField(x, y);
                connect(f, getField(x + 1, y));
                connect(f, getField(x - 1, y));
                connect(f, getField(x, y + 1));
                connect(f, getField(x, y - 1));
            }
        }
    }

    /**
     * Verbindet zwei Felder
     *
     * @param a ein Feld
     * @param b ein anderes Feld
     */
    public void connect(Field a, Field b) {
        if (a == null || b == null) return;
        a.addEdge(b);
        b.addEdge(a);
    }

    /**
     * Entfernt die Verbindung von zwei Feldern
     *
     * @param a ein Feld
     * @param b ein anderes Feld, das mit a verbunden ist
     */
    public void disconnect(Field a, Field b) {
        a.removeEdge(b);
        b.removeEdge(a);
    }

    /**
     * Gibt Feld an dieser Position zurück
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return Feld an Position (x,y), null falls Koordinaten außerhalb der Map liegen
     */
    public Field getField(int x, int y) {
        /*if(x < 0 || y < 0 || x >= sizeX || y >= sizeY)
            return null;
        return grid[y][x]; */
        return getField(coordinatesToId(x, y));
    }

    /**
     * Gibt Feld mit dieser ID zurück
     *
     * @param id Feld-ID
     * @return Feld mit der gegebenen ID, null falls ID außerhalb des zulässigen Bereich liegt
     */
    public Field getField(int id) {
        if (id < 0 || id >= sizeX * sizeY) {
            return null;
        }
        return fieldsById[id];
    }

    /**
     * Liste aller Felder
     *
     * @return Liste der Felder
     */
    public List<Field> getAllFields() {
        return allFields;
    }

    /**
     * @return Anzahl Felder auf X-Achse
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * @return Anzahl Felder auf Y-Achse
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * @return Skalierung der Felder(eine Positionseinheit entspricht so vielen Längeneinheiten in der Realität)
     */
    public double getScalingFactor() {
        return scalingFactor;
    }

    /**
     * Wandelt Koordinaten zur entsprechenden ID um
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return Feld-ID
     */
    private int coordinatesToId(int x, int y) {
        return x * (sizeY) + y;
    }
}
