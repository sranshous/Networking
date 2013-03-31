import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private Socket requestSocket = null;
    private BufferedReader br = null;
    private BufferedReader userInput = null;
    private PrintWriter pw = null;

    public void MyClient() {}

    void run() {
        try {
            userInput = new BufferedReader(new InputStreamReader(System.in));
            char connectAgain = 'y';

            while(connectAgain == 'y') {
                //create a socket to connect to the server
                requestSocket = new Socket("localhost", 8000);
                System.out.println("Connected to localhost in port 8000");

                //initialize inputStreams and outputStream
                br = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
                pw = new PrintWriter(requestSocket.getOutputStream());
                pw.flush();

                // first operand
                doServerReceiveAndSend();

                // operator
                doServerReceiveAndSend();

                // second operand
                doServerReceiveAndSend();

                // receive the result
                System.out.println(receiveServerLine());

                System.out.print("Would you like to do this again? y/[n]: ");
                String userChoice = receiveUserLine();

                if(userChoice != "" && userChoice != null) {
                    if(userChoice.charAt(0) == 'y' || userChoice.charAt(0) == 'Y')
                        connectAgain = 'y';
                    else
                        connectAgain = 'n';
                }
                else {
                    connectAgain = 'n';
                }
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
            try {
                if(pw != null)
                    pw.close();
                if(br != null)
                    br.close();
                if(userInput != null)
                    userInput.close();
                if(requestSocket != null)
                    requestSocket.close();
            }
            catch(IOException ioe) {
                System.err.print("Error: ");
                System.err.println(ioe.getMessage());
                ioe.printStackTrace();
                System.exit(1);
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

    private void doServerReceiveAndSend() {
        // receive request
        String serverMessage = receiveServerLine();
        // show it to the user
        System.out.print(serverMessage);

        // read the users input and send it
        String userMessage = receiveUserLine();
        sendMessage(userMessage);
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
    }

    //main method
    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }
}
