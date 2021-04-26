package app;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Queue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

/**
 * Represents the board of chutes and ladders composed of 100 squares that uses
 * a Digraph from algs4 to connect each square to the next square and squares
 * that are connected via a ladder or chute.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
@SuppressWarnings("serial")
public class Board extends JPanel {
	private Queue<Player> players;
	private Player currentPlayer;

	private int boardWidth;
	private int squareSize;
	private int columns;
	private int rows;

	private Square[] squares;
	public BoardGenerator graph;
	public EdgeWeightedDigraph boardGraph;

	/**
	 * Initializes all board fields and generates squares to fill up the board
	 */
	public Board(int boardSize, Queue<Player> players, Player currentPlayer) {
		// initialize board fields
		this.boardWidth = boardSize;
		this.squareSize = boardWidth / 10;
		this.players = players;
		this.currentPlayer = currentPlayer;

		// initialize necessary JPanel fields
		setPreferredSize(new Dimension(700, 700));
		setBackground(Color.GRAY);

		columns = 10;
		rows = 10;
		squares = new Square[columns * rows];
		
		graph = new BoardGenerator();
		
		// set up directed graph from text file
		// In in = new In("src/app/resources/board.txt");
		boardGraph = graph.graph;

		// initialize the array of squares and their corresponding x and y positions
		int x = 0;
		int y = (rows - 1) * squareSize;
		int direction = 1;

		for (int i = 0; i < columns * rows; i++) {
			Square s = new Square(x, y, squareSize, i);
			squares[i] = s;
			x = x + (squareSize * direction);
			if (x >= columns * squareSize || x <= -squareSize) {
				direction *= -1;
				x += squareSize * direction;
				y -= squareSize;
			}
		}
	}

	/**
	 * Paints all elements of the current board state.
	 */
	public void paintAll(Graphics g) {
		paintBoard(g);
		paintCurrentPlayer(g);
		paintAllPlayers(g);
		highlightPlayerMove(g);
	}

	// paints the base game board, complete with the squares, chutes, and ladders
	private void paintBoard(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// paints all squares on the board
		for (Square s : squares) {
			g.setColor(s.c);
			g.drawRect(s.x, s.y, s.width - 1, s.width - 1);
			g.fillRect(s.x, s.y, s.width - 1, s.width - 1);

			g.setColor(Color.black);
			g.drawString(Integer.toString(s.index + 1), s.getCenterX(7), s.getCenterY(7));
		}

		// paints all chutes and ladders
		for (int v = 1; v < boardGraph.V(); v++) {
			for (DirectedEdge edge : boardGraph.adj(v)) {
				if (edge.weight() > 6)
					g2.setColor(Color.green);
				else if (edge.weight() < 0)
					g2.setColor(Color.red);
				else
					continue;

				int x1 = squares[v - 1].getCenterX(12);
				int y1 = squares[v - 1].getCenterY(9);
				int x2 = squares[edge.to() - 1].getCenterX(12);
				int y2 = squares[edge.to() - 1].getCenterY(9);

				g2.setStroke(new BasicStroke(2));
				g2.draw(new Line2D.Float(x1, y1, x2, y2));
			}
		}
	}

	// paints all players to the board that are in the player queue
	private void paintAllPlayers(Graphics g) {
		for (Player p : players) {
			if (p.currentPosition > 0) {
				Square s = squares[p.currentPosition - 1];
				g.setColor(p.translatePlayerColor(200));

				g.drawOval((s.x + p.offsetX), (s.y + p.offsetY), p.size, p.size);
				g.fillOval((s.x + p.offsetX), (s.y + p.offsetY), p.size, p.size);
			}
		}
	}

	// paints the player taking their turn to the board
	private void paintCurrentPlayer(Graphics g) {
		if (currentPlayer.currentPosition > 0) {
			Square s = squares[currentPlayer.currentPosition - 1];
			int size = currentPlayer.size;

			g.setColor(currentPlayer.translatePlayerColor(200));
			g.drawOval((s.x + currentPlayer.offsetX), (s.y + currentPlayer.offsetY), size, size);
			g.fillOval((s.x + currentPlayer.offsetX), (s.y + currentPlayer.offsetY), size, size);
		}
	}

	// Highlights the path the player is going to move.
	private void highlightPlayerMove(Graphics g) {
		if (currentPlayer.currentPosition < currentPlayer.nextPosition) {
			g.setColor(currentPlayer.translatePlayerColor(120));

			int start = (1 > currentPlayer.currentPosition ? 0 : currentPlayer.currentPosition - 1);
			int end = (100 <= currentPlayer.nextPosition ? 99 : currentPlayer.nextPosition - 1);
			for (int i = start; i <= end; i++) {
				g.fillRect(squares[i].x, squares[i].y, squares[i].width, squares[i].width);
			}
		}
	}
	
	/**
	 * Updates whose turn it is on the board.
	 * 
	 * @param Player p
	 */
	public void setCurrentPlayer(Player p) {
		this.currentPlayer = p;
	}

	/**
	 * Returns the graph of the board.
	 * 
	 * @return directed graph
	 */
	public EdgeWeightedDigraph getGraph() {
		return boardGraph;
	}
}