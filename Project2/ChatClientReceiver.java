import java.io.BufferedReader;
import java.io.IOException;

/**
 * Receiver thread for the chat client. It will block on a BufferedReader
 * call to readLine() over the socket. Once it receives something it will
 * display it to the user terminal.
 * @author Stephen Ranshous
 */
public class ChatClientReceiver implements Runnable {
    private final BufferedReader br;
    private volatile boolean running = true;

    /**
     * Create a receiver thread.
     * @param br The BufferedReader to read from the socket.
     */
    public ChatClientReceiver(final BufferedReader br) {
        this.br = br;
    }

    /**
     * Run forever waiting for input from the socket to print to the screen.
     */
    public void run() {
        login();
        String line = receiveLine();
        while(true) {
            line = receiveLine();
            System.out.println(line);
        }
    }

    /**
     * Reads a line from the socket and returns it.
     * @return The line read from the socket.
     */
    public String receiveLine() {
        String line = null;

        try {
            line = br.readLine();
        }
        catch(IOException ioe) {
            System.out.print("Error: ");
            System.out.println(ioe.getMessage());
            System.exit(1);
        }

        if(line == null) {
            // assume the server quit abruptly so we should exit
            System.err.println("Server failed abruptly, exiting.");
            System.exit(1);
        }

        return line;
    }

    /**
     * A method to handle logging in. This is only necessary for formatting
     * purposes.
     */
    public void login() {
        String line = receiveLine();
        while(!line.equalsIgnoreCase("login done")) {
            System.out.print(line);
            line = receiveLine();
        }
    }
}
