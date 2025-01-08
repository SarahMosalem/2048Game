package org.cis1200.TwentyFortyEightAndCupcakes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private TwentyFortyEightAndCupcakes tfe; //Model for game
    private JLabel scoreLabel; //Current score display
    private JLabel gameOverLabel; //Label to display gameover message

    //Game constants
    public static final int BOARD_WIDTH = 400;
    public static final int BOARD_HEIGHT = 400;

    //Initializes the game board.

    public GameBoard(JLabel scoreInit) {
        this.tfe = new TwentyFortyEightAndCupcakes(); //Initialize game model
        this.scoreLabel = scoreInit; //Link score label

        // Set up gameover label
        this.gameOverLabel = new JLabel(" ");
        this.gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
        this.gameOverLabel.setForeground(Color.RED);
        this.gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.gameOverLabel.setVisible(true); //make the label always visible but initially empty

        // Set focusable-  key events are captured
        this.setFocusable(true);

        //key Adapter for arrow key handling
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                boolean validMove = false;

                if (keyCode == KeyEvent.VK_LEFT) {
                    System.out.println("Left arrow pressed.");
                    if (tfe.canMoveLeft()) {
                        tfe.moveLeft();
                        validMove = true;
                        System.out.println("Move left successful.");
                    } else {
                        System.out.println("Move left invalid.");
                    }
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    System.out.println("Right arrow pressed.");
                    if (tfe.canMoveRight()) {
                        tfe.moveRight();
                        validMove = true;
                        System.out.println("Move right successful.");
                    } else {
                        System.out.println("Move right invalid.");
                    }
                } else if (keyCode == KeyEvent.VK_UP) {
                    System.out.println("Up arrow pressed.");
                    if (tfe.canMoveUp()) {
                        tfe.moveUp();
                        validMove = true;
                        System.out.println("Move up successful.");
                    } else {
                        System.out.println("Move up invalid.");
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    System.out.println("Down arrow pressed.");
                    if (tfe.canMoveDown()) {
                        tfe.moveDown();
                        validMove = true;
                        System.out.println("Move down successful.");
                    } else {
                        System.out.println("Move down invalid.");
                    }
                } else if (keyCode == KeyEvent.VK_Z) {
                    System.out.println("Undo pressed.");
                    tfe.undo();
                    scoreLabel.setText("Score: " + tfe.getScore());
                    repaint();
                    return; // Skip further checks for undo

                    // Handle egde cases
                } else {
                    System.out.println("Unrecognized key pressed.");
                    return; // Ignore unrecognized keys
                }

                if (validMove) {
                    System.out.println("Checking game over...");
                    tfe.checkGameOver();
                    if (tfe.getGameOver()) {
                        System.out.println("Game Over!");
                        gameOverLabel.setText("Game Over!");
                    }
                } else {
                    System.out.println("No tiles merged or moved.");
                }

                // Update UI regardless of move validity
                scoreLabel.setText("Score: " + tfe.getScore());
                repaint();
                requestFocusInWindow(); // Ensure focus stays on the game board
            }
        });
    }

    public void reset() {
        System.out.println("Reset button pressed. Game resetting...");
        tfe.reset();
        scoreLabel.setText("Score: 0");
        gameOverLabel.setText(""); //Clear game over message
        repaint();
        this.requestFocusInWindow(); //Focus
    }

    public void undo() {
        System.out.println("Undo button pressed.");
        tfe.undo();
        scoreLabel.setText("Score: " + tfe.getScore());
        gameOverLabel.setText(""); // clear game over text
        repaint();
        this.requestFocusInWindow(); //Focus
    }

    public void load() {
        System.out.println("Load button pressed.");
        tfe.loadGameState();
        scoreLabel.setText("Score: " + tfe.getScore()); // Update score label
        gameOverLabel.setText(""); //clear game over
        repaint();
        this.requestFocusInWindow();
    }

    // Getter method for tfe
    public TwentyFortyEightAndCupcakes getGame() {
        return tfe;
    }

    //Draws the game board. sarah not overriding both, just paint component (rec slides)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //Draw grid
        int unitWidth = BOARD_WIDTH / 4;
        int unitHeight = BOARD_HEIGHT / 4;
        g2d.setColor(Color.BLACK);
        for (int i = 1; i < 4; i++) {
            g2d.drawLine(i * unitWidth, 0, i * unitWidth, BOARD_HEIGHT); // Vertical lines
            g2d.drawLine(0, i * unitHeight, BOARD_WIDTH, i * unitHeight); // Horizontal lines
        }

        //Draw tiles
        int[][] board = tfe.getBoard();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int value = board[row][col];
                if (value != 0) {
                    drawTile(g2d, value, col * unitWidth, row * unitHeight, unitWidth, unitHeight);
                }
            }
        }

        //Draw game over message in the center if game is over
        if (tfe.getGameOver()) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g2d.getFontMetrics();
            String text = "Game Over!";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2d.drawString(text, (BOARD_WIDTH - textWidth) / 2, (BOARD_HEIGHT + textHeight) / 2);
        }
    }

    //Helper method to draw tile
    private void drawTile(Graphics2D g2d, int value, int x, int y, int width, int height) {
        //Figure out tile color based on value (sarah google rgb values)
        Color color;
        if (value == 2) {
            color = new Color(238, 228, 218);
        } else if (value == 4) {
            color = new Color(237, 224, 200);
        } else if (value == 8) {
            color = new Color(242, 177, 121);
        } else if (value == 16) {
            color = new Color(245, 149, 99);
        } else if (value == 32) {
            color = new Color(246, 124, 95);
        } else if (value == 64) {
            color = new Color(246, 94, 59);
        } else if (value == 128) {
            color = new Color(237, 207, 114);
        } else if (value == 256) {
            color = new Color(237, 204, 97);
        } else if (value == 512) {
            color = new Color(237, 200, 80);
        } else if (value == 1024) {
            color = new Color(237, 197, 63);
        } else if (value == 2048) {
            color = new Color(237, 194, 46);
        } else {
            color = Color.GRAY; //Edge case, default color
        }
        g2d.setColor(color);
        g2d.fillRoundRect(x + 5, y + 5, width - 10, height - 10, 15, 15);

        //Draw tile text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        String text = String.valueOf(value);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2d.drawString(text, x + (width - textWidth) / 2, y + (height + textHeight) / 2 - 5);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}


