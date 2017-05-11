/*
 * Nghiem Ly
 * June 12, 2015
 * Simple replication of the famous tetris game 
 */

package tetris;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * The class that contains the main method of the program
 * This method extends the JFrame class and is used as a JFrame
 * 
 * @author Nghiem
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Tetris extends JFrame{

	public static JLabel gameLabel;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 610;
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Tetris tetris = new Tetris();
					tetris.setLocationRelativeTo(null);
					tetris.setVisible(true);
					tetris.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor for Tetris that initializes the gameLabel as well as create a new Game and sets the size of the JFrame
	 */
	public Tetris() {
		gameLabel = new JLabel();
		gameLabel.setBackground(this.getBackground());
		gameLabel.setOpaque(true);
		gameLabel.setForeground(Color.BLACK);
		gameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gameLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		gameLabel.setBounds(Game.gridSquare * 12, Game.gridSquare * 10, Game.gridSquare * 6, 82);
		gameLabel.setVisible(false);
		gameLabel.setDoubleBuffered(true);
		getContentPane().add(gameLabel);
		
		Game game = new Game(this);
		getContentPane().add(game);
		
		setSize(WIDTH, HEIGHT);
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
