package sphero.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RobotTest {

    public static final String pathChar = System.getProperty("file.separator");
    public final String pathToMapFolder = System.getProperty("user.dir") + pathChar + "resources" + pathChar + "inputs" + pathChar;
    public final String pathToTestFolder = System.getProperty("user.dir") + pathChar + "test" + pathChar + "sphero" + pathChar + "gui" + pathChar;

    private Map map;
    private Robot robot;

    @BeforeEach
    void setUp() {
        try {
            String pathToMap = pathToMapFolder + "input_01.txt";
            File currentFile = new File(pathToMap);
            map = new Map(currentFile);
            robot = new Robot(map);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        assertNotNull(map);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void moveUp() {
        try {
            Position positionOfRobot = map.getPositionOfRobot(); // 10x16
            assertEquals(10, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
            robot.moveUp(false);
            positionOfRobot = map.getPositionOfRobot(); // 8x16
            assertEquals(8, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void moveDown() {
        try {
            Position positionOfRobot = map.getPositionOfRobot(); // 10x16
            assertEquals(10, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
            robot.moveDown(false);
            positionOfRobot = map.getPositionOfRobot(); // 12x16
            assertEquals(12, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void moveLeft() {
        try {
            Position positionOfRobot = map.getPositionOfRobot(); // 10x16
            assertEquals(10, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
            robot.moveLeft(false);
            positionOfRobot = map.getPositionOfRobot(); // 10x14
            assertEquals(10, positionOfRobot.getX());
            assertEquals(14, positionOfRobot.getY());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void moveRight() {
        try {
            Position positionOfRobot = map.getPositionOfRobot(); // 10x16
            assertEquals(10, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
            robot.moveRight(false);
            positionOfRobot = map.getPositionOfRobot(); // 10x16
            assertEquals(10, positionOfRobot.getX());
            assertEquals(16, positionOfRobot.getY());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}