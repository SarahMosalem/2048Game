package org.cis1200.TwentyFortyEightAndCupcakes;

/*
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

//Put rest of fileIO stuff
//And run, check load and save!
//Add instructions at the stwrt

import javax.swing.*;
import java.awt.*;

public class RunTwentyFortyEightAndCupcakes implements Runnable {
    //Display instructions panel before the game starts using Jpption
    private void instructionsPanel() {
        String instructions = """
            Welcome to 2048 with a few extra features. In case you've never played, here's how to play!
            1. Use the arrow keys to slide the tiles
            2. Combine tiles with the same number to create larger numbers
            3. Your score is keeping track of the sum of all merges during gameplay
            4. Try to keep adding tiles to get to 2048!
            5. (Hint to get to 2048 - If you hit the "Game Over", you can undo your mistake by clicking "undo"!)
            
            Button functionalties: 
            1. Reset: starts a new game
            2. Undo: reverts to the previous move
            3. Save: saves your current game state
            4. Load: loads the configuration from your saved state. 
            
            Press the ok button to start. Goodluck! 
        """;

        //Show instructions in a JOptionPane dialog (sarah look at rec slides)
        JOptionPane.showMessageDialog(
                null, //parent component bc null centers it
                instructions, //the actual message to display
                "Game Instructions", //the dialog title
                JOptionPane.INFORMATION_MESSAGE // The type
        );
    }

    @Override
    public void run() {
        // FIRST, show the instructions panel before game starts
        instructionsPanel();

        // frame with game components stored
        final JFrame frame = new JFrame("2048");
        frame.setLocation(300, 300);

        // panel with BorderLayout
        final JPanel mainPanel = new JPanel(new BorderLayout());

        // fanel for buttons and score
        final JPanel topPanel = new JPanel(new BorderLayout());


        // Buttons at top
        final JPanel buttonPanel = new JPanel();
        final JButton resetButton = new JButton("Reset");
        final JButton undoButton = new JButton("Undo");
        final JButton saveButton = new JButton("Save");
        final JButton loadButton = new JButton("Load");

        final JLabel scoreDisplay = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreDisplay.setFont(new Font("Arial", Font.BOLD, 16));

        // Add buttons to the button panel
        buttonPanel.add(resetButton);
        buttonPanel.add(undoButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        //Add score label to the top panel
        topPanel.add(scoreDisplay, BorderLayout.EAST);
        topPanel.add(buttonPanel, BorderLayout.WEST);

        //Add the top panel to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);

        //Game board in the center
        final GameBoard board = new GameBoard(scoreDisplay);
        mainPanel.add(board, BorderLayout.CENTER);

        //Add functionality to reset button
        resetButton.addActionListener(e -> {
            board.reset();
            scoreDisplay.setText("Score: " + board.getGame().getScore()); // Update the score label
            board.requestFocusInWindow(); // Ensure focus is retained for key inputs
        });

        //add functionality to undo button
        undoButton.addActionListener(e -> {
            board.undo();
            scoreDisplay.setText("Score: " + board.getGame().getScore()); // Update the score label
            board.repaint(); // Repaint the board to reflect the undo state
            board.requestFocusInWindow(); // Request focus back to the game board
        });

        //Add functionality to save button
        saveButton.addActionListener(e -> {
            board.getGame().saveGameState();
            JOptionPane.showMessageDialog
                    (frame, "Game state saved! Press the button load to revert back to this state.");
            board.requestFocusInWindow(); // Ensure focus is retained for key inputs
        });
        //Add functionality to load button
        loadButton.addActionListener(e -> {
            board.load(); //Call load in gameboard
            scoreDisplay.setText("Score: " + board.getGame().getScore()); //update the score label
            board.repaint(); //repaint the board w loaded state
            board.requestFocusInWindow(); //keeps focus
        });

        //Add the main panel to the frame
        frame.add(mainPanel);

        //frame setup
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //set focus again
        board.setFocusable(true);
        board.requestFocusInWindow();

        //starts it
        board.reset();
    }
}

//old. debug
/** public class RunTwentyFortyEightAndCupcakes implements Runnable {

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the game
        board.reset();
    }
 **/

// three buttons, save, reset, cupcake mode. instead of toggle, have button change the whole thing.
// on cupcake version, have normal button and make it change whole screen.


