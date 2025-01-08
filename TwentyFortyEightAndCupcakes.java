package org.cis1200.TwentyFortyEightAndCupcakes;

import java.util.Random;
import java.util.Stack;
import java.io.*;

public class TwentyFortyEightAndCupcakes {

    //Returns the current game over state
    public boolean getGameOver() {
        return gameOver;
    }

    private int[][] board;
    private boolean gameOver;
    private int score;
    private Stack<int[][]> boardHistory; //Track previous game states for undo
    private Stack<Integer> scoreHistory; //Track scores for undo button

    //instatialize a new game
    public TwentyFortyEightAndCupcakes() {
        reset(); //want to make it its origional state
    }

    //puts it in the actual initial state
    public void reset() {
        board = new int[4][4];      //clean board
        gameOver = false;          //reset game state
        score = 0;                 //reset score
        boardHistory = new Stack<>(); //reset board history for undo
        scoreHistory = new Stack<>(); //reset score history for undo
        generateTiles(2);          //2 initial tiles (2 or 4)
        checkGameOver();           //update game after reset
    }

    //return the CURRENT state of the board
    public int[][] getBoard() {
        return board;
    }

    //and the current score
    public int getScore() {
        return score;
    }

    //Saves the current board and score to their respective undo stacks
    private void saveStateToHistory() {
        int[][] previousState = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                previousState[i][j] = board[i][j]; //Copy each element individually
            }
        }
        boardHistory.push(previousState); //Save board state
        scoreHistory.push(score);         //Save score
    }

    //Undoes the last move by restoring the board and score.
    public void undo() {
        if (!boardHistory.isEmpty() && !scoreHistory.isEmpty()) {
            //Get the last element in the list
            board = boardHistory.get(boardHistory.size() - 1);
            //Remove the last element
            boardHistory.remove(boardHistory.size() - 1);

            score = scoreHistory.get(scoreHistory.size() - 1);
            scoreHistory.remove(scoreHistory.size() - 1);

            checkGameOver();  //double check the game-over state based on the restored board
            System.out.println("Undo performed. Restored previous board state and score.");
        } else {
            System.out.println("No previous states to undo.");
        }
    }

    //check if the board changed after a move
    private boolean hasBoardChanged(int[][] originalBoard) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (originalBoard[i][j] != board[i][j]) {
                    System.out.println("Board changed at (" + i + ", " + j + ")");
                    return true;
                }
            }
        }
        return false;
    }

    public void moveLeft() {
        if (gameOver) {
            return;
        }

        saveStateToHistory(); // Save state before making a move

        // Create a copy of the current board to check for changes later
        int[][] originalBoard = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(board[i], 0, originalBoard[i], 0, 4);
        }

        // Process each row for movement
        for (int i = 0; i < 4; i++) {
            compressLeft(board[i]); // Push tiles to the left
            mergeLeft(board[i]);    // Merge adjacent tiles
            compressLeft(board[i]); // Push tiles to the left again
        }

        // Check if the board changed
        if (hasBoardChanged(originalBoard)) {
            generateTiles(1); // Generate a new tile only if the board changed
            System.out.println("Board changed, new tile generated.");
        } else {
            // Revert saved state manually if no changes occurred
            if (!boardHistory.isEmpty() && !scoreHistory.isEmpty()) { // Similar to the hasNext (check board not emoty)
                // Restore the previous board state
                int[][] lastSavedBoard = boardHistory.get(boardHistory.size() - 1);
                for (int i = 0; i < 4; i++) {
                    System.arraycopy(lastSavedBoard[i], 0, board[i], 0, 4);
                }

                // Restore the previous score
                score = scoreHistory.get(scoreHistory.size() - 1);

                // Remove the last saved states manually
                boardHistory.remove(boardHistory.size() - 1);
                scoreHistory.remove(scoreHistory.size() - 1);

                System.out.println("Board did not change, state reverted.");
            }
        }

        checkGameOver(); // Check if the game is over
    }

    public void moveRight() {
        if (gameOver) {
            return;
        }

        saveStateToHistory(); //save state before making move

        int[][] originalBoard = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(board[i], 0, originalBoard[i], 0, 4);
        }

        for (int i = 0; i < 4; i++) {
            compressRight(board[i]);
            mergeRight(board[i]);
            compressRight(board[i]);
        }

        if (hasBoardChanged(originalBoard)) { // Only generate a new tile if the board changed
            generateTiles(1);
        }

        checkGameOver();
    }

    public void moveUp() {
        if (gameOver) {
            return;
        }

        saveStateToHistory(); //save state before making move

        int[][] originalBoard = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(board[i], 0, originalBoard[i], 0, 4);
        }

        for (int col = 0; col < 4; col++) {
            compressUp(col);
            mergeUp(col);
            compressUp(col);
        }

        if (hasBoardChanged(originalBoard)) { //Only generate a new tile if board changed
            generateTiles(1);
        }

        checkGameOver();
    }

    public void moveDown() {
        if (gameOver) {
            return;
        }

        saveStateToHistory(); //Save state before making move

        int[][] originalBoard = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(board[i], 0, originalBoard[i], 0, 4);
        }

        for (int col = 0; col < 4; col++) {
            compressDown(col);
            mergeDown(col);
            compressDown(col);
        }

        if (hasBoardChanged(originalBoard)) { //Only generate a new tile if board changed
            generateTiles(1);
        }

        checkGameOver();
    }

    private void generateTiles(int tilesToGenerate) {
        Random random = new Random();
        while (tilesToGenerate > 0) {
            int i = random.nextInt(4);
            int j = random.nextInt(4);

            if (board[i][j] == 0) { //Only place a tile in an empty space
                int value = 2; //10% chance of it being 4, and 90% chance of it being 2. Default to 2
                if (random.nextInt(10) >= 9) {
                    value = 4;
                }
                board[i][j] = value;
                tilesToGenerate--;
            }
        }
    }

    //helper methods for movement operations
    //Compress fills the "empty" gaps to the left for ex
    //Then merge combines two adjacent tiles with the same value and returns a tile with
    // double the value on the left tile and returns the other tile with vallue 0
    private void compressLeft(int[] row) {
        int[] compressed = new int[4];
        int index = 0;

        //Move all not zero elements to the start of the row
        for (int num : row) {
            if (num != 0) {
                compressed[index++] = num;
            }
        }

        //copy back the compressed row
        System.arraycopy(compressed, 0, row, 0, 4);
    }

    private void mergeLeft(int[] row) {
        boolean[] merged = new boolean[4]; // Tracks merged tiles to prevent double merges
        for (int i = 0; i < 3; i++) {
            if (row[i] != 0 && row[i] == row[i + 1] && !merged[i]) {
                row[i] *= 2;           // Merge tiles by doubling the value
                score += row[i];       // Update score
                row[i + 1] = 0;        // Clear the merged tile
                merged[i] = true;      // Mark this tile as merged
            }
        }
    }

    private void compressRight(int[] row) {
        int[] compressed = new int[4];
        int index = 3;

        for (int i = 3; i >= 0; i--) {
            if (row[i] != 0) {
                compressed[index--] = row[i];
            }
        }

        System.arraycopy(compressed, 0, row, 0, 4);
    }

    private void mergeRight(int[] row) {
        boolean[] merged = new boolean[4]; // Tracks merged tiles to prevent double merges
        for (int i = 3; i > 0; i--) {
            if (row[i] != 0 && row[i] == row[i - 1] && !merged[i]) {
                row[i] *= 2;           // Merge tiles by doubling the value
                score += row[i];       // Update score
                row[i - 1] = 0;        // Clear the merged tile
                merged[i] = true;      // Mark this tile as merged
            }
        }
    }

    private void compressUp(int col) {
        int[] compressed = new int[4];
        int index = 0;

        for (int row = 0; row < 4; row++) {
            if (board[row][col] != 0) {
                compressed[index++] = board[row][col];
            }
        }

        for (int row = 0; row < 4; row++) {
            board[row][col] = compressed[row];
        }
    }

    private void mergeUp(int col) {
        for (int row = 0; row < 3; row++) {
            if (board[row][col] != 0 && board[row][col] == board[row + 1][col]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row + 1][col] = 0;
            }
        }
    }

    private void compressDown(int col) {
        int[] compressed = new int[4];
        int index = 3;

        for (int row = 3; row >= 0; row--) {
            if (board[row][col] != 0) {
                compressed[index--] = board[row][col];
            }
        }

        for (int row = 0; row < 4; row++) {
            board[row][col] = compressed[row];
        }
    }

    private void mergeDown(int col) {
        for (int row = 3; row > 0; row--) {
            if (board[row][col] != 0 && board[row][col] == board[row - 1][col]) {
                board[row][col] *= 2;
                score += board[row][col];
                board[row - 1][col] = 0;
            }
        }
    }

    public void checkGameOver() {
        gameOver = !(canMoveLeft() || canMoveRight() || canMoveUp() || canMoveDown());
        if (gameOver) {
            System.out.println("Game Over: No valid moves left.");
        }
    }

    //Helper methods checking if moves in any direction are possible
    boolean canMoveLeft() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) { //Stop before the last column
                if (board[i][j] == 0 && board[i][j + 1] != 0) {
                    return true; //An empty space can absorb a tile
                }
                if (board[i][j] == board[i][j + 1]) {
                    return true; //Two tiles can merge
                }
            }
        }
        return false;
    }

    boolean canMoveRight() {
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j > 0; j--) { // Start from the last column and check leftwards
                if (board[i][j] == 0 && board[i][j - 1] != 0) {
                    return true; //An empty space can absorb a tile
                }
                if (board[i][j] == board[i][j - 1]) {
                    return true; //Two tiles can merge
                }
            }
        }
        return false;
    }

    boolean canMoveUp() {
        for (int j = 0; j < 4; j++) { //Iterate through columns
            for (int i = 0; i < 3; i++) { //Stop before the last row
                if (board[i][j] == 0 && board[i + 1][j] != 0) {
                    return true; //An empty space can absorb a tile
                }
                if (board[i][j] == board[i + 1][j]) {
                    return true; //Two tiles can merge
                }
            }
        }
        return false;
    }

    boolean canMoveDown() {
        for (int j = 0; j < 4; j++) { //Iterate through columns
            for (int i = 3; i > 0; i--) { //Start from the last row and check upwards
                if (board[i][j] == 0 && board[i - 1][j] != 0) {
                    return true; //An empty space can absorb a tile
                }
                if (board[i][j] == board[i - 1][j]) {
                    return true; //Two tiles can merge
                }
            }
        }
        return false;
    }

    //sarah look at java docs type filewrite of type what
    private static final String SAVE_FILE = "FileIO.txt"; //file to save and load game state
    //Saves the current game state to a file.
    public void saveGameState() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    writer.write(board[i][j] + " ");
                }
                writer.newLine();
            }
            writer.write("Score: " + getScore());
            writer.newLine();
            System.out.println("Game state saved successfully.");
            //Handle any IO exceptions that might happen while writing to the file
        } catch (IOException e) {
            System.err.println("Error saving game state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Loads the game state from a file.
    public void loadGameState() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(SAVE_FILE)); //want to open the file to read
            int[][] loadedBoard = new int[4][4];
            //and reach each row of the board, splitting the reading based on the spaces
            for (int i = 0; i < 4; i++) {
                String[] line = reader.readLine().trim().split(" ");
                for (int j = 0; j < 4; j++) {
                    loadedBoard[i][j] = Integer.parseInt(line[j]);
                }
            }
            String scoreLine = reader.readLine();
            if (scoreLine != null && scoreLine.startsWith("Score: ")) {
                score = Integer.parseInt(scoreLine.substring(7)); //Get score value
            } else {
                throw new IOException("Invalid score format.");
            }

            board = loadedBoard;
            checkGameOver();
            System.out.println("Game state loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            e.printStackTrace();
            // close it
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
    }
}
