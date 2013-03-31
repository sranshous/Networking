import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread implements Runnable {
    private final Socket THREAD_SOCKET;
    private final String MENU = "Welcome! Please select an option\n" +
                                "1. Get the list of logged in users\n" +
                                "2. Send a message\n" +
                                "3. Check my messages\n" +
                                "4. Chat with a friend\n";
    private final Integer NUM_MENU_OPTIONS = 4;
    // to read the input from the socket as strings
    private BufferedReader br;
    // to write to the socket
    private PrintWriter pw;

    /**
     * Create a thread to handle a connection to the chat server.
     * @param threadSocket The socket which the server has accepted and is
     * handing off to this thread to handle
     */
    public ChatServerThread(Socket threadSocket) {
        this.THREAD_SOCKET = threadSocket;
    }

    /**
     * Run this thread.
     * It will create a PrintWriter and a BufferedReader to handle sending and
     * receiving messages from the client.
     */
    public void run() {
        try {
            this.pw = new PrintWriter(this.THREAD_SOCKET.getOutputStream(), true);
            this.br = new BufferedReader(
                    new InputStreamReader(this.THREAD_SOCKET.getInputStream())
            );
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
            return; // ends thread
        }

        while(true) {
            send(MENU);
            String userInput = receiveLine();
            handleChoice(userInput);
        }
    }

    /**
     * Send a message to the client over the socket.
     * @param msg The message to send to the client.
     */
    private void send(String msg) {
        pw.println(msg);
        System.out.println("Send: " + msg);
    }

    /**
     * Receive a message from the client over the socket.
     * @return The message received.
     */
    private String receiveLine() {
        String input = "";

        try {
            input = br.readLine();
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        if(input == null) {
            System.err.println("Error reading the input");
        }
        else {
            /* This is where the input message is handled */
        }
        return input;
    }

    /**
     * Find which option was chosen and do the according action.
     * @param userInput The string that was received by the user in response
     * to the menu.
     */
    public void handleChoice(String userInput) {
        Integer option = getUserOption(userInput);

        if(option == null) {
            System.err.println("Did not choose a valid option.");
            send("Invalid option chosen. Please choose from the menu");
            return;
        }

        switch(option) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                System.err.println("Did not choose a valid option.");
                send("Invalid option chosen. Please choose from the menu");
                break;
        }
    }

    /**
     * Parse the string to extract the users choice, or return null if invalid.
     * @param userInput The string from the user to parse for their chosen option.
     * @return The Integer representing the user choice.
     */
    private Integer getUserOption(String userInput) {
        if(userInput.length() == 0)
            return null;

        char op = userInput.charAt(0);
        // If user option was '1' then '1' - '0' = 1
        Integer userChoice = op - '0';

        return userChoice;
    }
}
