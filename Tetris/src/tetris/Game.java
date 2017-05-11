/*
 * Nghiem Ly
 * June 12, 2015
 * Simple replication of the famous tetris game 
 */

package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *This class is where the everything in the game is controlled, this class draws the shape, label, board, and basically everything that is visible in the frame
 *This class controls the movements of the tetrominoes, the rotation, and the other controls
 *This class also uses the other two classes to generate the shapes and the ghost piece that shows the path of the current tetromino
 *The structure/idea for how this program works originated from http://zetcode.com/tutorials/javagamestutorial/tetris/
 *
 *@version 1.0
 *@author Nghiem
 */
@SuppressWarnings("serial")
public class Game extends JPanel implements KeyListener, ActionListener{

	public static final int gridSquare = 20;//used for scaling in the majority of the program
	private static final int boardX = gridSquare * 7;
	private static final int boardY = 0;
	private static Rectangle board = new Rectangle(boardX, boardY, gridSquare * 16, gridSquare * 29);
	private Tetromino currentPiece;
	private ArrayList<Rectangle> shapeParts;
	private Timer timer;
	private Random rand = new Random();
	private ArrayList<Tetromino> placedPieces;
	private boolean isPaused;
	private Tetromino holdPiece;
	private LinkedList<Tetromino> shapePreview;
	private int score;
	private boolean canSwitch;
	private TetrominoGhost ghostPiece;
	private String message;

	/**
	 * This method is the constructor of the Game class
	 * The constructor only adds a keylistener to the jframe then proceeds to initialize everything
	 * 
	 * @param tetris The class with the main and also acts as the jframe
	 * (precondition: tetris must be a class that inherits from JFrame)
	 */
	public Game(Tetris tetris){
		tetris.addKeyListener(this);
		timer = new Timer(600, this);
		start();
	}

	public void start(){
		placedPieces = new ArrayList<Tetromino>();
		shapePreview = new LinkedList<Tetromino>();
		
		//new tetromino
		int index = rand.nextInt(7);
		currentPiece = new Tetromino(index);
		ghostPiece = new TetrominoGhost(index);
		ghostDrop();

		//linked list that stores 4 tetrominoes, current piece is removed from the front and a new piece is added at the end of the list
		for(int a = 0; a < 4; a++){
			index = rand.nextInt(7);
			Tetromino temp = new Tetromino(index);
			shapePreview.addLast(temp);
		}
		
		Tetris.gameLabel.setVisible(false);
		shapeParts = currentPiece.getShapeParts();
		holdPiece = null;
		score = 0;
		canSwitch = true;
		message = "";
		isPaused = false;

		timer.start();
	}

	/**
	 * This method draws everything that is visible on screen except for the tetrominoes and its ghost
	 * 
	 * @param g2d Used to generate new graphics
	 */
	public void drawBoard(Graphics2D g2d){

		//draw everything that is not the tetromino and its ghost
		g2d.setColor(Color.LIGHT_GRAY); 
		g2d.fill(board);
		//draw all rectangles
		g2d.fillRect(0, 40, gridSquare * 6, gridSquare * 6);
		g2d.fillRect(480, 0, gridSquare * 6, gridSquare * 26);
		g2d.fillRect(0, gridSquare * 10, gridSquare * 6, 80);
		g2d.fillRect(0, gridSquare * 16, gridSquare * 6, 255);
		g2d.fillRect(0, 0, gridSquare * 6, 40);
		//set fonts and stuff
		g2d.setFont(new Font("Times", Font.BOLD, 18));
		g2d.setColor(Color.BLACK);
		//draw labels
		g2d.drawString("Hold", 35, 25);
		g2d.drawString("Preview", 485 + gridSquare, 25);
		g2d.drawString("Score:", 30, 230);
		//draw score
		g2d.drawString(Integer.toString(score), 30, 260);
		//draw controls
		g2d.drawString("^ - Rotate", 5, 350);
		g2d.drawString("v - Down", 5, 380);
		g2d.drawString("> - Right", 5, 410);
		g2d.drawString("< - Left", 5, 440);
		g2d.drawString("Space - Drop", 5, 470);
		g2d.drawString("Shift - Hold", 5, 500);
		g2d.drawString("P - Pause", 5, 530);
		g2d.drawString("R - Reset", 5, 560);

		//draw preview separation line
		g2d.drawLine(0, 40, gridSquare * 6, 40);
		for(int c = 40; c < gridSquare * 24; c+=gridSquare * 6){
			g2d.drawLine(480, c, 600, c);
		}

	}

