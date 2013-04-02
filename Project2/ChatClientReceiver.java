/**
 * Receiver thread for the chat client. It will block on a BufferedReader
 * call to readLine() over the socket. Once it receives something it will
 * display it to the user terminal.
 * @author Stephen Ranshous
 */
import java.io.BufferedReader;
import java.io.IOException;

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
        try {
            String line = br.readLine();
            while(true) {
                System.out.println(line);
                line = br.readLine();

                if(line == null) {
                    // assume the server quit abruptly so we should exit
                    System.err.println("Server failed abruptly, exiting.");
                    running = false;
                }
            }
        }
        catch(IOException ioe) {
                System.out.print("Error: ");
                System.out.println(ioe.getMessage());
                System.exit(1);
        }
    }
}
