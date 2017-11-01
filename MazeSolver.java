import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;        

/**
 *  Class that runs a maze display/solution GUI
 *
 *  @author  Mulangma "Isabella" Zhu, Yixin (Vera) Bao
 *  @version CSC 212, 29 March 2017
 */
public class MazeSolver extends JApplet {
    /** Holds the maze to solve */
    private static Maze maze;

    /** The window */
    private static JFrame frame;

    /** Solve button */
    private static JButton solveButton;

    /** Reset button */
    private static JButton resetButton;

    /** A box for user to load a new maze */
    private static TextField loadMaze;

    /** Reset button */
    private static JButton okButton;

    public static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        frame = new JFrame("Maze Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components
        createComponents(frame.getContentPane());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void createComponents(Container pane) {
        pane.add(maze);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        solveButton = new JButton("Solve");
        solveButton.addActionListener(new SolveListener());
        panel.add(solveButton);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetListener());
        panel.add(resetButton);

        JPanel pane2 = new JPanel();
        pane2.setLayout(new FlowLayout());

        loadMaze = new TextField(30);
        loadMaze.addTextListener(new CustomTextListener());
        pane2.add(loadMaze);

        okButton = new JButton("OK");
        okButton.addActionListener(new OkListener());
        pane2.add(okButton);

        pane.add(panel,BorderLayout.SOUTH);
        pane.add(pane2,BorderLayout.NORTH);
    }

    // Application starts here
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.print("Please input the maze and run again.");
            System.exit(0);
        } else {
            maze = new Maze(args[0]);
        }
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    createAndShowGUI();
		}
	    });
    }

    // Applet starts here
    public void init() {
	//Execute a job on the event-dispatching thread:
	//creating this applet's GUI.
	try {
	    javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
		    public void run() {
			createComponents(getContentPane());
		    }
		});
	} catch (Exception e) {
	    System.err.println("createGUI didn't successfully complete");
	}
    }

    /** Event handler for Solve button */
    private static class SolveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            maze.reset();
            // call to solve should be on a new thread
            solveButton.setEnabled(false);
            resetButton.setEnabled(false);
            (new SolverThread()).execute();
        }
    }

    /** Event handler for Reset button */
    private static class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            maze.reset();
        }
        }

    /** Event handler for Text box */
    private static class CustomTextListener implements TextListener {
        public void textValueChanged(TextEvent e) { maze.reset(); }
    }

    /** Event handler for OK button */
    private static class OkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                BufferedReader fname = new BufferedReader(new FileReader(loadMaze.getText()));
                maze.readMaze(fname);
            } catch (IOException ignore) {
            }
            maze.revalidate();
            maze.repaint();
            maze.setMinimumSize(new Dimension(maze.getnCol()*50,maze.getnRow()*50));
            maze.setPreferredSize(new Dimension(maze.getnCol()*50,maze.getnRow()*50));
            frame.pack();
            frame.setVisible(true);
            loadMaze.setText("");
        }
    }

    /** Worker class for solving the maze */
    private static class SolverThread extends SwingWorker<Boolean, Object> {
       @Override
       public Boolean doInBackground() {
           return maze.solve();
       }

       @Override
       protected void done() {
           try {
	       if (!get()) {  // test the result of doInBackground()
		   System.out.println("Maze has no valid solution.");
	       }
	       solveButton.setEnabled(true);
	       resetButton.setEnabled(true);
           } catch (Exception e) {
	       e.printStackTrace();
           }
       }
   }
}
