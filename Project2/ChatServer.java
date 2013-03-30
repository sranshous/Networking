import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ChatServer {
    // the port to listen on for clients to connect
    private final int SOCKET_PORT = 8000;
    // the socket that the clients will connect to
    private final ServerSocket serverSocket;
    // to read the input from the socket as strings
    private final BufferedReader br;
    // to write to the socket
    private final PrintWriter pw;

    /**
     * Create the chat server.
     */
    public void ChatServer() {
        // Default constructor
    }

    void run() {
        try {
            //create a serversocket
            serverSocket = new ServerSocket(SOCKET_PORT, 10);

            while(true) {
                // Wait for connection
                System.out.println("Waiting for connection...");

                // accept a connection from the client
                connection = serverSocket.accept();
                System.out.println("Connection received from " +
                                    connection.getInetAddress().getHostName() +
                                    "\nSplitting it into a thread"
                );
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally {
            serverSocket.close();
        }
    }

    public static void main(String args[]) {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
