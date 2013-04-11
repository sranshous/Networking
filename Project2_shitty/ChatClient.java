import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatClient implements Runnable {
    private final static String MENU =
        "\n-------------------------------------\n" +
        "Command:\n" +
        "0. Connect to the server\n" +
        "1. Get the user list\n" +
        "2. Send a message\n" +
        "3. Get my messages\n" +
        "4. Initiate a chat with my friend\n" +
        "5. Chat with my friend\n" +
        "Enter your option (0-5): ";
    private Socket requestSocket;
    private ServerSocket serverSocket;
    private BufferedReader br;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean connected = false;
    private boolean running = true;
    private String username = "";

    public ChatClient() {

    }

    public void run() {
        br = new BufferedReader(new InputStreamReader(System.in));
        while(running) {
            System.out.print(MENU);

            try {
                handleUserInput();
            }
            catch(IOException ioe) {
                System.err.print("Error: ");
                System.err.println(ioe.getMessage());
                this.running = false;
            }
        }
    }

    private void handleUserInput() throws IOException {
        String userInput = receiveUserLine();
        Integer command = getUserOption(userInput);
        if(command == null) return;

        switch(command) {
            case 0:
                connectToServer();
                break;
            case 1:
                getUserList();
                break;
            case 2:
                sendAMessage();
                break;
            case 3:
                getMyMessages();
                break;
            case 4:
                initiateChatWithFriend();
                break;
            case 5:
                joinChatWithFriend();
                break;
            default:
                System.err.println("Invalid case");
                break;
        }
    }

    private Integer getUserOption(String input) {
        Integer userOption = null;

        if(input.length() != 0) {
            try {
                userOption = Integer.parseInt(input.substring(0, 1));
            }
            catch(NumberFormatException nfe) {
                System.err.print("Error: ");
                System.err.println(nfe.getMessage());
            }
        }

        return userOption;
    }

    private String receiveUserLine() {
        String input = "";

        try {
            input = br.readLine();
        }
        catch(SocketException se) {
            System.err.print("Socket Error: ");
            System.err.println(se.getMessage());
            System.exit(1);
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        if(input == null) {
            System.err.println("Error reading the input. " +
                               "Assuming the client disconnected.");
            System.exit(1);
        }

        return input;
    }

    private Integer getPort() {
        String port = receiveUserLine();
        Integer portNum = null;
        try {
            portNum = Integer.parseInt(port);
        }
        catch(NumberFormatException nfe) {
            System.err.print("Error: ");
            System.err.println(nfe.getMessage());
        }

        return portNum;
    }

    private void connectToServer() {
        if(this.connected) return;

        System.out.print("Enter the IP: ");
        String IP = receiveUserLine();
        System.out.print("Enter the port: ");
        Integer port = getPort();

        if(port == null) {
            return;
        }

        try {
            System.out.println("Connecting...");
            requestSocket = new Socket(IP, port);
            System.out.println("Connected!");
            socketReader = new BufferedReader(new InputStreamReader(
                        requestSocket.getInputStream()));
            socketWriter = new PrintWriter(requestSocket.getOutputStream(), true);
            this.connected = true;
            login();
        }
        catch(UnknownHostException uhe) {
            System.err.print("Error: ");
            System.err.println(uhe.getMessage());
            System.err.println("Connection failed. Try again.");
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            System.err.println("Connection failed. Try again.");
        }
    }

    private void login() throws IOException {
        String serverMessage = "";

        // read the welcome message and print it
        serverMessage = socketReader.readLine();
        System.out.println(serverMessage);

        while(!serverMessage.equals("VALID_USER")) {
            System.out.print("Enter your username: ");
            this.username = receiveUserLine();
            System.out.print("Enter your password: ");
            String password = receiveUserLine();
            socketWriter.println(username + "," + password);
            serverMessage = socketReader.readLine();
        }
    }

    private void getUserList() throws IOException {
        if(!this.connected) {
            System.out.println("Must connect to a server first.");
            return;
        }

        socketWriter.println("GET_USER_LIST");

        ArrayList<String> users = new ArrayList<>();
        String serverMessage = socketReader.readLine();
        while(!(serverMessage.equals("END_USER_LIST"))) {
            users.add(serverMessage);
            serverMessage = socketReader.readLine();
        }

        System.out.format("There is/are %d user(s).\n", users.size());
        for(int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i));
        }
    }

    public void sendAMessage() {
        if(!this.connected) {
            System.out.println("Must connect to a server first.");
            return;
        }

        System.out.print("Who would you like to send a message to? ");
        String username = receiveUserLine();
        System.out.println("Enter the message you would like to send:");
        String message = receiveUserLine();

        socketWriter.println("SEND_MESSAGE");
        socketWriter.println(username);
        socketWriter.println(message);
    }

    public void getMyMessages() throws IOException {
        if(!this.connected) {
            System.out.println("Must connect to a server first.");
            return;
        }

        socketWriter.println("GET_MESSAGES");
 
        ArrayList<String> messages = new ArrayList<>();
        String serverMessage = socketReader.readLine();
        while(!(serverMessage.equals("END_MESSAGES"))) {
            messages.add(serverMessage);
            serverMessage = socketReader.readLine();
        }

        System.out.format("You have %d message(s).\n", messages.size());
        for(int i = 0; i < messages.size(); i++) {
            System.out.println(messages.get(i));
        }
       
    }

    private void initiateChatWithFriend() throws IOException {
        if(this.connected) {
            System.out.println("---------- Disconnect with server----------");
            requestSocket.close();
            this.connected = false;
        }

        System.out.print("Enter the port you would like to listen on: ");
        Integer port = getPort();
        if(port == null) return;

        final ServerSocket server = new ServerSocket(port, 10);
        System.out.println("I am listening on 127.0.0.1:" + port);
        final Socket friend = server.accept();
        System.out.println("Connection found");
        socketWriter = new PrintWriter(friend.getOutputStream(), true);
        socketReader = new BufferedReader(
                new InputStreamReader(
                    friend.getInputStream()
                )
        );

        // read the clients username
        String friendName = socketReader.readLine();
        // send my username
        socketWriter.println(username);

        System.out.println("Type \"Bye\" to end the conversation");
        while(true) {
            String input = socketReader.readLine();
            if(input == null || input.equalsIgnoreCase("Bye")) {
                System.out.println("Disconnected from my friend!");
                server.close();
                this.connected = false;
                return;
            }

            System.out.format("%s: %s\n", friendName, input);
            System.out.format("%s: ", username);
            String mts = receiveUserLine();
            socketWriter.println(mts);

            if(mts.equalsIgnoreCase("Bye")) {
                System.out.println("Disconnected from my friend!");
                server.close();
                this.connected = false;
                return;
            }
        }
    }

    private void joinChatWithFriend() throws IOException {
        if(this.connected) {
            requestSocket.close();
            this.connected = false;
        }

        System.out.print("Enter the IP: ");
        String IP = receiveUserLine();
        System.out.print("Enter the port: ");
        Integer port = getPort();

        if(port == null) {
            return;
        }

        try {
            System.out.println("Connecting...");
            requestSocket = new Socket(IP, port);
            System.out.println("Connected!");
            socketReader = new BufferedReader(new InputStreamReader(
                        requestSocket.getInputStream()));
            socketWriter = new PrintWriter(requestSocket.getOutputStream(), true);
            this.connected = true;

            // send my username
            socketWriter.println(username);
            // read the clients username
            String friendName = socketReader.readLine();

            System.out.println("Type \"Bye\" to end the conversation");
            while(true) {
                System.out.format("%s: ", username);
                socketWriter.println(receiveUserLine());

                String input = socketReader.readLine();
                if(input == null || input.equalsIgnoreCase("Bye")) {
                    System.out.println("Disconnected from my friend!");
                    requestSocket.close();
                    this.connected = false;
                    return;
                }

                System.out.format("%s: %s\n", friendName, input);
            }
        }
        catch(UnknownHostException uhe) {
            System.err.print("Error: ");
            System.err.println(uhe.getMessage());
            System.err.println("Connection failed. Try again.");
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
            System.err.println("Connection failed. Try again.");
        }

    }

    public static void main(String... args) {
        ChatClient chatClient = new ChatClient();
        chatClient.run();
    }
}
