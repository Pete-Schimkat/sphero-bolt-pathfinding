package sphero.common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * ConfigurationBuilder bietet Methoden zum Aufbau einer Routing-Konfiguration an
 * Kann entweder über eine Eingabe-Datei, oder manuell konfiguriert werden
 * <p>
 * Beispiel für manuelle Konfiguration:
 * Configuration c = new ConfigurationBuilder().withMap(...).withStart(...).withDestinations(...).create();
 */
public class ConfigurationBuilder {

    private Map map;
    private Location start;
    private List<Location> locationList = new ArrayList<>();

    public ConfigurationBuilder() {

    }

    /**
     * @param file Input-Datei mit den Konfigurations-Daten
     * @deprecated dieser Konstruktor wird später entfert. Aufruf ersetzen durch: (new ConfigurationBuilder).fromFile(file)
     */
    @Deprecated
    public ConfigurationBuilder(File file) throws IOException {
        fromFile(file);
    }

    /**
     * Überträgt die Konfigurations-Datei in die internen Datensturkturen
     *
     * @param file Konfigurations-Datei
     * @return this
     * @throws IOException Fehler beim Laden der Datei
     */
    public ConfigurationBuilder fromFile(File file) throws IOException {
        if (file == null) throw new IllegalArgumentException();

        FileInputStream fileStream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fileStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(file.getAbsoluteFile())));

        // Bestimmung der Größe der Karte
        int sizeX = 0;
        int sizeY = 0;
        if (scanner.hasNextLine()) sizeX = Integer.parseInt(scanner.nextLine().trim());
        if (scanner.hasNextLine()) sizeY = Integer.parseInt(scanner.nextLine().trim());
        map = new Map(sizeX, sizeY, 1.0);

        int currentPosX = -1;
        int currentPosY = 0;
        int txtRow = 0;

        String strLine;
        while ((strLine = br.readLine()) != null) {
            //skip upper and lower frames
            System.out.println(strLine);
            if (txtRow == 0) {
                txtRow++;
                continue;
            }

            //Line with positions and horizontal transitions.
            if (txtRow % 2 == 1) {
                for (int j = 1; j < strLine.length() - 1; j++) {
                    if (strLine.charAt(j) == Constants.GOALS) {
                        Field dest = map.getField(currentPosX, currentPosY);
                        locationList.add(new Location(dest, Location.Type.DESTINATION));
                    } else if (strLine.charAt(j) == Constants.ROBOT) {
                        Field f = map.getField(currentPosX, currentPosY);
                        start = new Location(f, Location.Type.START);
                        locationList.add(start);
                    } else if (strLine.charAt(j) == Constants.TRANSITION) {
                        Field a = map.getField(currentPosX, currentPosY - 1);
                        Field b = map.getField(currentPosX, currentPosY);
                        map.connect(a, b);
                    }
                    if (strLine.charAt(j) == Constants.FREE || strLine.charAt(j) == Constants.ROBOT || strLine.charAt(j) == Constants.GOALS) {
                        currentPosY++;
                    }
                }
                currentPosX++;
                currentPosY = 0;
            }

            // Line with vertical transitions
            else if (txtRow % 2 == 0) {
                for (int j = 1; j < strLine.length() - 1; j++) {
                    if (strLine.charAt(j) == Constants.TRANSITION) {
                        Field a = map.getField(currentPosX, currentPosY);
                        Field b = map.getField(currentPosX - 1, currentPosY);
                        map.connect(a, b);
                    }
                    if (strLine.charAt(j) == Constants.WALL || strLine.charAt(j) == Constants.TRANSITION) {
                        currentPosY++;
                    }
                }
                currentPosY = 0;
            }
            txtRow++;
        }
        in.close();
        return this;
    }


    /**
     * Fügt eine Map zur Konfiguration hinzu.
     *
     * @param map - die Karte
     * @return - this
     */
    public ConfigurationBuilder withMap(Map map) {
        this.map = map;
        return this;
    }

    /**
     * Fügt eine Start-Position zur Konfiguration hinzu.
     *
     * @param start - die Karte
     * @return - this
     */
    public ConfigurationBuilder withStart(Location start) {
        this.start = start;
        return this;
    }

    /**
     * Fügt eine eine Liste an Orten zur Konfiguration hinzu.
     *
     * @param locationList - die Lise der Orte
     * @return - this
     */
    public ConfigurationBuilder withLocations(List<Location> locationList) {
        this.locationList = locationList;
        return this;
    }

    /**
     * @return Karte auf der sich der Sphero bewegen soll
     */
    public Map getMap() {
        return map;
    }

    /**
     * @return Start-Position des Spheros
     */
    public Location getStart() {
        return start;
    }

    /**
     * @return Liste der Orte auf der Karte
     */
    public List<Location> getLocations() {
        return locationList;
    }

    /**
     * Erstellt eine Configuration-Instanz basierend auf den Informationen des Builders
     * @throws IllegalStateException falls Eingabe fehlerhaft/unvollständig
     * @return - die erstelle Configuration-Instanz
     */
    public Configuration create() {
        if(map == null || start == null || locationList == null || locationList.isEmpty())
            throw new IllegalStateException("Konfiguration kann nicht erstellt werden: Daten fehlerhaft oder unvollständig");
        return new Configuration(this);
    }

    /**
     * !! Diese Methode nur zu Testzwecken nutzen
     * Sie umgeht die Prüfung der Parameter bei create()
     *
     * @param force Umgehung der Parameter erzwingen
     * @return eine Configuration
     */
    public Configuration create(boolean force) {
        if (!force) return create();
        else return new Configuration(this);
    }
}
