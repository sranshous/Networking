import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Throw errors up the chain back to the run() method so we can
 * disconnect the current user (or realize they disconnected) and then
 * wait for the next one.
 */
public class ChatServer implements Runnable {
    // the port to listen on for clients to connect
    private final int SOCKET_PORT = 8000;

    // the socket that the clients will connect to
    private ServerSocket serverSocket = null;
    // keep track of client # that is connecting
    private int clientNum = 1;
    // to read from the socket
    private BufferedReader br = null;
    // to write to the socket
    private PrintWriter pw = null;
    // are we currently handling a client? did he disconnect?
    private boolean clientConnected = false;
    // current client
    private String client = null;
    // map usernames to their password. all in plaintext. nice.
    private HashMap<String, String> loginInfo = null;
    // map each client to their and their messages
    private HashMap<String, ArrayList<String>> userMessages = null;

    public ChatServer() {
        initUserList();
        printUserList();
    }

    private void initUserList() {
        try {
            FileReader fr = new FileReader("users.txt");
            BufferedReader br = new BufferedReader(fr);

            loginInfo = new HashMap<>();
            userMessages = new HashMap<>();

            String line = null;
            while((line = br.readLine()) != null) {
                String[] user = line.split(",");
                String username = user[0];
                String password = user[1];

                loginInfo.put(username, password);
                userMessages.put(username, new ArrayList<String>());
            }
        }
        catch(FileNotFoundException fnfe) {
            System.err.print("Error: ");
            System.err.println(fnfe.getMessage());
            System.err.println("You forgot to create a users file.");
            fnfe.printStackTrace();
            System.exit(1);
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private void printUserList() {
        if(loginInfo == null)
            System.err.println("Shits null");
        for(Map.Entry<String, String> user : loginInfo.entrySet()) {
            System.out.format("Username: %s\tPassword: %s\n", user.getKey(), user.getValue());
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(SOCKET_PORT, 10);
            System.out.format(
                    "%s\n%s:%d\n",
                    "Server started!",
                    "Listen on 127.0.0.1",
                    SOCKET_PORT
            );


            while(true) {
                // accept a connection from the client
                final Socket connection = serverSocket.accept();
                System.out.format("Client %d connected\n", clientNum++);
                this.clientConnected = true;
                getStreams(connection);
                handleClient();
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            close();
        }
    }

    private void getStreams(Socket client) {
        try {
            this.pw = new PrintWriter(client.getOutputStream(), true);
            this.br = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    private void close() {
        try {
            if(br != null)
                br.close();
            if(pw != null)
                pw.close();
            if(serverSocket != null)
                serverSocket.close();
        }
        catch(IOException ioe) {
            // it must be closed already, do nothing
        }
    }

    private String receiveLine() {
        String input = "";

        try {
            input = br.readLine();
        }
        catch(SocketException se) {
            System.err.print("Socket Error: ");
            System.err.println(se.getMessage());
            this.clientConnected = false;
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        if(input == null) {
            /*
            System.err.println("Error reading the input. " +
                               "Assuming the client disconnected.");
            */
            this.clientConnected = false;
        }

        return input;
    }

    private void send(String message) {
        pw.println(message);
    }

    private void handleClient() {
        send("Welcome! Please login.");
        boolean loginSuccessful = login();

        while(clientConnected && loginSuccessful) {
            String userCommand = receiveLine();

            if(userCommand == null) {
                clientConnected = false;
            }
            else if(userCommand.equals("GET_USER_LIST")) {
                sendUserList();
            }
            else if(userCommand.equals("SEND_MESSAGE")) {
                receiveMessage();
            }
            else if(userCommand.equals("GET_MESSAGES")) {
                sendMessages();
            }
            else {
                // invalid command
            }
        }
    }

    private boolean login() {
        String user = "";
        boolean validUser = false;
        boolean success = false;

        while(!validUser) {
            String ui = receiveLine();
            if(ui == null) {
                break;
            }

            String[] userInfo = ui.split(",");
            if(userInfo.length < 2) {
                validUser = false;
            }
            else {
                if(loginInfo.containsKey(userInfo[0])) {
                    if(loginInfo.get(userInfo[0]).equals(userInfo[1])) {
                        client = userInfo[0];
                        validUser = true;
                    }
                }
            }

            if(validUser) {
                send("VALID_USER");
                success = true;
            }
            else {
                send("INVALID");
                success = false;
            }
        }

        System.out.format("Login user %s\n", client);
        System.out.format("Login password %s\n", loginInfo.get(client));

        return success;
    }

    public void sendUserList() {
        StringBuilder s = new StringBuilder();
        for(String user : loginInfo.keySet()) {
            s.append(user + "\n");
        }
        s.append("END_USER_LIST");
        send(s.toString());
        System.out.println("User list sent");
    }

    public void receiveMessage() {
        String username = receiveLine();
        String message = receiveLine();
        System.out.format("A message to %s\n", username);
        if(userMessages.containsKey(username)) {
            ArrayList<String> messages = userMessages.get(username);
            messages.add(message);
        }
    }

    public void sendMessages() {
        StringBuilder s = new StringBuilder();
        for(String message : userMessages.get(client)) {
            s.append(message + "\n");
        }
        s.append("END_MESSAGES");
        send(s.toString());
        System.out.println("Sent the clients messages");
    }

    public static void main(String... args) {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
