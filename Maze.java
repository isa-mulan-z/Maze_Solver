import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 *  The class of a maze
 *
 *  @author  Mulangma "Isabella" Zhu, Yixin (Vera) Bao
 *  @version CSC 212, 29 March 2017
 */
public class Maze extends JComponent {
    /** The number of rows */
    private int nRow;

    /** The number of columns */
    private int nCol;

    /** A 2D array that stores the contents of a maze */
    private MazeContents[][] mazeArray;

    /** The start state of the maze */
    private MazeCoordinate start;

    /** The finish state of the maze */
    private MazeCoordinate finish;


    /** a boolean to show if the maze is able to be solved */
    private boolean exitReachable;

    /** Set Display for the maze */
    private Dimension WINDOW_SIZE;

    /** A LinkedList that stores the coordinates of the path */
    private LinkedList<MazeCoordinate> pathWay = new LinkedList<>();


    /**
     * The Constructor for maze,
     * takes arguments specifying the name of an input file
     *
     * @param fname Specifies the name of an input file
     */
    public Maze(String fname) throws IOException {
        BufferedReader file = null;
        file = new BufferedReader(new FileReader(fname));
        readMaze(file);
    }

    /**
     * The Constructor for maze,
     * reads from the system input
     */
    public Maze() throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
        readMaze(file);
    }

    /**
     * A method of reading the file into a maze
     */
    public void readMaze(BufferedReader file) throws IOException {
        // Read lines
        ArrayList<String> lines = new ArrayList<>();
        String thisLine;
        while ((thisLine = file.readLine()) != null) {
            lines.add(thisLine);
        }
        // Set the nRow, nCol and the size of the window
        nRow = lines.size();
        nCol = lines.get(0).length();
        WINDOW_SIZE = new Dimension(nCol * 50, nRow * 50);
        // Set up the 2D array of maze contents
        mazeArray = new MazeContents[nRow][nCol];
        for (int i = 0; i < nRow; i++) {
            thisLine = lines.get(i);
            for (int j = 0; j < nCol; j++) {
                switch (thisLine.charAt(j)) {
                    case '#':
                        mazeArray[i][j] = MazeContents.WALL;
                        break;
                    case '.':
                        mazeArray[i][j] = MazeContents.OPEN;
                        break;
                    case 'S':
                        mazeArray[i][j] = MazeContents.OPEN;
                        start = new MazeCoordinate(i, j);
                        break;
                    case 'F':
                        mazeArray[i][j] = MazeContents.OPEN;
                        finish = new MazeCoordinate(i, j);
                        break;
                }
            }
        }
    }

    /**
     * The Accessor of the number of rows
     *
     * @return nRow the number of rows
     */
    public int getnRow() {
        return nRow;
    }

    /**
     * The Accessor of the number of rows
     *
     * @return nCol the number of columns
     */
    public int getnCol() {
        return nCol;
    }

    /**
     * A method that solves the maze
     *
     * @return exitReachable A boolean to show if the maze is able to be solved
     */
    public boolean solve() {
        solving(start);
        System.out.println("The path is:");
        for (MazeCoordinate coordinate: pathWay){
            System.out.println("("+coordinate.getRow()+","+coordinate.getCol()+")");
        }
        return exitReachable;
    }

    /**
     * A recursive worker method,
     * steps forward and back to find the path, and paint those steps
     *
     * @return exitReachable A boolean to show if the maze is able to be solved
     */
    public boolean solving(MazeCoordinate current) {
        if (current.equals(finish)) {
            mazeArray[current.getRow()][current.getCol()] = MazeContents.PATH;
            pathWay.addFirst(current);
            exitReachable = true;
        } else if (isOpen(current)) {
            mazeArray[current.getRow()][current.getCol()] = MazeContents.VISITED;
            if (!exitReachable) {
                exitReachable = solving(current.neighbor(MazeDirection.NORTH))
                        || solving(current.neighbor(MazeDirection.EAST))
                        || solving(current.neighbor(MazeDirection.SOUTH))
                        || solving(current.neighbor(MazeDirection.WEST));
            }
            // If have reached the finish, mark as path and record the coordinate
            if (exitReachable) {
                mazeArray[current.getRow()][current.getCol()] = MazeContents.PATH;
                pathWay.addFirst(current);
            // If all four directions are not open, mark as dead end
            } else if (inMaze(current)) {
                mazeArray[current.getRow()][current.getCol()] = MazeContents.DEAD_END;
            }
        } else {
            exitReachable = false;
        }
        // Paint out each step
        repaint();
        try {
            Thread.sleep(30);
        } catch (InterruptedException ignore) {
        }

        return exitReachable;
        }

    /**
     * Test if the grid is open or not
     *
     * @return A boolean to show if the grid is open not
     */
    public boolean isOpen(MazeCoordinate neighbor) {
        if (inMaze(neighbor) && mazeArray[neighbor.getRow()][neighbor.getCol()].isTraversible()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Test if the grid is in the maze or not
     *
     * @return A boolean to show if the grid is in maze or not
     */
    public boolean inMaze(MazeCoordinate neighbor) {
        if (neighbor.getRow()<nRow && neighbor.getCol()<nCol) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *
     * Reset the maze
     */
    public void reset() {
        exitReachable = false;
        pathWay.clear();
        for (int i = 0; i < nRow; i ++) {
           for ( int j = 0; j < nCol; j ++) {
               if (mazeArray[i][j]!= MazeContents.WALL) {
                   mazeArray[i][j] = MazeContents.OPEN;
               }
           }
        }
        repaint();
    }

    /**
     * get minimum size
     *
     * @return WINDOW_SIZE the size of the window
     */
    public Dimension getMinimumSize(){
        return WINDOW_SIZE;
    }

    /**
     * get preferred size
     *
     * @return WINDOW_SIZE the size of the window
     */
    public Dimension getPreferredSize(){
        return WINDOW_SIZE;
    }



    /**
     *  Draws the maze in the graphics window
     *
     *  @param g The graphics object to draw into
     */
    public void paintComponent(Graphics g) {
        // Paint every grid, the length of the side equals to 50
        for (int i = 0; i <nRow; i++) {
            for (int j = 0; j < nCol; j++) {
                Color c = mazeArray[i][j].getColor();
                g.setColor(c);
                g.fillRect(50*j,50*i,50,50);
                super.paintComponent(g);
            }
        }
        // Mark the start and finish of the maze
        g.setColor(Color.red);
        g.drawString("S",50*start.getCol()+10,50*start.getRow()+15);
        g.drawString("F",50*finish.getCol()+35,50*finish.getRow()+40);

    }
}
