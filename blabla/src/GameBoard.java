import javax.swing.*;
import java.awt.*;

public class GameBoard extends JPanel {
    private int[][] board;
    private final int CELL_SIZE = 30; // Size of each cell

    public GameBoard() {
        this.board = new int[20][10]; // Default size of the board
        setBackground(Color.BLACK); // Set background color to black
    }

    public void updateBoard(int[][] newBoard) {
        this.board = newBoard;
        repaint(); // Request a repaint to update the display
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        final Color BLOCK_COLOR = Color.WHITE; // Color of the blocks

        // Draw the board
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] != 0) { // If the cell is occupied
                    g.setColor(BLOCK_COLOR);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK); // Border color
                    g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw cell border
                }
            }
        }
    }
}
