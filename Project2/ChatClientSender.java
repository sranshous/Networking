import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChatClientSender implements Runnable {
    private final PrintWriter pw;       // to write to the socket
    private final BufferedReader br;    // to read from the user
    private volatile boolean running = true;

    /**
     * Create a sender thread.
     * @param br The buffered reader to read from the user.
     * @param pw PrintWriter that is used for writing to the socket.
     */
    public ChatClientSender(BufferedReader br, PrintWriter pw) {
        this.br = br;
        this.pw = pw;
    }

    public void run() {
        while(true) {
            send();
        }
    }

    private String receiveUserLine() {
        String input = "";

        try {
            input = br.readLine();

            if(input == null) {
                System.err.println("Error reading the input from the server");
                input = "";
            }
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        return input;
    }

    private void send() {
        // read the users input and send it
        String userMessage = receiveUserLine();
        sendMessage(userMessage);
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        pw.println(msg);
        System.out.println("[Debug] Send message: " + msg);
    }
}
