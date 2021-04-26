package app;

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.Queue;

/**
 * Represents the game play flow of Chutes and Ladders. Implements Digraph to
 * track the connections to each square via adjacency, a chute or a ladder. Uses
 * a queue to track the order of turns of each Player. Keeps track of the total
 * number of turns taken by all players collectively.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
public class GameDirector {
	private Queue<Player> players;
	private Player currentPlayer;
	private Player playerWinner;
	private Board board;
	private EdgeWeightedDigraph graph;

	private int boardWidth;
	private int squareSize;
	private int totalTurns;
	private int tokenChange;

	public boolean isActive;
	public StringBuilder fb;

	public GameDirector(int boardSize) {
		boardWidth = boardSize;
		squareSize = boardWidth / 10;

		players = new Queue<Player>();

		resetGame();
	}

	/**
	 * Initializes all GameDirector fields to the default values.
	 */
	public void resetGame() {
		while (!players.isEmpty()) {
			players.dequeue();
		}
		for (PlayerColor color : PlayerColor.values()) {
			Player player = new Player(color, squareSize);
			players.enqueue(player);
		}

		totalTurns = 0;
		tokenChange = 0;
		isActive = true;
		fb = new StringBuilder();

		playerWinner = null;
		currentPlayer = players.dequeue();
		board = new Board(boardWidth, players, currentPlayer);
		graph = board.getGraph();
		board.repaint();
	}

	/**
	 * Starts the current players turn by spinning the spinner.
	 */
	public void startPlayerSpin() {
		fb = new StringBuilder();
		currentPlayer.spinSpinner();
		currentPlayer.spinTimes++;
		currentPlayer.totalSpins++;

		if (currentPlayer.spinTimes == 1) {
			fb.append("Turn ").append(currentPlayer.turns + 1)
					.append(" log (" + currentPlayer + "): \n");
			fb.append(currentPlayer.toString()).append(" spun and it landed on ")
					.append(currentPlayer.latestSpin + ".\n");
		} else if (currentPlayer.spinTimes == 2) {
			fb.append(currentPlayer.toString()).append(" spun again and it landed on ")
					.append(currentPlayer.latestSpin + ".\n");
		}

		board.repaint();
	}

	/*
	 * Moves player to the next square they will land on based on their latest spin
	 * value.
	 */
	public void movePlayer() {
		fb = new StringBuilder();

		currentPlayer.spinTimes = 0;
		currentPlayer.turns++;
		totalTurns++;
		tokenChange = getPlayerTokensEarned(currentPlayer);

		currentPlayer.move();

		fb.append(currentPlayer.toString() + " moved to square " + (currentPlayer.currentPosition));
		fb.append(" and collected " + tokenChange + " token(s).\n");
	}

	// helper method to calculate the number of tokens the player earned
	private int getPlayerTokensEarned(Player p) {
		int tokens = 0;

		for (DirectedEdge edge : board.boardGraph.adj(p.currentPosition)) {
			if (edge.to() == p.nextPosition) {
				tokens += (int) edge.weight();
				break;
			}

		}
		p.tokens += tokens;
		return tokens;
	}

	/**
	 * Checks if current player has won, and ends their turn. If they have not won,
	 * they will be requeue'd and the next player in the queue is selected as the
	 * new current player.
	 */
	public void endTurn() {
		fb = new StringBuilder();
		
		players.enqueue(currentPlayer);
		if (currentPlayer.hasFinished()) {
			playerFinished();
		} else {
			currentPlayer = players.dequeue();
			board.setCurrentPlayer(currentPlayer);
		}

		board.repaint();
	}

	// helper method to check if a player has finished
	private void playerFinished() {
		fb = new StringBuilder();
		isActive = false;
		int place = 2;

		fb.append("\nWoah, we have a finisher! \n");
		fb.append("Now it's time to determine the winner!\n");

		MaxPQ<Player> ranking = new MaxPQ<>();
		for (Player player : players) {
			ranking.insert(player);
		}

		Player p = ranking.delMax();
		playerWinner = p;
		fb.append("Congratulations " + p.toString());
		fb.append(", you won with (" + p.tokens + ") tokens!\n");

		fb.append("Here is how the remaining players ranked.\n");
		while (!ranking.isEmpty()) {
			p = ranking.delMax();
			fb.append(place++ + ": " + p.toString() + " (" + p.tokens + ")\n");
		}
	}

	/**
	 * Sends the player up a ladder or down a chute if they are standing on either
	 */
	public void takeChuteOrLadder() {
		fb = new StringBuilder();
		EdgeWeightedDigraph graph = board.getGraph();
		tokenChange = 0;

		for (DirectedEdge edge : graph.adj(currentPlayer.currentPosition)) {
			if (edge.weight() > 6) {
				if (canTakeLadder()) {
					currentPlayer.nextPosition = edge.to();
					currentPlayer.move();
					fb.append(currentPlayer.toString() + " took a ladder to square " + (edge.to()));

					tokenChange = (int) (edge.weight());
					// System.out.println(tokenChange);
					currentPlayer.tokens += tokenChange;
					fb.append(" and collected\n " + tokenChange + " additional token(s).\n");
				}
				break;
			} else if (edge.weight() < 0) {

				currentPlayer.nextPosition = edge.to();
				currentPlayer.move();
				fb.append(currentPlayer.toString() + " took a chute to square " + (edge.to()));

				if (edge.weight() * -1 < currentPlayer.tokens) {
					tokenChange = (int) edge.weight();
					currentPlayer.tokens += tokenChange;
					fb.append(" and lost " + -tokenChange + " token(s).\n");
				} else
					currentPlayer.tokens = 0;
				break;
			}
		}
	}

	/**
	 * Checks if the current player has landed on a chute square.
	 * 
	 * @return true or false
	 */
	public boolean isChute() {
		for (DirectedEdge edge : graph.adj(currentPlayer.currentPosition)) {
			if (edge.weight() < 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the current player has landed on a ladder square.
	 * 
	 * @return true or false
	 */
	public boolean isLadder() {
		for (DirectedEdge edge : graph.adj(currentPlayer.currentPosition)) {
			if (edge.weight() > 6) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the current player can take the ladder they are standing on.
	 * 
	 * @return true or false
	 */
	public boolean canTakeLadder() {
		for (DirectedEdge e : graph.adj(currentPlayer.currentPosition)) {
			if (e.weight() > 6 && (e.weight() / 2) <= currentPlayer.tokens) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the cost to take the ladder based on it's edge weight.
	 * 
	 * @return true or false
	 */
	public int getLadderCost() {
		int cost = 0;
		for (DirectedEdge e : graph.adj(currentPlayer.currentPosition)) {
			if (e.weight() > 6) {
				cost = (int) e.weight() / 2;
			}
		}
		return cost;
	}

	/**
	 * Returns the player that's in the lead.
	 */
	public Player getLead() {
		Player currentLead = null;
		for (Player player : players) {
			if (currentLead == null)
				currentLead = player;
			else if (player.currentPosition > currentLead.currentPosition)
				currentLead = player;
		}
		return currentLead;
	}

	/**
	 * Returns the sum of all turns by each player.
	 * 
	 * @return number of total turns
	 */
	public int getTotalTurns() {
		return totalTurns;
	}

	/**
	 * Returns the which players turn it currently is.
	 * 
	 * @return current player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Returns the queue of players.
	 * 
	 * @return current player
	 */
	public Queue<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns the player that has won the game.
	 * 
	 * @return player that won
	 */
	public Player getWinner() {
		return playerWinner;
	}

	/**
	 * Returns board used by the game director
	 * 
	 * @return current board
	 */
	public Board getGameBoard() {
		return this.board;
	}
}
