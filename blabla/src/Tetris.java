import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Tetris extends JFrame {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameBoard board;

    public Tetris() {
        initUI();
        initNetwork();
    }

    private void initUI() {
        board = new GameBoard();
        add(board, BorderLayout.CENTER);
        setTitle("Tetris");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 5));

        addButton(controlPanel, "Left", "LEFT");
        addButton(controlPanel, "Right", "RIGHT");
        addButton(controlPanel, "Down", "DOWN");
        addButton(controlPanel, "Rotate", "ROTATE");
        addButton(controlPanel, "Drop", "DROP");

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void addButton(JPanel panel, String label, String command) {
        JButton button = new JButton(label);
        button.addActionListener(e -> sendCommand(command));
        panel.add(button);
    }

    private void initNetwork() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Start a thread to receive the game state from the server
            new Thread(this::receiveGameState).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receiveGameState() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof int[][]) {
                    int[][] boardState = (int[][]) obj;
                    SwingUtilities.invokeLater(() -> board.updateBoard(boardState));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error receiving game state from the server.", "Network Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources();
        }
    }

    private void sendCommand(String command) {
        try {
            out.writeUTF(command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send command to the server.", "Network Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        closeResources();
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
