package visualizer;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    protected static final int WIDTH = 800;
    protected static final int HEIGHT = 600;
    protected static final Color BACKGROUND_COLOR = Color.black;
    private JLabel modeLabel;
    private Graph graphPanel;
    private static final String PROMPT = "Please choose a starting vertex";
    private static final JTextArea resultsArea = new JTextArea(5, 40);
    private static final JScrollPane resultsPane = new JScrollPane(resultsArea);
    protected static Mode mode = Mode.ADD_A_VERTEX;
    protected static Algorithm algorithm = null;
    
    static {
        resultsArea.setName("Display");
        resultsArea.setEditable(false);
        resultsArea.setLineWrap(true);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultsArea.setMargin(new Insets(6, 10, 6, 10));
        resultsArea.setText(PROMPT);
        resultsPane.setVisible(false);
    }

    public MainFrame() {
        super("Graph-Algorithms Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
        setLayout(new BorderLayout());
        setResizable(false);

        setModeJLabel();
        this.add(resultsPane, BorderLayout.SOUTH);
        setJMenu();

        add(this.graphPanel = new Graph(), BorderLayout.CENTER);

        setVisible(true);

    }

    private void setJMenu() {
        // Creating the menuBar
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // Creating the menu "File"
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Creating the menu "Mode"
        JMenu modeMenu = new JMenu("Mode");
        menuBar.add(modeMenu);

        // Creating the menu "Algorithms"
        JMenu algorithmsMenu = new JMenu("Algorithms");
        menuBar.add(algorithmsMenu);
        
        // Create the menu items for "Mode" in the order they should appear
        Mode[] menuOrder = {Mode.ADD_A_VERTEX, Mode.ADD_AN_EDGE,
            Mode.REMOVE_A_VERTEX, Mode.REMOVE_AN_EDGE, Mode.NONE};
        for (Mode m : menuOrder) {
            JMenuItem item = new JMenuItem(m.getDescription());
            item.setName(m.getDescription());
            item.addActionListener(e -> setMode(m));
            modeMenu.add(item);
        }

        // Creating the two menu items for "File"
        JMenuItem newReset = new JMenuItem("New");
        newReset.setName("New");
        JMenuItem exit = new JMenuItem("Exit");
        exit.setName("Exit");

        fileMenu.add(newReset);
        fileMenu.add(exit);

        // Creating the four menu items for "Algorithms"
        JMenuItem DFS = new JMenuItem("Depth-First Search");
        DFS.setName("Depth-First Search");
        JMenuItem BFS = new JMenuItem("Breadth-First Search");
        BFS.setName("Breadth-First Search");
        JMenuItem Dijkstras = new JMenuItem("Dijkstra's Algorithm");
        Dijkstras.setName("Dijkstra's Algorithm");
        JMenuItem Prims = new JMenuItem("Prim's Algorithm");
        Prims.setName("Prim's Algorithm");

        algorithmsMenu.add(DFS);
        algorithmsMenu.add(BFS);
        algorithmsMenu.add(Dijkstras);
        algorithmsMenu.add(Prims);

        // Add event listeners to the two menu items of "File"
        newReset.addActionListener(e -> {
            this.remove(this.graphPanel);
            this.repaint();
            this.revalidate();
            this.add(this.graphPanel = new Graph());
        // Clear all the static containers
        Edge.edges.clear();
        Vertex.vertices.clear();
        Graph.availableEdges.clear();
        setMode(Mode.ADD_A_VERTEX);
        });
        
        exit.addActionListener(e -> {
            this.dispose();
        });

        // Add event listeners to the four menu items of "Algorithms"
        DFS.addActionListener(e -> selectAlgorithm(Algorithm.DFS));
        BFS.addActionListener(e -> selectAlgorithm(Algorithm.BFS));
        Dijkstras.addActionListener(e -> selectAlgorithm(Algorithm.DIJKSTRAS));
        Prims.addActionListener(e -> selectAlgorithm(Algorithm.PRIMS));
    }

    private void changeTextForModeLabel(String newText) {
        this.modeLabel.setText(newText);
        modeLabel.setSize(modeLabel.getPreferredSize());
        this.modeLabel.setLocation(MainFrame.WIDTH - modeLabel.getWidth() - 20, 0);
    }
    private void setMode(Mode newMode) {
        mode = newMode;
        changeTextForModeLabel("Current Mode -> " + mode.getDescription());
        Graph.edgeVertices.clear();
        resultsPane.setVisible(false);
        resultsArea.setText(PROMPT);
    }
    
    private void selectAlgorithm(Algorithm chosen) {
        setMode(Mode.NONE);
        resultsPane.setVisible(true);
        algorithm = chosen;
    }

    private void setModeJLabel() {
        modeLabel = new JLabel();
        this.add(modeLabel, BorderLayout.NORTH);
        modeLabel.setName("Mode");
        modeLabel.setText("Current Mode -> Add a Vertex");
        modeLabel.setOpaque(true);
        modeLabel.setForeground(Vertex.VERTEX_COLOR);
        modeLabel.setBackground(MainFrame.BACKGROUND_COLOR);
        modeLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        modeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    }

    public static boolean isResultsVisible() {
        return resultsPane.isVisible();
    }
    
    public static void setResultsText(String text) {
        resultsArea.setText(text);
        resultsArea.setCaretPosition(0);   // scroll back to the top
    }
    
    protected static String formatResult(Algorithm alg, Vertex start, String raw) {
        String id = start.getId();
        return switch (alg) {
            case BFS -> "Breadth-first visit order from " + id + ":\n"
                + raw.replaceFirst("^BFS : ", "");
            case DFS -> "Depth-first visit order from " + id + ":\n"
                + raw.replaceFirst("^DFS : ", "");
            case DIJKSTRAS -> "Shortest distances from " + id + ":\n"
                + raw.replace(", ", "\n").replace("=", " = ")
                     .replace("Infinity", "unreachable");
            case PRIMS -> raw.contains("=")
                ? "Minimum spanning tree from " + id + " (child = parent):\n"
                    + raw.replace(", ", "\n").replace("=", " = ")
                : "Vertex " + id + " has no edges, so the tree is just " + raw;
        };
    }   
    
}
