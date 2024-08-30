import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tetris extends JFrame {

    private GameBoard gameBoard;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        gameBoard = new GameBoard();
        add(gameBoard);

        setTitle("Tetris");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Key listener for user inputs
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameBoard.handleKeyPress(e.getKeyCode());
            }
        });
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
