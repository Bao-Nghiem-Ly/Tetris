/*
 * Nghiem Ly
 * June 12, 2015
 * Simple replication of the famous tetris game 
 */

package tetris;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * This class is used as the Tetrominoes. It contains a list of coordinates for every piece for the game but each Tetromino object has its own index to get the coordinates to construct thaT shape
 * 
 * @author Nghiem
 * @version 1.0
 */
public class Tetromino{

	protected ArrayList<Rectangle> shapeParts = new ArrayList<Rectangle>();
	protected static final int[][][] coordinates = new int[][][]{ 
		{ {0, -1}, {1, -1}, {0, 0}, {1, 0} }, //square
		{ {-1, 0}, {0, 0}, {1, 0}, {2, 0} },//line
		{ {-1, 0}, {0, 0}, {0, -1}, {1, -1} },//s
		{ {-1, -1}, {0, -1}, {0, 0}, {1, 0} },//z
		{ {-1, 0}, {0, 0}, {1, 0}, {1, -1} },//L
		{ {-1, -1}, {-1, 0}, {0, 0}, {1, 0} },//J
		{ {0, -1}, {-1, 0}, {0, 0}, {1, 0} }//T
	};
	protected int[][] tempCoord = new int[4][2];
	protected int index;
	protected int square = Game.gridSquare;
	protected final int middle = 6 * square + square * 8;
	protected int[][] tempMove;
		
	/**
	 * Constructor for the Tetromino object
	 * 
	 * @param shapeIndex The index to be used to determine what shape the object will be
	 * (precondition: shapeIndex must be a non decimal number from 0 to 6)
	 */
	public Tetromino(int shapeIndex){
		this.index = shapeIndex;

		for(int a = 0; a < 4; a++){
			tempCoord[a][0] = coordinates[index][a][0];
			tempCoord[a][1] = coordinates[index][a][1];
			Rectangle temp = new Rectangle();
			temp.setSize(square, square);
			shapeParts.add(temp);
		}	
		resetShape();
	}

	/**
	 * Method that resets the tetromino to its original starting location
	 */
	public void resetShape(){
		for(int a = 0; a < 4; a++){
			tempCoord[a][0] = coordinates[index][a][0];
			tempCoord[a][1] = coordinates[index][a][1];
			shapeParts.get(a).setLocation(middle + coordinates[index][a][0] * square, coordinates[index][a][1] * square);
		}
	}
	
	/**
	 * Method that sets the tetromino into the right place for the preview
	 * 
	 * @param lblBoxMiddle center of the preview box
	 * @param yLocation the y coordinate that is about the center of each preview box
	 */
	public void lblLocation(int lblBoxMiddle, int yLocation){
		for(int a = 0; a < 4; a++){
			shapeParts.get(a).setLocation(lblBoxMiddle + coordinates[index][a][0] * square, coordinates[index][a][1] * square + yLocation);
		}
	}
	
	/**
	 * Method that rotates the tetrominoes
	 * 
	 * @param game Game class, used to gain access to some methods in the class
	 * (precondition: game must be a proper Game object that inherits from JPanel and must not be null)
	 */
	public void rotate(Game game){

		int canMove = 0;
		int[][] tempCoord = new int[4][2];//temp coord to transfer over if the piece is able to rotate
		tempMove = new int[4][2];//amount that each part needs to be translated by

		//check if rotation is possible
		for(int a = 0; a < 4; a++){
			int oldX = this.tempCoord[a][0];
			int oldY = this.tempCoord[a][1];
			int newX = -oldY;
			int newY = oldX;

			int moveX = (newX - oldX)* square;
			int moveY = (newY - oldY) * square;

			if(game.tryMoveSingle(moveX + shapeParts.get(a).getBounds().x, moveY + shapeParts.get(a).getBounds().y) ){
				tempCoord[a][0] = newX;
				tempCoord[a][1] = newY;
				tempMove[a][0] = moveX;
				tempMove[a][1] = moveY;
				canMove++;	
			}

		}

		//if possible, rotate tetromino
		if(canMove == 4){
			for(int a = 0; a < 4; a++){
				this.tempCoord[a][0] = tempCoord[a][0];
				this.tempCoord[a][1] = tempCoord[a][1];
				shapeParts.get(a).translate(tempMove[a][0], tempMove[a][1]);
			}
		}
	}
	
	/**
	 * Method that translates the tetromino
	 * 
	 * @param newX Amount to be moved left or right
	 * @param newY Amount to be moved up or down
	 */
	public void move(int newX, int newY){
		int moveX = newX * square;
		int moveY = newY * square;

		for(Rectangle a: shapeParts){
			a.translate(moveX, moveY);
		}
	}
	
	/**
	 * get method for shapeParts
	 * 
	 * @return shapeParts
	 */
	public ArrayList<Rectangle> getShapeParts(){
		return shapeParts;
	}

	/**
	 * get method for index
	 * 
	 * @return index
	 */
	public int getIndex(){
		return this.index;
	}

	/**
	 * get method for temporary coordinates
	 * 
	 * @return tempCoord
	 */
	public int[][] getTempCoord(){
		return this.tempCoord;
	}
	
	/**
	 * get method for temporary movement array
	 * 
	 * @return tempMove
	 */
	public int[][] getMove(){
		return this.tempMove;
	}
}
