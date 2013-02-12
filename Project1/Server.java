import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private final int sPort = 8000;    //The server will be listening on this port number
    ServerSocket sSocket;   //serversocket used to lisen on port number 8000
    Socket connection = null; //socket for the connection with the client
    String message_sent;    //message received from the client
    String message_received;    //uppercase message send to the client
    ObjectOutputStream out;  //stream write to the socket
    ObjectInputStream in;    //stream read from the socket

    public void Server() {}

	void run() {
		try {
            //create a serversocket
            sSocket = new ServerSocket(sPort, 10);
            //Wait for connection
            System.out.println("Waiting for connection");

            //accept a connection from the client
            connection = sSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());

            //initialize Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

           try {
                while(true) {
                    sendMessage("Please send the first operand: ");
                    Integer op1 = Integer.parseInt((String)in.readObject()); // assumes its not null
                    System.out.println("op1: " + op1);

                    //send MESSAGE back to the client
                    sendMessage("Please send the operator (+,-,*,/): ");
                    char operator = ((String)in.readObject()).charAt(0); // assumes its not null
                    System.out.println("operator: " + operator);

                    sendMessage("Please send the second operand: ");
                    Integer op2 = Integer.parseInt((String)in.readObject()); // assumes its not null
                    System.out.println("op2: " + op2);

                    sendMessage("The result is: " + doOperation(op1, operator, op2));
                }
            }
            catch(ClassNotFoundException classnot) {
                System.err.println("Data received in unknown format");
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally {
            //Close connections
            try {
                in.close();
                out.close();
                sSocket.close();
            }
            catch(IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream
    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("Send message: " + msg);
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // this returns 0 by default also, which is wrong it should use Integer and
    // return null on an error but bleh
    private int doOperation(Integer op1, char operator, Integer op2) {
        System.out.println("op1: " + op1 + "\toperator: " + operator + "\top1: " + op1);
        int result = 0;

        switch(operator) {
            case '+':
                result = op1 + op2;
                break;
            case '-':
                result = op1 - op2;
                break;
            case '*':
                result = op1 * op2;
                break;
            case '/':
                result = op1 / op2;
                break;
            default:
                result = 0;
        }

        return result;
    }

 	public static void main(String args[]) {
        Server s = new Server();
        s.run();
    }
}