	/**
	 * paintComponent method that is overridden to draw everything that appears on screen
	 */
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		drawBoard(g2d);
		drawShape(g2d);

		if(isPaused){
			Tetris.gameLabel.setText(message);
		}
	}  

	/**
	 * Another method called by paintComponents to draw the tetrominoes and its ghost
	 * 
	 * @param g2d used to draw components
	 */
	private void drawShape(Graphics2D g2d) {

		//draws the tetromino and colors it according to its shape and as well as drawing the ghost
		Color[] shapeColor = new Color[] {new Color(255,215,0), new Color(0,191,255), new Color(178,34,34), new Color(0,128,0), new Color(210,105,30), Color.BLUE, new Color(186,85,211) };
		int yCounter = 6 * gridSquare;

		//draw placed pieces
		if(placedPieces.size() > 0){
			for(Tetromino b: placedPieces){
				for(Rectangle c: b.getShapeParts()){
					g2d.setColor(shapeColor[b.getIndex()]);
					g2d.fill(c);
					g2d.setColor(Color.WHITE);
					g2d.draw(c);
				}
			}
		}

		//draw current piece
		for(Rectangle a: shapeParts){
			g2d.setColor(shapeColor[currentPiece.getIndex()]);
			g2d.fill(a);
			g2d.setColor(Color.WHITE);
			g2d.draw(a);
		}

		//draw piece that is being held
		if(holdPiece != null){
			for(Rectangle b: holdPiece.getShapeParts()){
				g2d.setColor(shapeColor[holdPiece.getIndex()]);
				g2d.fill(b);
				g2d.setColor(Color.WHITE);
				g2d.draw(b);
			}
		}

		//draw preview pieces
		for(int c = 0; c < shapePreview.size(); c++){
			shapePreview.get(c).lblLocation(480 + gridSquare * 2, 5 * gridSquare + yCounter * c);
			for(Rectangle d: shapePreview.get(c).getShapeParts()){
				g2d.setColor(shapeColor[shapePreview.get(c).getIndex()]);
				g2d.fill(d);
				g2d.setColor(Color.WHITE);
				g2d.draw(d);
			}
			shapePreview.get(c).resetShape();
		}

		//draw ghost piece
		for(Rectangle e: ghostPiece.getShapeParts()){
			g2d.setColor(Color.BLACK);
			g2d.draw(e);
		}
	}

	/**
	 * Overridden keyPressed method to allow keyboard input to control the tetrominoes
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		if(e.getKeyCode() == KeyEvent.VK_P){//puase/unpause
			pause();
		}

		if(e.getKeyCode() == KeyEvent.VK_R){
			start();
		}

		if(!isPaused){
			if(e.getKeyCode() == KeyEvent.VK_UP){//rotate
				if(currentPiece.getIndex() != 0){
					currentPiece.rotate(this);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN){//move down
				if(tryMove(0,1,currentPiece)){
					currentPiece.move(0,1);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){//move right
				if(tryMove(1,0,currentPiece)){
					currentPiece.move(1,0);
				}	
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT){//move left
				if(tryMove(-1,0,currentPiece)){
					currentPiece.move(-1,0);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_SPACE){//drop current piece down
				drop();
			}else if(e.getKeyCode() == KeyEvent.VK_SHIFT){//holds piece
				if(canSwitch)
					hold();
			}
			ghostPiece.updateGhost(currentPiece.getShapeParts());
			ghostDrop();
			repaint(); //method that repaints everything on the panel to essentially update the screen
		}
	}

	/**
	 * This method is used to store a tetris piece that can be later used to replace the current piece
	 */
	private void hold() {
		if(holdPiece == null ){//holds current piece and creates new piece if there is currently no pieces being held
			holdPiece = currentPiece;
			newPiece("");
		}else{//switches the held piece and the current piece
			Tetromino temp = currentPiece;
			currentPiece = holdPiece;
			ghostPiece = new TetrominoGhost(currentPiece.getIndex());
			ghostDrop();
			currentPiece.resetShape();
			holdPiece = temp;
			shapeParts = currentPiece.getShapeParts();
			canSwitch = false;
		}

		holdPiece.lblLocation(gridSquare * 2, 5 * gridSquare);
		repaint(); //method that repaints everything on the panel to essentially update the screen
	}

	/**
	 * Pause method, pauses game and displays indicator stating that game is paused
	 */
	private void pause() {
		if(isPaused){//unpause
			timer.start();
			isPaused = false;
			Tetris.gameLabel.setVisible(false);
			repaint(); //method that repaints everything on the panel to essentially update the screen
			fallDown();
		}else{//pause
			timer.stop();
			isPaused = true;
			Tetris.gameLabel.setVisible(true);
			message = "Paused";
			repaint(); //method that repaints everything on the panel to essentially update the screen
		}
	}

	/**
	 * Method that drops the tetromino's ghost to the lowest point on the board possible, meaning that it will move the ghost until the ghost collides with something else
	 */
	private void ghostDrop(){
		boolean done = false;
		while(!done){//drop down 1 line until not possible anymore
			if(!tryMove(0,1,ghostPiece)){
				done = true;//stop dropping
			}else{
				ghostPiece.move(0,1);
			}
		}
	}

	/**
	 * Same as the ghostDrop() method but is used for the tetromino and also calls other methods to update the game
	 */
	private void drop() {
		boolean done = false;
		while(!done){//drop down 1 line until not possible anymore
			if(!tryMove(0,1,currentPiece)){
				done = true;//stop dropping
			}else{
				currentPiece.move(0,1);
			}
		}
		newPiece("new"); //calls method to generate new tetromino
		clearLine(); //calls method that clears any full lines
		repaint(); //method that repaints everything on the panel to essentially update the screen
		canSwitch = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {}//does nothing
	@Override
	public void keyTyped(KeyEvent e) {}//does nothing

	/**
	 * Method that checks whether or not the place that the user wants to move a single square of the tetromino is possible. returns true if move is possible, false if not
	 * 
	 * @param newX movement in x-axis
	 * @param newY movement in y-axis
	 * @return boolean true if move is possible, false if move is not possible
	 */
	public boolean tryMoveSingle(int newX, int newY){

		//checks for collision with sides
		if(newY + gridSquare > board.height || newX + gridSquare > board.width + boardX || newX < boardX){
			return false;
		}

		//checks for collisions with other pieces
		for(Tetromino b: placedPieces){
			for(Rectangle c: b.getShapeParts()){
				if(newX == c.getBounds().x && newY == c.getBounds().y){
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Method that checks whether or not the place that the user wants to move the tetromino is possible. returns true if move is possible, false if not
	 * 
	 * @param newX movement in x-axis
	 * @param newY movement in y-axis
	 * @param shape the piece that needs to be tested
	 * (precondition: shape must be a proper Tetromino and must not be null)
	 * @return boolean true if move is possible, false if move is not possible
	 */
	private boolean tryMove(int newX, int newY, Tetromino shape){

		for(int a = 0; a < 4; a++){

			int x = shape.getShapeParts().get(a).getBounds().x + gridSquare * newX; 
			int y = shape.getShapeParts().get(a).getBounds().y + gridSquare * newY; 

			//checks for collision with sides
			if(y + gridSquare > board.height || x + gridSquare > board.getWidth() + boardX || x < boardX){
				return false;
			}

			//checks for collisions with other pieces
			for(Tetromino b: placedPieces){
				for(Rectangle c: b.getShapeParts()){
					if(x == c.getBounds().x && y == c.getBounds().y){
						return false;
					}
				}
			}

		}
		return true;//can move
	}

	/**
	 * Method that gets the highest y-coordinate that a piece of tetromino has been placed
	 * 
	 * @return highestY The highest y-value that a tetromino has reached
	 */
	private int highestY(){
		int highestY = gridSquare * 29;

		//replaces highest y value each time a higher one is found
		for(Tetromino a: placedPieces){
			for(Rectangle b: a.getShapeParts()){
				if(b.getBounds().y < highestY){
					highestY = b.getBounds().y;
				}
			}
		}

		return highestY;
	}

	/**
	 * Method that moves the tetromino down 1 square at certain intervals and if the piece can't move down anymore, it will generate a new piece or end the game
	 */
	private void fallDown() {

		if(tryMove(0,1,currentPiece)){
			currentPiece.move(0,1);
			repaint(); //method that repaints everything on the panel to essentially update the screen
		}else{
			if(gameDone()){//game is done
				timer.stop();
				isPaused = true;
				Tetris.gameLabel.setVisible(true);
				message = "Game Over";
			}else{
				newPiece("new"); //calls method to generate new tetromino
				clearLine(); //calls method that clears any full lines
			}
			repaint(); //method that repaints everything on the panel to essentially update the screen
		}

	}

	/**
	 * Method that checks if the game is done by checking if the current piece is able to move down 
	 * 
	 * @return boolean true if game is done and false if not
	 */
	private boolean gameDone() {
		//checks if current piece cannot move down anymore
		for(Rectangle a: currentPiece.getShapeParts()){
			if(a.getBounds().y <= 0){
				return true;
			}
		}
		return false;
	}

	/**
	 * method that calls the fallDown method at certain intervals
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		fallDown();
	}

	/**
	 * Method that clears any line that is filled by checking it from top to bottom, removing them in order and moving everything above that line down 1 square until every filled line is removed
	 */
	private void clearLine() {
		int blocksFull = 0;//16 is full
		ArrayList<Integer> fullLines = new ArrayList<Integer>();
		int highest = highestY()/gridSquare;

		//checks for each filled line
		for(int a = highest; a <= 29; a++){
			for(Tetromino b: placedPieces){
				for(Rectangle c: b.getShapeParts()){
					if(c.getBounds().y/gridSquare == a){
						blocksFull++;
					}
				}

			}
			if(blocksFull == 16){
				fullLines.add(a);//adds line that needs to be removed
			}
			blocksFull = 0;
		}

		int linesRemoved = fullLines.size();

		//removes filled lines if there are any by checking every placed pieces
		if(linesRemoved > 0){
			for(int a: fullLines){
				for(int b = 0; b < placedPieces.size(); b++){
					for(int c = 0; c < placedPieces.get(b).getShapeParts().size(); c++){
						if((placedPieces.get(b).getShapeParts().get(c)).getBounds().y == a*gridSquare){
							placedPieces.get(b).getShapeParts().remove(c);//removes each part of the tetromino at a certain spot
							c = -1;
						}	
					}
					if(placedPieces.get(b).getShapeParts().size() == 0){
						placedPieces.remove(b);//removes the piece if all parts have been removed
						b = -1;
					}
				}
				movePlacedPieces(a, highest);	//method that moves everything down accordingly
			}
			score += linesRemoved * 100;//calculate score
			ghostPiece.updateGhost(currentPiece.getShapeParts());
			ghostDrop();
			repaint();//method that repaints everything on the panel to essentially update the screen
		}

	}

	/**
	 * Method that moves everything down accordingly as described in the method clearLine()
	 * 
	 * @param line line that was removed
	 * @param highest the highest placed piece
	 */
	private void movePlacedPieces(int line, int highest) {

		for(int a = line-1; a >= highest; a--){//every piece above line given
			for(Tetromino b: placedPieces){
				for(Rectangle c: b.getShapeParts()){
					if(c.getBounds().y/gridSquare == a){
						c.translate(0, 20);
					}				
				}
			}
		}
	}

	/**
	 * Method that generates a new tetromino and updates the game
	 */
	private void newPiece(String reason) {

		int index = rand.nextInt(7);
		shapePreview.addLast(new Tetromino(index));
		
		if(reason.equals("new")){
			placedPieces.add(currentPiece);// new piece
		}
		
		currentPiece = shapePreview.removeFirst();
		ghostPiece = new TetrominoGhost(currentPiece.getIndex());//new ghost piece
		ghostDrop();
		shapeParts = currentPiece.getShapeParts();
		repaint(); //method that repaints everything on the panel to essentially update the screen

	}
}