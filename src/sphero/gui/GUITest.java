/*
package sphero.gui;

import org.junit.*;
import sphero.gui.GUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GUITest {

    @org.junit.jupiter.api.Test
    void testRunPython() {
        // Test if output is being produced/read correctly
        assertEquals("Hello World!", sphero.gui.GUI.runPython("src/sphero/gui/testHelloWorld.py"));

        // Test with arg
        assertEquals("success!", sphero.gui.GUI.runPython("src/sphero/gui/testRest.py", "-argTest"));

        // Test with longer output
        Path fileName = Path.of("src/sphero/gui/testLoremIpsum.txt");
        String lorem = null;
        try {
            lorem = Files.readString(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(lorem, sphero.gui.GUI.runPython("src/sphero/gui/testRest.py", "-loremTest"));

        // Test with multiple args
        assertEquals("success!\n" + lorem, sphero.gui.GUI.runPython("src/sphero/gui/testRest.py", "-argTest", "-loremTest"));
    }

    @org.junit.jupiter.api.Test
    void main() {
    }
}*/
