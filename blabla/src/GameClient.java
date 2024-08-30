import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class GameClient {
    private Socket socket;
    private DataOutputStream out;
    private ObjectInputStream in;

    public GameClient(String host, int port) {
        try {
            // Initialize connection
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server.");

            // Example of sending a command
            sendCommand("LEFT");

            // Example of receiving board state
            List<List<Integer>> board = receiveBoardState();
            if (board != null) {
                System.out.println("Received board state: " + board);
            } else {
                System.out.println("Failed to receive board state.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (socket != null) socket.close();
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommand(String command) {
        try {
            out.writeUTF(command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<List<Integer>> receiveBoardState() {
        try {
            // This assumes that the server sends a serialized object.
            return (List<List<Integer>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        new GameClient("localhost", 5000);
    }
}
