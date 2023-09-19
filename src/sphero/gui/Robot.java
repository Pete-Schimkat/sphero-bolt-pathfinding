package sphero.gui;

import sphero.common.Constants;
import sphero.hw.sphero_connection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Robot extends JPanel implements ActionListener {

    private final Map map;

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    private int xPos;
    private int yPos;

    public Robot(Map map) throws IOException {
        this.map = map;
        this.xPos = map.getPositionOfRobot().getX();
        this.yPos = map.getPositionOfRobot().getY();
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }


    public void moveUp(boolean run) throws IOException {
        if (xPos - 2 >= 0) {
            // if neighboring position isn't a wall
            if (map.get(xPos - 1, yPos).getCharacter() != Constants.WALL) {
                map.set(new Position(xPos, yPos, Constants.FREE));
                // next Cell (+2 positions)
                xPos = xPos - 2;
                map.setPositionOfRobot(new Position(xPos, yPos, Constants.ROBOT));
                writeProtocol(new Position(xPos / 2, yPos / 2, Constants.ROBOT));
                if (run) {
                    sphero_connection.guiMovement(false, false, false, false);
                }
            }
        }
    }

    public void moveDown(boolean run) throws IOException {
        if (xPos + 2 < map.getOriginalSizeX()) {
            // if neighboring position isn't a wall
            if (map.get(xPos + 1, yPos).getCharacter() != Constants.WALL) {
                map.set(new Position(xPos, yPos, Constants.FREE));
                // next Cell (+2 positions)
                xPos = xPos + 2;
                map.setPositionOfRobot(new Position(xPos, yPos, Constants.ROBOT));
                writeProtocol(new Position(xPos / 2, yPos / 2, Constants.ROBOT));
                if (run) {
                    sphero_connection.guiMovement(false, false, true, false);
                }
            }
        }
    }

    public void moveLeft(boolean run) throws IOException {
        if (yPos - 2 >= 0) {
            // if neighboring position isn't a wall
            if (map.get(xPos, yPos - 1).getCharacter() != Constants.WALL) {
                map.set(new Position(xPos, yPos, Constants.FREE));
                // next Cell (+2 positions)
                yPos = yPos - 2;
                map.setPositionOfRobot(new Position(xPos, yPos, Constants.ROBOT));
                writeProtocol(new Position(xPos / 2, yPos / 2, Constants.ROBOT));
                if (run) {
                    sphero_connection.guiMovement(true, false,false, false);
                }
            }
        }
    }

    public void moveRight(boolean run) throws IOException {
        if (yPos + 2 < map.getOriginalSizeY()) {
            // if neighboring position isn't a wall
            if (map.get(xPos, yPos + 1).getCharacter() != Constants.WALL) {
                map.set(new Position(xPos, yPos, Constants.FREE));
                // next Cell (+2 positions)
                yPos = yPos + 2;
                map.setPositionOfRobot(new Position(xPos, yPos, Constants.ROBOT));
                writeProtocol(new Position(xPos / 2, yPos / 2, Constants.ROBOT));
                if (run) {
                    sphero_connection.guiMovement(false, true, false, false);
                }
            }
        }
    }

    public void action(boolean run) throws IOException {
        if (run) {
            sphero_connection.guiMovement(false, false, false, true);}
    }


        private void writeProtocol(Position position) {
        System.out.println(position);
    }
}	

