package app;

import java.awt.Color;

/**
 * Represents a single square on the game board.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
public class Square {
	protected int x;
	protected int y;
	protected int width;
	protected int index;
	protected Color c;

	/**
	 * Initializes the x and y coordinates, size, and index of the square.
	 * 
	 * @param x X-Coordinate of the square on game board
	 * @param y Y-Coordinate of the square on game board
	 * @param width width (or length) of square
	 * @param index square's position on the game board
	 */
	public Square(int x, int y, int width, int index) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.index = index;
		
		//alternating color pattern
		if (index % 2 == 0) {
			this.c = Color.WHITE;
		} else {
			this.c = new Color(70, 130, 180);
		}
	}
	
	/**
	 * Returns the center x value of the square, the offset accommodates printing text.
	 * 
	 * @return center value of x with an offset
	 */
	public int getCenterX(int offset) {
		return x + (width / 2) - offset;
	}
	
	/**
	 * Returns the center y value of the square, the offset accommodates printing text.
	 * 
	 * @return center value of y with an offset
	 */
	public int getCenterY(int offset) {
		return y + (width / 2) + offset;
	}
}
