package app;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.Queue;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextArea;

/**
 * Creates a window that displays the statistics from the most recent game.
 * 
 * @author Trevor Colton & Joshua Gray
 *
 */
@SuppressWarnings("serial")
public class ResultsWindow extends JPanel {
	private GameDirector game;
	private StringBuilder stats;
	
	private Iterable<Integer> shortestPath;
	private int leastNumOfSpins;
	private int totalTurns;
	private int rank;
	private Player winner;
	private Queue<Player> players;
	private MaxPQ<Player> ranking;
	
	public ResultsWindow(GameDirector game) {
		this.game = game;
		
		setPreferredSize(new Dimension(500, 500));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		setGameStats();
		writeGameStats();
		
		JLabel lblTitle = createTitleLabel();
		panel.add(lblTitle, BorderLayout.NORTH);
		
		JScrollPane scrollStatsSummary = createScrollTextArea();
		panel.add(scrollStatsSummary, BorderLayout.CENTER);

	}

	private JScrollPane createScrollTextArea() {
		JTextArea statsTextArea = new JTextArea();
		statsTextArea.setText(stats.toString());
		statsTextArea.setEditable(false);
		statsTextArea.setCaretPosition(0);
		setVisible(true);
		
		JScrollPane scrollStatsSummary = new JScrollPane(statsTextArea);
		scrollStatsSummary.setBackground(Color.LIGHT_GRAY);
		return scrollStatsSummary;
	}

	private JLabel createTitleLabel() {
		JLabel lblTitle = new JLabel(winner.toString() + " Won!  Game Stats:");
		lblTitle.setOpaque(true);
		lblTitle.setBackground(winner.translatePlayerColor(200));
		lblTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 24));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		return lblTitle;
	}

	// initializes fields
	private void setGameStats() {
		shortestPath = game.getGameBoard().graph.shortestPath;
		leastNumOfSpins = -1;
		shortestPath.forEach((e) -> {leastNumOfSpins++;} );
		
		totalTurns = game.getTotalTurns();
		winner = game.getWinner();
		players = game.getPlayers();
		
		rank = 1;
		ranking = new MaxPQ<>();
		for (Player player : players) {
			ranking.insert(player);
		}
	}
	
	private void writeGameStats() {
		stats = new StringBuilder();
		stats.append("Most Recent Game's Statistics.\n");
		stats.append("Game Genereal Stats:\n");
		stats.append("The shortest possible path of squares to reach the finish is:\n");
		shortestPath.forEach((e) -> {
			if (!e.equals(0) && !e.equals(100))
				stats.append(e + " -> ");
			else if(!e.equals(0))
				stats.append(e + "\n");
		} );
		stats.append("That would only take " + leastNumOfSpins + " spins!\n");
		stats.append("Game ended on turn: " + winner.turns + "\n");
		stats.append("Total turns taken by all players: " + totalTurns + "\n\n");
		
		stats.append("Player Stats: \n");
		stats.append("The winner of the game is: " + winner.toString() + " with " + winner.tokens + " tokens!\n");
		ranking.forEach((p) -> {
			stats.append(p.toString() + "'s stats:\n");
			stats.append("Ranking: " + rank++ + "\n");
			stats.append("Number of tokens earned: " + p.tokens + "\n");
			stats.append("Path player took:\n");
			p.playerPath.forEach((e) -> {
				if (!e.equals(0) && !e.equals(100))
					stats.append(e + " -> ");
				else if(!e.equals(0))
					stats.append(e);
				});
			stats.append("\nNumber of spins used: " + p.totalSpins + "\n");
			stats.append("Turns taken: " + p.turns + "\n\n");
		});
	}
}
