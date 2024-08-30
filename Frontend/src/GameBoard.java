import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.KeyEvent;  // Import KeyEvent for handling keyboard inputs

public class GameBoard extends JPanel {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int BLOCK_SIZE = 30;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int[][] board;  // Holds the current state of the game board

    public GameBoard() {
        initBoard();
        connectToBackend();
    }

    private void initBoard() {
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));

        board = new int[BOARD_HEIGHT][BOARD_WIDTH];  // Empty board
    }

    private void connectToBackend() {
        try {
            socket = new Socket("localhost", 5000);  // Connect to the Python backend
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start a thread to continuously receive the game state from the Python backend
        new Thread(() -> {
            while (true) {
                try {
                    // Assuming the backend sends the board as a 2D array
                    board = (int[][]) in.readObject();
                    repaint();  // Redraw the board
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] != 0) {
                    g.setColor(Color.GREEN);  // You can change this to different colors based on the block type
                    g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }

    public void handleKeyPress(int keyCode) {
        try {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    out.writeObject("LEFT");
                    break;
                case KeyEvent.VK_RIGHT:
                    out.writeObject("RIGHT");
                    break;
                case KeyEvent.VK_DOWN:
                    out.writeObject("DOWN");
                    break;
                case KeyEvent.VK_UP:
                    out.writeObject("ROTATE");
                    break;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
