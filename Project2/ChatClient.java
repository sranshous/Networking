/**
 * A chat client that will connect to an IP on a given port and send and receive
 * messages. If no IP and port is provided it will connect to localhost on port
 * 8000.
 * @author Stephen Ranshous
 */
import java.net.Socket;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class ChatClient {
    private Socket requestSocket = null;       // socket to connect to the server
    private BufferedReader br = null;          // for reading from the socket
    private BufferedReader userInput = null;   // for reading from the user
    private PrintWriter pw = null;             // for writing to the socket
    private Thread receiver = null;            // to receive from the socket
    private Thread sender = null;              // to send over the socket
    private String IP = null;                  // IP to connect to
    private Integer port = null;               // port to connect to

    public void ChatClient() {

    }

    /**
     * Read a line of input from the user.
     * @return The input from the user.
     */
    private String readUserLine() {
        String line = null;

        try {
            line = userInput.readLine();
        }
        catch(IOException ioe) {
            System.out.print("Error: ");
            System.out.println(ioe.getMessage());
            System.exit(1);
        }

        return line;
    }

    /**
     * Asks the user to enter the IP to connect to.
     * @return The IP typed in by the user
     */
    private String getIP() {
        System.out.print("Enter the IP you would like to connect to [localhost]: ");
        String IP = readUserLine();

        return IP;
    }

    /**
     * Asks the user to enter the port they would like to connect on.
     * @return The port entered
     */
    private Integer getPort() {
        System.out.print("Enter the port you would like to connect on [8000]: ");
        String portS = readUserLine();
        Integer port = null;

        if(portS.equals("")) {
            port = 8000;
        }
        else {
            try {
                port = Integer.parseInt(portS);
            }
            catch(NumberFormatException nfe) {
                System.out.print("Error: ");
                System.out.println(nfe.getMessage());
                System.exit(1);
            }
        }

        return port;
    }

    /**
     * Run the chat client. Spawn the sender and receiver threads.
     * It will ask the user for the IP and port. If the user does not enter
     * an IP it will use "localhost". If the user does not enter a port it
     * will use 8000.
     */
    public void run() {
        try {
            userInput = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            // Get the IP and port from the user. If they just hit Enter Enter
            // then use the default IP of localhost and port 8000.
            String userIP = getIP();
            this.IP = (userIP.equals("") ? "127.0.0.1" : userIP);
            Integer userPort = getPort();
            this.port = (userPort == null ? 8000 : userPort);

            requestSocket = new Socket(IP, port);
            System.out.format("Connected to %s on port %d\n", IP, port);

            br = new BufferedReader(
                    new InputStreamReader(requestSocket.getInputStream())
            );
            pw = new PrintWriter(requestSocket.getOutputStream(), true);

            receiver = new Thread(new ChatClientReceiver(br));
            receiver.start();
            sender = new Thread(new ChatClientSender(userInput, pw));
            sender.start();

            while(true) {
                // accept incoming connections to chat and do something.
                // need to be able to chat to different connections ultimately
                // which will require multiple windows (in the form of a GUI)
            }
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //Close connections
            try{
                pw.close();
                br.close();
                userInput.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    //main method
    public static void main(String args[]) {
        ChatClient client = new ChatClient();
        client.run();
    }
}
