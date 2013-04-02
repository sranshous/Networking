/**
 * Handler thread for dealing with clients that connect to the ChatServer.
 * It will prompt them for a login, then present them with a menu of options
 * for them to choose from.
 * @author Stephen Ranshous
 * @see ChatServer
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerThread implements Runnable {
    private final Socket THREAD_SOCKET;
    // list of users logged in
    private final ConcurrentHashMap<String, User> users;
    private final String MENU = "\nWelcome! Please select an option\n" +
                                "1. Get the list of logged in users\n" +
                                "2. Send a message\n" +
                                "3. Check my messages\n" +
                                "4. Chat with a friend\n";
    private final Integer NUM_MENU_OPTIONS = 4;

    // to read the input from the socket as strings
    private BufferedReader br = null;
    // to write to the socket
    private PrintWriter pw = null;
    // while true thread keeps going
    private volatile boolean running = true;

    /**
     * Create a thread to handle a connection to the chat server.
     * @param threadSocket The socket which the server has accepted and is
     * handing off to this thread to handle
     * @param users The map of users logged in
     */
    public ChatServerThread(final Socket threadSocket,
                            final ConcurrentHashMap<String, User> users) {
        this.THREAD_SOCKET = threadSocket;
        this.users = users;
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

        while(running) {
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
        System.out.println("[Debug] Send: " + msg);
    }

    /**
     * Receive a message from the client over the socket.
     * @return The message received.
     */
    private String receiveLine() {
        String input = "";

        try {
            input = br.readLine();
            System.out.println("Input was: " + input);
        }
        catch(SocketException se) {
            System.err.print("Socket Error: ");
            System.err.println(se.getMessage());
            this.running = false; // socket problem, close the thread
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        if(input == null) {
            System.err.println("Error reading the input. " +
                               "Assuming the client disconnected.");
            this.running = false;
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
            case -1:
                System.err.println("-1 sent, EOF.");
                System.exit(1);
            case 1:
                System.err.println("Option 1 chosen.");
                break;
            case 2:
                System.err.println("Option 2 chosen.");
                break;
            case 3:
                System.err.println("Option 3 chosen.");
                break;
            case 4:
                System.err.println("Option 4 chosen.");
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
        if(userInput == null || userInput.length() == 0)
            return null;

        char op = userInput.charAt(0);
        // If user option was '1' then '1' - '0' = 1
        Integer userChoice = op - '0';

        return userChoice;
    }
}
