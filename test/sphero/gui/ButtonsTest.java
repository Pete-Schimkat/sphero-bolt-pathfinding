package sphero.gui;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ButtonsTest {
    FrameFixture window;

    @BeforeEach
    void setUp() {
        GUI gui = GuiActionRunner.execute(() -> new GUI("Test GUI"));
        window = new FrameFixture(gui);
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void loadMapButtonTest() {
        JButtonFixture loadButton = window.button("loadMapButton");
        loadButton.requireText("Karte laden");
        loadButton.requireVisible();
        loadButton.requireEnabled();
        loadButton.click();

    }

    @Test
    void connectSpheroboltButtonTest() {
        JButtonFixture connectSpheroboltButton = window.button("connectSpheroboltButton");
        connectSpheroboltButton.requireText("Sphero Bolt verbinden");
        connectSpheroboltButton.requireVisible();
        connectSpheroboltButton.requireEnabled();
    }

    @Test
    void calculateWayButtonTest() {
        JButtonFixture calculateWayButton = window.button("calculateWayButton");
        calculateWayButton.requireText("Weg berechnen");
        calculateWayButton.requireVisible();
        calculateWayButton.requireEnabled();
    }

    @Test
    void startSimulationButtonTest() {
        JButtonFixture startSimulationButton = window.button("startSimulationButton");
        startSimulationButton.requireText("Start Simulation");
        startSimulationButton.requireVisible();
        startSimulationButton.requireDisabled();
    }

    @Test
    void startButtonTest() {
        JButtonFixture startButton = window.button("startButton");
        startButton.requireText("Start");
        startButton.requireVisible();
        startButton.requireDisabled();
    }

    @Test
    void resetButtonTest() {
        JButtonFixture resetButton = window.button("resetButton");
        resetButton.requireText("Zurücksetzen");
        resetButton.requireVisible();
        resetButton.requireEnabled();
    }
}