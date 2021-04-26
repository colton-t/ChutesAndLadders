package app;


import java.awt.Color;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Represents a current player on the board. Each player (max of 4) will have a distinct PlayerColor and
 * methods to navigate the board.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
public class Player implements Comparable<Player>{
	protected PlayerColor playerColor;
	
	protected int currentPosition;
	protected int nextPosition;
	protected int latestSpin;
	
	protected Queue<Integer> playerPath;
	protected int tokens;
	protected int spinTimes;
	protected int totalSpins;
	protected int turns;
	
	protected int size;
	protected int offsetX;
	protected int offsetY;
	
	/**
	 * Initializes all player fields;
	 * @param color from PlayerColor Enum
	 */
	public Player(PlayerColor color, int squareSize) {
		this.playerColor = color;
		this.size = squareSize / 4;
		resetPlayer();
		
		//offset positioning for drawing players on an individual square of the board
		switch(playerColor) {
		   case RED: 	//top left corner
			   offsetX = 1;
			   offsetY = 1;
		       break;
		   case BLUE:	//top right corner
			   offsetX = (size * 3) - 2;
			   offsetY = 1;
		       break;
		   case YELLOW:	//bottom left corner
			   offsetX = 1;
			   offsetY = (size * 3) - 2;
			   break;
		   case GREEN:	//bottom right corner
			   offsetX = (size * 3) - 2;
			   offsetY = (size * 3) - 2;
			   break;
		}
	}
	
	/**
	 * Resets player fields to default values.
	 */
	public void resetPlayer() {
		currentPosition = 0;
		nextPosition = -1;
		latestSpin = -1;
		
		playerPath = new Queue<>();
		
		spinTimes = 0;
		tokens = 0;
		turns = 0;
	}
	
	/**
	 * Gets a random number from the spinner (between 1-6) and sets the <code>nextPosition</code>
	 * to the square the player will land on.
	 */
	public void spinSpinner() {
		if (spinTimes == 1) {
			int secondSpin = 0;
			do {
				secondSpin = StdRandom.uniform(1, 7);
			} while(secondSpin == latestSpin);
			latestSpin = secondSpin;
		} else {
			latestSpin = StdRandom.uniform(1, 7);
		}
		nextPosition = currentPosition + latestSpin;
	}
	
	/**
	 * Moves the player's <code>currentPosition</code> to the specified <code>nextPosition</code>.
	 * 
	 * @param moves number of positions the player should move
	 */
	public void move() {
		if (nextPosition >= 100) {
			currentPosition = 100;
			playerPath.enqueue(currentPosition);
		}
		else {
			currentPosition = nextPosition;
			playerPath.enqueue(currentPosition);
		}
	}
	
	/**
	 * Returns the java.awt.Color representation of the players PlayerColor with custom
	 * transparency.
	 * 
	 * @param transparency 
	 * @return Color c
	 */
	public Color translatePlayerColor(int transparency) {
		Color c;
		switch(this.playerColor) {
		   case RED:
			   c = new Color(255,0,0,transparency);
			   break;
		   case BLUE:
			   c = new Color(0,0,255,transparency);
			   break;
		   case YELLOW:
			   c = new Color(255,255,0,transparency);
			   break;
		   case GREEN:
			   c = new Color(0,128,0,transparency);
			   break;
		   default:
			   c = Color.BLACK;
		}
		return c;
	}
	
	/**
	 * Checks if the player is at position 99 which indicates that the player has reached the finish line and won.
	 * 
	 * @return true if player's at position 99, false if not
	 */
	public boolean hasFinished() {
		return currentPosition == 100;
	}

	@Override
	public String toString() {
		return "Player " + playerColor.toString();
	}
	
	@Override
	public int compareTo(Player that) {
		return this.tokens - that.tokens;
	}
}

