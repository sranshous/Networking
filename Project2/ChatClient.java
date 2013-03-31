import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ChatClient {
    private Socket requestSocket = null;
    private BufferedReader br = null;
    private BufferedReader userInput = null;
    private PrintWriter pw = null;
    private Thread receiver = null;

    public void ChatClient() {

    }

    void run() {
        try {
            requestSocket = new Socket("localhost", 8000);
            System.out.println("Connected to localhost in port 8000");

            br = new BufferedReader(
                    new InputStreamReader(requestSocket.getInputStream())
            );
            userInput = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            pw = new PrintWriter(requestSocket.getOutputStream(), true);

            receiver = new Thread(new ChatClientReceiver(br));
            receiver.start();

            while(true) {
                // this is the sender
                send();
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

    private String receiveUserLine() {
        String input = "";

        try {
            input = userInput.readLine();

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
        pw.flush();
        System.out.println("[Debug] Send message: " + msg);
    }

    //main method
    public static void main(String args[]) {
        ChatClient client = new ChatClient();
        client.run();
    }
}
