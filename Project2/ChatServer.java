import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ChatServer implements Runnable {
    // the port to listen on for clients to connect
    private final int SOCKET_PORT = 8000;
    // the socket that the clients will connect to
    private ServerSocket serverSocket = null;

    /**
     * Create the chat server and it will listen on port 8000.
     */
    public void ChatServer() {
        // Do nothing
    }

    public void run() {
        try {
            //create a serversocket
            serverSocket = new ServerSocket(SOCKET_PORT, 10);

            while(true) {
                // Wait for connection
                System.out.println("Waiting for connection...");

                // accept a connection from the client
                Socket connection = serverSocket.accept();
                System.out.println("Connection received from " +
                                    connection.getInetAddress().getHostName() +
                                    "\nSplitting it into a thread"
                );

                // Spawn a new thread to handle the connection
                Thread clientHandler = new Thread(new ChatServerThread(connection));
                clientHandler.start();
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        finally {
            try {
                serverSocket.close();
            }
            catch(IOException ioe) {
                // it must be closed already, do nothing
            }
        }
    }

    public static void main(String args[]) {
        ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
