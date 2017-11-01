import java.awt.*;

/**
 *  Maze Contents represents the status of a square in a maze
 *
 *  @author  Nicholas R. Howe
 *  @version CSC 112, 20 March 2006
 */
public enum MazeContents {
    WALL (false,Color.black),
    OPEN (true,Color.white),
    VISITED (false,new Color(200,255,200)),
    DEAD_END (false,new Color(255,200,200)),
    PATH (true,Color.green.darker());

    /** Can we visit this square? */
    private boolean isTraversible;

    /** How to display the square */
    private Color color;

    /** Constructor */
    MazeContents(boolean isTraversible, Color color) {
	this.isTraversible = isTraversible;
	this.color = color;
    }

    /** Accessor for isTraversible */
    public boolean isTraversible() {
	return isTraversible;
    }

    /** Accessor for color */
    public Color getColor() {
	return color;
    }
}
