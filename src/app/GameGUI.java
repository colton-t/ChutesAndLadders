package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Represents the main window that houses all aspects of the game.
 * @author Trevor Colton & Joshua Gray
 *
 */
@SuppressWarnings("serial")
public class GameGUI extends JFrame {
	private static GameGUI frame;
	private JPanel contentPane;
	private GameDirector game;
	private GameDirector mostRecentGame;
	private JLabel playerRedLbl, playerBlueLbl, playerYellowLbl, playerGreenLbl;
	private boolean gameSimulating;

	/**
	 * Launchs the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new GameGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates the frame.
	 */
	public GameGUI() {
		game = new GameDirector(700);
		BoardCanvas canvas = new BoardCanvas();
		gameSimulating = false;

		initMainWindow();

		createContentPane();
		setContentPane(contentPane);

		JPanel infoPanel = createInfoPanel();
		contentPane.add(infoPanel, BorderLayout.WEST);

		createRedLabel();
		infoPanel.add(playerRedLbl);

		createBlueLabel();
		infoPanel.add(playerBlueLbl);

		createYellowLabel();
		infoPanel.add(playerYellowLbl);

		createGreenLabel();
		infoPanel.add(playerGreenLbl);

		JPanel textPanel = createTextPanel();
		infoPanel.add(textPanel);

		JPanel menuPanel = new JPanel();
		menuPanel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(menuPanel, BorderLayout.NORTH);

		JMenu mnNewMenu = new JMenu("New menu");
		menuPanel.add(mnNewMenu);

		JPanel controlPanel = createControlPanel();
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		JLabel lblCurrentPlayer = createCurrentPlayerLabel();
		controlPanel.add(lblCurrentPlayer);
		
		JLabel lblRemainingSpins = createSpinLabel();
		controlPanel.add(lblRemainingSpins);

		JLabel lblSpinResult = createSpinResultLabel();
		controlPanel.add(lblSpinResult);

		JButton btnSpin = createBtnSpin();
		controlPanel.add(btnSpin);

		JButton btnMove = createBtnMove();
		controlPanel.add(btnMove);

		JButton btnNewGame = createBtnNewGame();
		controlPanel.add(btnNewGame);
		
		JButton btnSimulateGame = createSimBtn();
		controlPanel.add(btnSimulateGame);

		JButton btnRecentStats = createStatsBtn();
		controlPanel.add(btnRecentStats);
		
		JTextArea turnSummary = new JTextArea();
		turnSummary.setBackground(Color.LIGHT_GRAY);
		
		JScrollPane scrollSummary = createScrollText(turnSummary);
		controlPanel.add(scrollSummary);

		// spin button
		btnSpin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (game.isActive) {
					game.startPlayerSpin();

					lblSpinResult.setText("" + game.getCurrentPlayer().latestSpin);
					lblRemainingSpins.setText("Remaining Spins: " + (2 - game.getCurrentPlayer().spinTimes));

					turnSummary.append(game.fb.toString());

					btnMove.setEnabled(true);
					if (game.getCurrentPlayer().spinTimes == 2) {
						btnSpin.setEnabled(false);
					}
					repaint();
				}
			}
		});

		// move button
		btnMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (game.isActive) {
					moveButtonLogic(infoPanel, controlPanel, lblCurrentPlayer, lblRemainingSpins, btnSpin, btnMove,
							btnRecentStats, turnSummary);
				}
			}
		});

		// new game button
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				game.resetGame();
				lblCurrentPlayer.setText(game.getCurrentPlayer().toString() + "'s turn, press spin to start.");
				turnSummary.setText("");
				lblSpinResult.setText("Press Spin");
				lblRemainingSpins.setText("Remaining Spins: " + (2 - game.getCurrentPlayer().spinTimes));
				resetPlayerLbls(game.getCurrentPlayer());
				resetPanelColor(infoPanel, controlPanel);
				
				btnSpin.setEnabled(true);
				btnMove.setEnabled(false);
				btnRecentStats.setVisible(false);
				repaint();
			}
		});
		
		btnRecentStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mostRecentGame != null) {
					showStatsWindow(frame);
				}
			}
		});
		
		btnSimulateGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameSimulating = true;
				
				while (game.isActive) {
					btnSpin.doClick();
					btnMove.doClick();
				}
				
				gameSimulating = false;
			}
		});
		
		contentPane.add(canvas);
	}
	
	private void showStatsWindow(JFrame frame) {
		ResultsWindow r = new ResultsWindow(mostRecentGame);
		
		JDialog resultsFrame = new JDialog(frame, "Game Stats");
		resultsFrame.getContentPane().add(r);
		resultsFrame.setSize(500, 500);
		resultsFrame.setLocationRelativeTo(frame);
		resultsFrame.setModal(true);
		resultsFrame.setVisible(true);
		resultsFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
	}
	
	private void winnerColorDisplay(JPanel infoPanel, JPanel controlPanel) {
		infoPanel.setBackground(game.getWinner().translatePlayerColor(150));
		controlPanel.setBackground(game.getWinner().translatePlayerColor(150));
	}
	
	private void resetPanelColor(JPanel infoPanel, JPanel controlPanel) {
		infoPanel.setBackground(Color.GRAY);
		controlPanel.setBackground(Color.GRAY);
	}

	private void updatePlayerLblText(Player p) {
		switch (p.playerColor) {
		case RED: // top left corner
			playerRedLbl.setText("Player Red (" + p.tokens + " tokens)");
			break;
		case BLUE: // top right corner
			playerBlueLbl.setText("Player Blue (" + p.tokens + " tokens)");
			break;
		case YELLOW: // bottom left corner
			playerYellowLbl.setText("Player Yellow (" + p.tokens + " tokens)");
			break;
		case GREEN: // bottom right corner
			playerGreenLbl.setText("Player Green (" + p.tokens + " tokens)");
			break;
		}
	}
	
	private void updatePlayerLblColor(Player p) {
		setAllPlayerLblDull();
		
		switch (p.playerColor) {
		case RED: // top left corner
			playerRedLbl.setBackground(p.translatePlayerColor(255));
			break;
		case BLUE: // top right corner
			playerBlueLbl.setBackground(p.translatePlayerColor(255));
			break;
		case YELLOW: // bottom left corner
			playerYellowLbl.setBackground(p.translatePlayerColor(255));
			break;
		case GREEN: // bottom right corner
			playerGreenLbl.setBackground(p.translatePlayerColor(255));
			break;
		}
	}
	
	private void setAllPlayerLblDull() {
		playerRedLbl.setBackground(new Color(255, 0, 0, 40));
		playerBlueLbl.setBackground(new Color(0, 0, 255, 40));
		playerYellowLbl.setBackground(new Color(255, 255, 0, 40));
		playerGreenLbl.setBackground(new Color(0, 125, 0, 40));
	}

	private void resetPlayerLbls(Player current) {
		playerRedLbl.setText("Player Red (0 tokens)");
		playerBlueLbl.setText("Player Blue (0 tokens)");
		playerYellowLbl.setText("Player Yellow (0 tokens)");
		playerGreenLbl.setText("Player Green (0 tokens)");
		
		updatePlayerLblColor(current);
	}

	private class BoardCanvas extends JPanel {
		@Override
		protected void paintComponent(Graphics g) { // called back via repaint()
			super.paintComponent(g);
			game.getGameBoard().paintAll(g);
			repaint();
		}
	}
	
	private void moveButtonLogic(JPanel infoPanel, JPanel controlPanel, JLabel lblCurrentPlayer,
			JLabel lblRemainingSpins, JButton btnSpin, JButton btnMove, JButton btnRecentStats,
			JTextArea turnSummary) {
		game.movePlayer();
		
		updatePlayerLblText(game.getCurrentPlayer());
		turnSummary.append(game.fb.toString());
		repaint();

		if (game.isLadder()) {
			if (game.canTakeLadder()) {
				if (gameSimulating) {
					game.canTakeLadder();
				}else {
					String m = "Your tokens meet the ladder requirements, would you like to take the ladder?";
					int response = JOptionPane.showConfirmDialog(contentPane, m, "It's Ladder Time!",
							JOptionPane.YES_NO_OPTION);
					if (response == JOptionPane.YES_OPTION) {
						game.takeChuteOrLadder();
						turnSummary.append(game.fb.toString());
					}
				}
			} else {
				int ladderCost = game.getLadderCost();
				if (!gameSimulating) {
					String m = "You do not have enough tokens to use the ladder. You need " + ladderCost
							+ " tokens." + " You currently have " + game.getCurrentPlayer().tokens + ".";
					JOptionPane.showMessageDialog(contentPane, m, "It's Not Ladder Time..",
							JOptionPane.OK_OPTION);
				}
				turnSummary.append(game.getCurrentPlayer().toString());
				turnSummary.append(" did not have enough tokens to take the ladder. \n(Needed " + ladderCost + " tokens)\n");
			}
		} else {
			game.takeChuteOrLadder();
			turnSummary.append(game.fb.toString());
		}
		
		updatePlayerLblText(game.getCurrentPlayer());
		repaint();

		game.endTurn();
		turnSummary.append("\n");
		
		if (!game.isActive) {
			winnerColorDisplay(infoPanel, controlPanel);
			updatePlayerLblColor(game.getWinner());
			mostRecentGame = game;
			btnRecentStats.setVisible(true);
			showStatsWindow(frame);

		} else {
			lblCurrentPlayer.setText(game.getCurrentPlayer().toString() + "'s turn, press spin to start.");
			lblRemainingSpins.setText("Remaining Spins: " + (2 - game.getCurrentPlayer().spinTimes));
			updatePlayerLblColor(game.getCurrentPlayer());
		}
		
		repaint();

		btnSpin.setEnabled(true);
		btnMove.setEnabled(false);
	}
	
	private JScrollPane createScrollText(JTextArea turnSummary) {
		JScrollPane scrollSummary = new JScrollPane(turnSummary);
		scrollSummary.setBackground(Color.LIGHT_GRAY);
		scrollSummary.setBounds(358, 54, 376, 145);
		return scrollSummary;
	}

	private JButton createStatsBtn() {
		JButton btnRecentStats = new JButton("See Latest Games Stats");
		btnRecentStats.setFont(new Font("SansSerif", Font.PLAIN, 17));
		btnRecentStats.setBounds(10, 139, 338, 60);
		btnRecentStats.setVisible(false);
		return btnRecentStats;
	}

	private JButton createSimBtn() {
		JButton btnSimulateGame = new JButton("Game Simulation");
		btnSimulateGame.setFont(new Font("SansSerif", Font.PLAIN, 17));
		btnSimulateGame.setBounds(10, 27, 185, 60);
		return btnSimulateGame;
	}

	private JButton createBtnNewGame() {
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.setFont(new Font("SansSerif", Font.PLAIN, 17));
		btnNewGame.setBounds(212, 27, 136, 60);
		return btnNewGame;
	}

	private JButton createBtnMove() {
		JButton btnMove = new JButton("Move");
		btnMove.setFont(new Font("Tahoma", Font.ITALIC, 18));
		btnMove.setBounds(912, 123, 152, 76);
		btnMove.setEnabled(false);
		return btnMove;
	}

	private JButton createBtnSpin() {
		JButton btnSpin = new JButton("Spin");
		btnSpin.setFont(new Font("Tahoma", Font.ITALIC, 18));
		btnSpin.setBounds(912, 11, 152, 76);
		return btnSpin;
	}

	private JLabel createSpinResultLabel() {
		JLabel lblSpinResult = new JLabel("Press Spin..");
		lblSpinResult.setFont(new Font("Trebuchet MS", Font.BOLD, 23));
		lblSpinResult.setOpaque(true);
		lblSpinResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpinResult.setBounds(744, 54, 158, 106);
		return lblSpinResult;
	}

	private JLabel createSpinLabel() {
		JLabel lblRemainingSpins = new JLabel("Remaining Spins: 2");
		lblRemainingSpins.setHorizontalAlignment(SwingConstants.CENTER);
		lblRemainingSpins.setBounds(744, 37, 158, 14);
		return lblRemainingSpins;
	}

	private JLabel createCurrentPlayerLabel() {
		JLabel lblCurrentPlayer = new JLabel(game.getCurrentPlayer().toString() + "'s turn, press spin to start.");
		lblCurrentPlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblCurrentPlayer.setBackground(Color.LIGHT_GRAY);
		lblCurrentPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentPlayer.setOpaque(true);
		lblCurrentPlayer.setFocusable(false);
		lblCurrentPlayer.setBounds(358, 11, 376, 25);
		return lblCurrentPlayer;
	}
	
	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(Color.GRAY);
		controlPanel.setPreferredSize(new Dimension(10, 210));
		controlPanel.setLayout(null);
		return controlPanel;
	}

	private void initMainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1100, 1000);
		setBackground(Color.DARK_GRAY);
	}

	private void createContentPane() {
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(1100, 900));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
	}

	private JPanel createInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.GRAY);
		infoPanel.setPreferredSize(new Dimension(360, 10));
		infoPanel.setSize(200, HEIGHT);
		infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		return infoPanel;
	}

	private void createGreenLabel() {
		playerGreenLbl = new JLabel("Player Green (0 tokens)");
		playerGreenLbl.setPreferredSize(new Dimension(150, 60));
		playerGreenLbl.setOpaque(true);
		playerGreenLbl.setHorizontalAlignment(SwingConstants.CENTER);
		playerGreenLbl.setForeground(Color.BLACK);
		playerGreenLbl.setBackground(new Color(0, 125, 0, 40));
	}

	private void createYellowLabel() {
		playerYellowLbl = new JLabel("Player Yellow (0 tokens)");
		playerYellowLbl.setPreferredSize(new Dimension(150, 60));
		playerYellowLbl.setOpaque(true);
		playerYellowLbl.setHorizontalAlignment(SwingConstants.CENTER);
		playerYellowLbl.setForeground(Color.BLACK);
		playerYellowLbl.setBackground(new Color(255, 255, 0, 40));
	}

	private void createBlueLabel() {
		playerBlueLbl = new JLabel("Player Blue (0 tokens)");
		playerBlueLbl.setPreferredSize(new Dimension(150, 60));
		playerBlueLbl.setOpaque(true);
		playerBlueLbl.setHorizontalAlignment(SwingConstants.CENTER);
		playerBlueLbl.setForeground(Color.BLACK);
		playerBlueLbl.setBackground(new Color(0, 0, 255, 40));
	}

	private void createRedLabel() {
		playerRedLbl = new JLabel("Player Red (0 tokens)");
		playerRedLbl.setPreferredSize(new Dimension(150, 60));
		playerRedLbl.setOpaque(true);
		playerRedLbl.setForeground(Color.BLACK);
		playerRedLbl.setHorizontalAlignment(SwingConstants.CENTER);
		playerRedLbl.setBackground(new Color(255, 0, 0));
	}

	private JPanel createTextPanel() {
		JPanel textPanel = new JPanel();
		textPanel.setPreferredSize(new Dimension(350, 550));
		textPanel.setLayout(new BorderLayout(0, 0));

		JLabel titleLbl = new JLabel("Chutes and Ladders");
		titleLbl.setFont(new Font("Trebuchet MS", Font.BOLD | Font.ITALIC, 25));
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		textPanel.add(titleLbl, BorderLayout.NORTH);

		JTextArea txtrWelcomeToChutes = new JTextArea();
		txtrWelcomeToChutes.setRequestFocusEnabled(false);
		txtrWelcomeToChutes.setForeground(Color.BLACK);
		txtrWelcomeToChutes.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		txtrWelcomeToChutes.setText("Welcome to Chutes and Ladders! \r\nRules of the the Game: \r\n\r\nSpin your way to the last square with the most tokens \r\npossible. Whoever has the highest token count, \r\nregardless of who finishes first, wins the game! \r\n\r\nEach player will earn 1 token for every square traveled \r\non the board. If a player takes a ladder, they will earn\r\nadditional tokens. If a player lands on a chute, they will \r\nlose tokens.\r\n\r\nTo take a ladder, each player must have a certain \r\namount of tokens depending on the ladder size, if \r\nthe player doesn't have enough tokens, they cannot \r\nclimb the ladder.\r\n\r\nEach player is allowed two spins per turn. If the player \r\nchooses, they may forfiet their first spin to respin, in an \r\nattempt to get a more favorable result.\r\n\r\nWhen a player lands on the last square the game ends! \r\nPlayer tokens are then tallied, and whichever player has \r\nthe most tokens, wins the game!\r\n\r\nHave fun, and good luck!");
		txtrWelcomeToChutes.setEnabled(true);
		txtrWelcomeToChutes.setEditable(false);
		txtrWelcomeToChutes.setPreferredSize(new Dimension(300, 400));
		textPanel.add(txtrWelcomeToChutes, BorderLayout.CENTER);
		return textPanel;
	}
}
