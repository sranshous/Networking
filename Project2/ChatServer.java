import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple chat server that will sit on a port and listen for connections.
 * It receives a connection and then spawns a handler thread that will deal
 * with the individual details such as logging in, choosing menu options, etc.
 * @author Stephen Ranshous
 * @see ChatServerThread
 */
public class ChatServer implements Runnable {
    // the port to listen on for clients to connect
    private final int SOCKET_PORT = 8000;
    // the list of logged in users
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    // the socket that the clients will connect to
    private ServerSocket serverSocket = null;
    // keep track of the number of threads we've created
    private static int numClientThreads = 0;

    /**
     * Create the chat server and it will listen on port 8000.
     */
    public void ChatServer() {

    }

    /**
     * Run forever listening for clients to connect. Once they connect spawn
     * a thread to handle them
     * @see ChatServerThread
     */
    public void run() {
        try {
            //create a serversocket
            serverSocket = new ServerSocket(SOCKET_PORT, 10);

            while(true) {
                // Wait for connection
                System.out.format(
                        "Waiting for connection on port %d...\n", SOCKET_PORT
                );

                // accept a connection from the client
                final Socket connection = serverSocket.accept();
                System.out.format("Connection received from %s " +
                                    " with IP address %s " +
                                    " on port %d " +
                                    "\nSplitting it into a thread\n",
                                    connection.getInetAddress().getHostName(),
                                    connection.getInetAddress().getHostAddress(),
                                    connection.getPort()
                );

                // Spawn a new thread to handle the connection
                Thread clientHandler = new Thread(
                        new ChatServerThread(connection, users),
                        "ClientHandler" + ++numClientThreads
                );
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
