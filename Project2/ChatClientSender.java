import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A sender thread for the chat client. It will block on a BufferedReader
 * call to readLine and when it receives a line it will send it over the
 * socket.
 * @author Stephen Ranshous
 */
public class ChatClientSender implements Runnable {
    private final PrintWriter pw;       // to write to the socket
    private final BufferedReader br;    // to read from the user
    private volatile boolean running = true;

    /**
     * Create a sender thread.
     * @param br The buffered reader to read from the user.
     * @param pw PrintWriter that is used for writing to the socket.
     */
    public ChatClientSender(final BufferedReader br, final PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }

    /**
     * Run forever.
     */
    public void run() {
        while(running) {
            send();
        }
    }

    /**
     * Read a line from the user using the BufferedReader.
     * @return The line read from the user.
     */
    private String receiveUserLine() {
        String input = "";

        try {
            input = br.readLine();

            if(input == null) {
                System.err.println("Error reading the input from the server");
                input = "";
                this.running = false;
            }
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            System.exit(1);
        }

        return input;
    }

    /**
     * Send a string over the socket connection after reading it from the user.
     */
    private void send() {
        // read the users input and send it
        String userMessage = receiveUserLine();
        sendMessage(userMessage);
    }

    /**
     * The helper method to actually send the message over the socket.
     */
    private void sendMessage(String msg) {
        pw.println(msg);
        System.out.println("[Debug] Send message: " + msg);
    }
}
