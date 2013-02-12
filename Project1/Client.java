import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message_sent;                //message send to the server
    String message_received;                //capitalized message read from the server

    public void Client() {}

    void run() {
        try {
            //create a socket to connect to the server
            requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                // Get first message
                message_received = (String)in.readObject();
                System.out.println(message_received);

                //read a sentence from the standard input
                message_sent = bufferedReader.readLine();

                //Send the operand to the server
                sendMessage(message_sent);

                // Get second message
                message_received = (String)in.readObject();
                System.out.println(message_received);

                //read a sentence from the standard input
                message_sent = bufferedReader.readLine();

                //Send the operator to the server
                sendMessage(message_sent);

                // Get first message
                message_received = (String)in.readObject();
                System.out.println(message_received);

                //read a sentence from the standard input
                message_sent = bufferedReader.readLine();

                //Send the operand to the server
                sendMessage(message_sent);
            }
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
        catch (ClassNotFoundException e) {
            System.err.println("Class not found");
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
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        try{
        //stream write the message
        out.writeObject(msg);
        out.flush();
        System.out.println("Send message: " + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    //main method
    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }
}
