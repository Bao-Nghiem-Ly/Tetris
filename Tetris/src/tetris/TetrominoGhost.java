/*
 * Nghiem Ly
 * June 12, 2015
 * Simple replication of the famous tetris game 
 */

package tetris;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Class for the ghost of the tetrominoes. The class inherits the properties of the Tetromino class but is used differently
 * 
 * @author Nghiem
 * @version 1.0
 */
public class TetrominoGhost extends Tetromino{


	/**
	 * Constructor that creates the ghost piece
	 * 
	 * @param index number used to indicate what the ghost piece will be
	 * (precondition: shapeIndex must be a non decimal number from 0 to 6)
	 */
	public TetrominoGhost(int index) {
		super(index);//constructs a new ghost piece with the Tetromino class' properties 
	}

	/**
	 * Method that updates the ghost piece to be exactly where the Tetromino is. The ghost piece is then later dropped down in the Game class
	 * 
	 * @param parts the four squares making up the ghost piece
	 */
	public void updateGhost(ArrayList<Rectangle> parts){
		for(int a = 0; a < parts.size(); a++){
			shapeParts.get(a).setLocation(parts.get(a).getBounds().x, parts.get(a).getBounds().y);
		}
		
	}

}
