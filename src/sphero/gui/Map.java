package sphero.gui;

import sphero.common.Constants;
import sphero.common.Field;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Map extends JPanel {

    public static final int MARGIN = 20; // margin between window and map
    private final static String SPLITTER = " ";
    File filenameMap;
    private Position[][] map;
    private Position robotPosition;
    private int sizeX = 0;
    private int sizeY = 0;
    private int numberOfDestinations;
    private List<Field> path;

    public Map(File filenameMap) {
        this.filenameMap = filenameMap;
        loadMap();
        printMap();
    }

    /**
     * Takes a step, removing first element of
     * path.
     */
    public void pathStep() {
        if (this.path.size() != 0) {
            this.path.remove(0);
        }
        repaint();
        validate();
    }

    public void popUpErrorWindow(String errorText){
        JOptionPane.showMessageDialog(this.getParent(), errorText);
    }

    /**
     * Invoked by Swing to draw components.
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        int CELL_WIDTH = 1000 / (sizeX * 2);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));

        //Scale map so it fits the screen
        double scale = Math.min((double) (this.getWidth()) / (CELL_WIDTH * sizeY + 2 * MARGIN), (double) this.getHeight() / (CELL_WIDTH * sizeX + 2 * MARGIN));
        scale = Math.max(scale, (double) 1 / 2);
        g2d.scale(scale, scale);


        if (map != null) {
            super.paint(g2d);

            g2d.drawRect(MARGIN, MARGIN, CELL_WIDTH * sizeY, CELL_WIDTH * sizeX);

            int xIndex = 0;
            int yIndex = 0;
            int rowCount = 0;

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {

                    char charFromMap = (map[i][j]).getCharacter();

                    if (charFromMap == Constants.FREE) {
                        yIndex++;
                        continue;
                    }

                    if (charFromMap == Constants.WALL) {
                        g2d.setColor(Color.black);

                        if (rowCount % 2 == 0) {
                            // vertical wall
                            g2d.drawLine(MARGIN + yIndex * CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH, MARGIN + yIndex * CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH + CELL_WIDTH);
                        } else {
                            // horizontal wall
                            g2d.drawLine(MARGIN + yIndex * CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH, MARGIN + yIndex * CELL_WIDTH + CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH);
                            yIndex++;
                        }
                        continue;
                    } else if (charFromMap == Constants.ROBOT) {
                        try {
                            //  Load icon for Sphero
                            BufferedImage sphero = ImageIO.read(new File(GUI.pathToIconFolder + GUI.pathChar + "sphero.png"));
                            g2d.drawImage(sphero, MARGIN + yIndex * CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH, CELL_WIDTH, CELL_WIDTH, null);
                            yIndex++;
                            continue;
                        } catch (IOException e) {
                            System.out.println("Icon sphero.png not found!");
                        }
                    } else if (charFromMap == Constants.GOALS) {
                        try {
                            // Load icon for fire
                            BufferedImage flame = ImageIO.read(new File(GUI.pathToIconFolder + GUI.pathChar + "flame.png"));
                            g2d.drawImage(flame, MARGIN + yIndex * CELL_WIDTH, MARGIN + xIndex * CELL_WIDTH, CELL_WIDTH, CELL_WIDTH, null);
                            yIndex++;
                            continue;
                        } catch (IOException e) {
                            System.out.println("Icon flame.png not found!");
                        }

                    }

                    if (rowCount % 2 == 1 && charFromMap == Constants.TRANSITION) {
                        yIndex++;
                    }
                }

                yIndex = 0;
                if (rowCount % 2 == 0) xIndex++;
                rowCount++;
            }

            // Draw paths
            int pathSize;
            if (this.path != null) {
                pathSize = this.path.size();

            } else pathSize = 0;
            g2d.setColor(Color.red);
            for (int i = 0; i < pathSize - 1; i++) {
                Field fieldCurrent = this.path.get(i);
                Field fieldNext = this.path.get(i + 1);
                int yCurrent = fieldCurrent.getX();
                int xCurrent = fieldCurrent.getY();
                int yNext = fieldNext.getX();
                int xNext = fieldNext.getY();
                g2d.drawLine(MARGIN + xCurrent * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + yCurrent * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + xNext * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + yNext * CELL_WIDTH + CELL_WIDTH / 2);
            }
            if (pathSize >= 1) {
                g2d.setColor(Color.green);
                g2d.drawLine(MARGIN + robotPosition.getY() / 2 * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + robotPosition.getX() / 2 * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + this.path.get(0).getY() * CELL_WIDTH + CELL_WIDTH / 2, MARGIN + this.path.get(0).getX() * CELL_WIDTH + CELL_WIDTH / 2);
            }
        }
    }

    /**
     * Map is output to the console
     */
    public void printMap() {
        if (map != null) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    System.out.print((map[i][j]).getCharacter());
                    System.out.print(" ");
                }
                System.out.println();
            }
        }
    }

    public void printMap2() {
        if (map != null) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    System.out.println("map[" + i + "][" + j + "]   " + (map[i][j]).getCharacter());
                }
                System.out.println();
            }
        }
    }

    public Position getPositionOfRobot() {
        return robotPosition;
    }

    public void setPositionOfRobot(Position position) {
        map[position.getX()][position.getY()] = position;
        robotPosition = position;
        if (this.path != null && this.path.size() >= 1) {
            if (this.path.get(0).getX() * 2 == position.getX() && this.path.get(0).getY() * 2 == position.getY())
                pathStep();
        }
        repaint();
    }

    public Position[][] getMap() {
        return map;
    }

    public Position get(int x, int y) {
        return map[x][y];
    }

    public void set(Position position) {
        map[position.getX()][position.getY()] = position;
        repaint();
    }

    /**
     * load the map and create a two dimensional array of it
     */
    public void loadMap() {
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(filenameMap)));

            try {
                if (scanner.hasNextLine()) sizeX = Integer.parseInt(scanner.nextLine().trim());
                if (scanner.hasNextLine()) sizeY = Integer.parseInt(scanner.nextLine().trim());
            } catch(Exception e) {
                throw new NumberFormatException(e.getMessage());
            }

            // Initialize map array
            map = new Position[sizeX * 2 - 1][sizeY * 2 - 1];

            int xIndex = 0;
            int yIndex = 0;
            int rowCount = 0;


            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split(SPLITTER);

                if (rowCount == 0) {
                    rowCount++;
                    continue;
                }

                // N° of lines is at the end, regarding
                // the given sizeX
                if (rowCount == sizeX * 2) {
                    if(scanner.hasNextLine()) {
                        popUpErrorWindow("Die Kartedatei hat mehr Zeilen als eingegeben.");
                        throw new RuntimeException("Extra lines in input file.");
                    }
                    break;
                }

                // Row is odd <=> the line has both fields and field edges
                if (rowCount % 2 == 1) {
                    if (line.length > 2 * sizeY + 1){
                        popUpErrorWindow("Die Zeile " + sizeX + " " + rowCount + " hat mehr Felder als eingegeben.");
                        throw new RuntimeException("Extra column in input file");
                    }
                    for (int j = 1; j < line.length - 1; j++) {
                        map[xIndex][yIndex] = new Position(xIndex, yIndex, line[j].charAt(0));
                        //Tests if the characters are all those defined in Constants.java
                        switch (j % 2) {
                            //is a wall/transition
                            case 0:
                                switch ((map[xIndex][yIndex]).getCharacter()) {
                                    case Constants.WALL:
                                    case Constants.TRANSITION:
                                        break;
                                    default:
                                        popUpErrorWindow("Die Karte ist nicht richtig formatiert. Stelle sicher, dass zwischen allen Wänden und Wegen ein Feld steht, und dass sie richtig gekennzeichnet sind.");
                                        throw new RuntimeException("Field edge isn't wall or transition symbol");
                                }
                                break;
                            //is a field
                            case 1:
                                switch ((map[xIndex][yIndex]).getCharacter()) {
                                    case Constants.GOALS:
                                        numberOfDestinations++;
                                        break;
                                    case Constants.ROBOT:
                                        robotPosition = map[xIndex][yIndex];
                                        break;
                                    case Constants.FREE:
                                        break;
                                    default:
                                        popUpErrorWindow( "Die Karte ist nicht richtig formatiert. Stelle sicher, dass zwischen allen Feldern eine Wand oder ein Weg steht, und dass sie richtig gekennzeichnet sind.");
                                        throw new RuntimeException("Field isn't robot, goal or free space.");
                                }
                                break;
                        }
                        yIndex++;
                    }
                    rowCount++;
                    xIndex++;
                    yIndex = 0;
                // Row is even <=> the line has only field edges
                } else if (rowCount % 2 == 0) {
                    for (int j = 1; j < line.length - 1; j++) {
                        if (line[j].equals("")) {
                            map[xIndex][yIndex] = new Position(xIndex, yIndex, ' ');
                            j++;
                        } else {
                            switch (line[j].charAt(0)){
                                case Constants.WALL:
                                case Constants.TRANSITION:
                                    map[xIndex][yIndex] = new Position(xIndex, yIndex, line[j].charAt(0));
                                    break;
                                default:
                                    popUpErrorWindow( "Die Karte ist nicht richtig formatiert. Stelle sicher, dass zwischen allen Feldern eine Wand oder ein Weg steht, und dass sie richtig gekennzeichnet sind.");
                                    throw new RuntimeException("Field edge isn't wall or transition symbol");
                            }
                        }
                        yIndex ++;
                    }
                    rowCount++;
                    xIndex++;
                    yIndex = 0;
                }

            }

            System.out.println("Map size is: " + sizeX + "x" + sizeY);
            System.out.println("Number of Destinations is: " + numberOfDestinations);

            // Position positionOfRoboter = getPositionOfRoboter();
            System.out.println("Position of Robot: " + robotPosition.getX() + "x" + robotPosition.getY());


        } catch (FileNotFoundException e) {
            System.out.println("File " + filenameMap.getAbsolutePath() + " could not be read.");
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getOriginalSizeX() {
        return sizeX * 2 - 1;
    }

    public int getOriginalSizeY() {
        return sizeY * 2 - 1;
    }

    public List<Field> getPath() {
        return path;
    }

    /**
     * Sets paths calculated by the algorithm
     * calculated by the AlgoTeam and paints
     * the way on the map
     */
    public void setPath(List<Field> path) {
        this.path = path;
        repaint();
    }
}
