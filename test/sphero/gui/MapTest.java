package sphero.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    public static String pathChar = System.getProperty("file.separator");
    public final String pathToMapFolder = System.getProperty("user.dir") + pathChar + "resources" + pathChar + "inputs" + pathChar;
    public final String pathToTestFolder = System.getProperty("user.dir") + pathChar + "test" + pathChar + "sphero" + pathChar + "gui" + pathChar;

    private Map map;

    //MapNoPopUp is just map but with no windows so the test can be automated
    public static class MapNoPopup extends Map {
        public MapNoPopup(File filenameMap) {
            super(filenameMap);
        }

        @Override
        public void popUpErrorWindow(String errorText) {
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        String pathToMap = pathToMapFolder + "input_02.txt";
        File currentFile = new File(pathToMap);
        map = new Map(currentFile);
        assertNotNull(map);
    }


    @Test
    void loadMap() throws IOException {
        map.filenameMap = new File(pathToMapFolder + "input_05.txt");
        System.out.println("x is " + map.getX());
        map.loadMap();
        assertNotNull(map);
        assertNotNull(map.getMap());
        assertNotNull(map.getPositionOfRobot());
        assertEquals(11, map.getSizeX());
        assertEquals(13, map.getSizeY());
        assertEquals(21, map.getOriginalSizeX());
        assertEquals(25, map.getOriginalSizeY());
        assertEquals('E', map.get(2, 0).getCharacter());

        //testEingabe1 tests if it throws an error when what is written
        //in the first/second line is not a number.
        MapNoPopup mapNoPopup = new MapNoPopup(map.filenameMap);
        mapNoPopup.filenameMap = new File(pathToTestFolder + "testEingabe1");
        assertThrows(NumberFormatException.class, () -> mapNoPopup.loadMap());

        //testEingabe2 tests if it throws an error if a field has a character
        //that's not the robot, a goal or a free field.
        mapNoPopup.filenameMap = new File(pathToTestFolder + "testEingabe2");
        assertThrows(RuntimeException.class, () -> mapNoPopup.loadMap());

        //testEingabe3 tests if it throws an error if a field edge has a
        //character that's not a wall or transition
        mapNoPopup.filenameMap = new File(pathToTestFolder + "testEingabe3");
        assertThrows(RuntimeException.class, () -> mapNoPopup.loadMap());

        //testEingabe4 tests if it throws an error if the file has extra lines
        //than what was written in the first two lines
        mapNoPopup.filenameMap = new File(pathToTestFolder + "testEingabe4");
        assertThrows(RuntimeException.class, () -> mapNoPopup.loadMap());

        //testEingabe4 tests if it throws an error if the file has extra columns
        //than what was written in the first two lines
        mapNoPopup.filenameMap = new File(pathToTestFolder + "testEingabe5");
        assertThrows(RuntimeException.class, () -> mapNoPopup.loadMap());
    }

    @Test
    void getPathNull() {
        assertTrue(map.getPath() == null);
    }

}