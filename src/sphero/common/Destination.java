package sphero.common;

/**
 * Repräsentiert einen Einsatzort auf der Karte
 */
@Deprecated
public class Destination {
    private final int id;
    private final Field field;

    /**
     * Erstellt einen neuen Einsatzort (Ziel)
     *
     * @param field Feld, auf dem sich das Ziel befindet
     * @param id    ID des Ziels (erstmal optional I guess)
     */
    public Destination(Field field, int id) {
        //assert field != null;
        //assert id >= 0;
        this.field = field;
        this.id = id;
    }

    /**
     * @return Feld, auf dem sich das Ziel befindet
     */
    public Field getField() {
        return field;
    }

    /**
     * @return ID des Ziels
     */
    public int getId() {
        return id;
    }
}
