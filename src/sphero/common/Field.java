package sphero.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert ein einzelnes Feld im Gitter
 */
public class Field {
    private final int id;
    private final int x;
    private final int y;
    private final List<Field> neighbors;

    /**
     * @param id ID des Feldes
     * @param x  X-Koordinate
     * @param y  Y-Koordinate
     */
    public Field(int id, int x, int y) {
        assert id >= 0;
        assert x >= 0;
        assert y >= 0;
        neighbors = new ArrayList<>();
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * @return ID des Feldes
     */
    public int getId() {
        return id;
    }

    /**
     * @return X-Koordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return Y-Koordinate
     */
    public int getY() {
        return y;
    }

    /**
     * @return Erreichbare Nachbar-Felder
     */
    public List<Field> getNeighbors() {
        return neighbors;
    }

    /**
     * Erstellt Verbindung zu einem Nachbar-Feld
     *
     * @param b Nachbar-Feld
     */
    protected void addEdge(Field b) {
        assert b != null;
        if (!neighbors.contains(b))
            neighbors.add(b);
    }

    /**
     * Entfernt Verbindung zu einem Nachbar-Feld
     *
     * @param b Nachbar-Feld
     */
    protected void removeEdge(Field b) {
        neighbors.remove(b);
    }

    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return id == field.id && x == field.x && y == field.y;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
