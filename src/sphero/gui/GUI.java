package sphero.gui;

import sphero.common.*;
import sphero.hw.sphero_connection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class GUI extends JFrame {

    public static String pathChar = System.getProperty("file.separator");

    public final static String pathToIconFolder = System.getProperty("user.dir") + pathChar + "resources" + pathChar + "icons" + pathChar;
    public final static String pathToMapFolder = System.getProperty("user.dir") + pathChar + "resources" + pathChar + "inputs" + pathChar;
    private File currentFile;

    //private ConfigurationBuilder configBuilder;

    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private JPanel controlPanel;

    /**
     * All the GUI Buttons
     */
    private JButton loadMapButton;
    private JButton connectSpheroboltButton;
    private JButton calculateWayButton;
    private JButton startSimulationButton;
    private JButton controlUp;
    private JButton controlDown;
    private JButton controlLeft;
    private JButton controlRight;
    private JButton resetButton;
    private JButton startButton;

    private Map map;

    private Robot robot;

    private Boolean pathIsCalculated;
    private Boolean isSpheroConnected;

    private List<Field> destinations = new LinkedList<>();


    public static void main(String[] args) throws IOException {
        new GUI("GUI - SPHERO BOLT");
    }


    /**
     * constructor
     */
    public GUI(String title) throws IOException {

        super(title);
        setComponentsName();

        resetAll();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(mainPanel);
        setPreferredSize(new Dimension(1500, 800));

        BoxLayout contentPanelLayout = new BoxLayout(contentPanel, BoxLayout.Y_AXIS);
        contentPanel.setLayout(contentPanelLayout);
        contentPanel.setBorder(BorderFactory.createTitledBorder("Karte"));

        // Show a map as default when loading the GUI
        String pathToMap = pathToMapFolder + "input_02.txt";
        currentFile = new File(pathToMap);

        map = new Map(currentFile);
        contentPanel.add(map);

        robot = new Robot(map);

        menuPanel.setBackground(Color.LIGHT_GRAY);

        controlPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 2;
        constraints.gridy = 0;

        controlPanel.add(controlUp, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;

        controlPanel.add(controlDown, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        controlPanel.add(controlLeft, constraints);

        constraints.gridx = 4;
        constraints.gridy = 1;

        controlPanel.add(controlRight, constraints);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);

        calculateWayButton.setMaximumSize(getSize());
        resetButton.setMaximumSize(getSize());
        startSimulationButton.setMaximumSize(getSize());
        startButton.setMaximumSize(getSize());


        settingActListenerForWindow();

        settingActListnerForControlButtons();

        settingActListenerForConnectSpheroboltButton();

        // load a new map afterwards
        settingActListenerForLoadMapButton();

        // Calls the algorithm from algoTeam and sets them in map.
        settingActListenerForCalculateWayButton();

        settingActListenerForResetButton();

        //Calculates the way if it hasn't been calculated yet
        //and moves the sphero on the map (so far only display,
        //not the real robot.)
        settingActListenerForSimulationButton();

        settingActListenerForStartButton();
        this.pack();
        setVisible(true);
    }

    /**
     * Setting ActionListener for Window
     */
    private void settingActListenerForWindow() {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // Close server and client
                if (isSpheroConnected) {
                    try {
                        sphero_connection.shutdown();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * Calls algorithm to calculate path and sets it in map
     */
    private void calculatePath(Map map) {
        try {
            ConfigurationBuilder builder = new ConfigurationBuilder(getCurrentFile());
            Configuration configuration = new Configuration(builder);

            List<Location> listDestinations = new LinkedList<>(configuration.getOrderedLocations());

            for (Location location : listDestinations) {
                destinations.add(location.getField());
            }

            //Get path from Algo
            List<Path> listOfPath = AlgorithmFactory.createInstance(configuration).calculate(configuration).getRoute();
            List<Field> pathForMap = new LinkedList<>();
            for (Path path : listOfPath) {
                pathForMap.addAll(path.getFields());
            }
            map.setPath(pathForMap);

            // Start Sphero should only be done when the shortest path is calculated.
            if (map.getPath().size() > 0) {
                startSimulationButton.setEnabled(true);
                pathIsCalculated = true;
            }

            activateStartBtn();

            repaint();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Setting ActionListener for Connect Spherobolt button
     */
    private void settingActListenerForConnectSpheroboltButton() {
        connectSpheroboltButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    sphero_connection.serverStart();
                    isSpheroConnected = true;
                    activateStartBtn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Setting ActionListener for Calculate Way  button
     */
    private void settingActListenerForCalculateWayButton() {
        calculateWayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePath(map);
            }
        });
    }

    /**
     * Setting ActionListener for Reset button
     */
    private void settingActListenerForResetButton() {
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentPanel.removeAll();
                map = new Map(getCurrentFile());
                if (map.getMap() != null) {
                    contentPanel.add(map);
                    try {
                        robot = new Robot(map);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                    destinations = new LinkedList<>();
                }
                // Update panel so that the Paint method can be called again
                contentPanel.updateUI();

                resetAll();

                System.out.println("Map is loaded..");

            }
        });
    }

    /**
     * Setting ActionListener for Load Map button
     */
    private void settingActListenerForLoadMapButton() {
        loadMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // customize JFileChooser to show German labels
                UIManager.put("FileChooser.lookInLabelText", "Suchen in:");
                UIManager.put("FileChooser.upFolderToolTipText", "Einen Ordner aufwärts in der Hierarchie");
                UIManager.put("FileChooser.newFolderToolTipText", "Neuen Ordner anlegen");
                UIManager.put("FileChooser.fileNameLabelText", "Dateiname:");
                UIManager.put("FileChooser.filesOfTypeLabelText", "Dateityp:");
                UIManager.put("FileChooser.cancelButtonText", "Abbrechen");
                UIManager.put("FileChooser.cancelButtonToolTipText ", "Aktion abbrechen");

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(pathToMapFolder));

                //  only text files can be loaded as map
                FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(".txt", "txt");
                fileChooser.setFileFilter(extensionFilter);

                fileChooser.setDialogTitle("Karte auswählen");

                int decision = fileChooser.showOpenDialog(null);
                if (decision == JFileChooser.APPROVE_OPTION) {

                    // Empty panel first, otherwise multiple cards will be created
                    contentPanel.removeAll();
                    map = new Map(fileChooser.getSelectedFile().getAbsoluteFile());
                    currentFile = fileChooser.getSelectedFile().getAbsoluteFile();
                    if (map.getMap() != null) {
                        contentPanel.add(map);
                        try {
                            robot = new Robot(map);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    resetAll();

                    // Update panel so that the Paint method can be called again
                    contentPanel.updateUI();

                    System.out.println("Map is loaded..");
                }
            }
        });
    }

    /**
     * Setting ActionListener for Simulation button
     */
    private void settingActListenerForSimulationButton() {
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulationButton.setEnabled(false);
                startButton.setEnabled(false);
                while (map.getPath().size() >= 1) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    List<Field> path = map.getPath();
                    System.out.println(path);
                    int vert = (robot.getXPos() / 2) - path.get(0).getX();
                    int horz = (robot.getYPos() / 2) - path.get(0).getY();

                    boolean activity = destinations.contains(path.get(0));

                    if (vert != 0) {
                        if (vert == 1) {
                            try {
                                robot.moveUp(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                robot.moveDown(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (horz != 0) {
                        if (horz == 1) {
                            try {
                                robot.moveLeft(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                robot.moveRight(false);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    RepaintManager.currentManager(contentPanel).paintDirtyRegions();
                    repaint();
                    // Aktivität im Einsatzort 5 Sec:
                    if (activity) {
                        if (path.size() > 0) {
                            destinations.remove(path.get(0));
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            }
        });
    }

    /**
     * Setting ActionListener for Start button
     */
    private void settingActListenerForStartButton() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sphero_connection.runClient();
                startButton.setEnabled(false);
                startSimulationButton.setEnabled(false);
                while (map.getPath().size() >= 1) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    List<Field> path = map.getPath();
                    System.out.println(path);
                    int vert = (robot.getXPos() / 2) - path.get(0).getX();
                    int horz = (robot.getYPos() / 2) - path.get(0).getY();

                    boolean activity = destinations.contains(path.get(0));

                    if (vert != 0) {
                        if (vert == 1) {
                            try {
                                robot.moveUp(true);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                robot.moveDown(true);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    if (horz != 0) {
                        if (horz == 1) {
                            try {
                                robot.moveLeft(true);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                robot.moveRight(true);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    RepaintManager.currentManager(contentPanel).paintDirtyRegions();
                    // Aktivität im Einsatzort 5 Sec:
                    if (activity) {
                        if (path.size() > 0) {
                            destinations.remove(path.get(0));
                            try {
                                robot.action(true);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            }
        });
    }

    /**
     * Setting ActionListener for Control buttons
     */
    private void settingActListnerForControlButtons() {
        controlUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                startSimulationButton.setEnabled(false);
                calculateWayButton.setEnabled(false);
                startButton.setEnabled(false);

                try {
                    robot.moveUp(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        controlDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                startSimulationButton.setEnabled(false);
                calculateWayButton.setEnabled(false);
                startButton.setEnabled(false);
                try {
                    robot.moveDown(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        controlRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                startSimulationButton.setEnabled(false);
                calculateWayButton.setEnabled(false);
                startButton.setEnabled(false);
                try {
                    robot.moveRight(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        controlLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                startSimulationButton.setEnabled(false);
                calculateWayButton.setEnabled(false);
                startButton.setEnabled(false);
                try {
                    robot.moveLeft(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public File getCurrentFile() {
        return currentFile;
    }


    /**
     * Setting names of components for ButtonsTest
     */
    private void setComponentsName() {
        loadMapButton.setName("loadMapButton");
        connectSpheroboltButton.setName("connectSpheroboltButton");
        calculateWayButton.setName("calculateWayButton");
        startSimulationButton.setName("startSimulationButton");
        controlUp.setName("controlUp");
        controlDown.setName("controlDown");
        controlLeft.setName("controlLeft");
        controlRight.setName("controlRight");
        resetButton.setName("resetButton");
        startButton.setName("startButton");
    }

    // Start button can be activated only when the shortest path is calculated and the GUI is connected to Sphero.
    private void activateStartBtn() {
        if (pathIsCalculated && isSpheroConnected) {
            startButton.setEnabled(true);
        }
    }

    private void resetAll() {
        pathIsCalculated = false;
        isSpheroConnected = false;

        // Start Sphero should only be done when the shortest path is calculated.
        startSimulationButton.setEnabled(false);
        startButton.setEnabled(false);

        calculateWayButton.setEnabled(true);
    }

}
