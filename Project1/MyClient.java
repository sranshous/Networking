import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MyClient {
    private Socket requestSocket = null;
    private BufferedReader br = null;
    private BufferedReader userInput = null;
    private PrintWriter pw = null;

    public void MyClient() {}

    void run() {
        try {
            while(true) {
                //create a socket to connect to the server
                requestSocket = new Socket("localhost", 8000);
                System.out.println("Connected to localhost in port 8000");

                //initialize inputStreams and outputStream
                br = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
                userInput = new BufferedReader(new InputStreamReader(System.in));
                pw = new PrintWriter(requestSocket.getOutputStream());
                pw.flush();

                // first operand
                doServerSendAndReceive();

                // operator
                doServerSendAndReceive();

                // second operand
                doServerSendAndReceive();
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

    private String receiveServerLine() {
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

    private void doServerSendAndReceive() {
        // receive request
        String serverMessage = receiveServerLine();
        // show it to the user
        System.out.print(serverMessage);

        // read the users input and send it
        String userMessage = receiveUserLine();
        System.err.println("Sending the message: " + userMessage);
        sendMessage(userMessage);
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
        System.out.println("Send message: " + msg);
    }

    //main method
    public static void main(String args[]) {
        MyClient client = new MyClient();
        client.run();
    }
}
