import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MyServer {
    private final int sPort = 8000;
    private ServerSocket sSocket = null;
    private Socket connection = null;
    private BufferedReader br = null;
    private PrintWriter pw = null;

    public void MyServer() {}

	void run() {
		try {
            //create a serversocket
            sSocket = new ServerSocket(sPort, 10);

            while(true) {
                //Wait for connection
                System.out.println("Waiting for connection");

                //accept a connection from the client
                connection = sSocket.accept();
                System.out.println("Connection received from " + connection.getInetAddress().getHostName());

                //initialize Input and Output streams
                pw = new PrintWriter(connection.getOutputStream());
                pw.flush();
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // get first operand
                Integer op1 = null;
                do {
                    sendMessage("Send the first operator");
                    op1 = readOperand();
                } while(op1 == null);

                // get the operator
                sendMessage("Enter the operation you would like performed (+,-,*,/)");
                char operator = readOperator();

                // get the second operator
                sendMessage("Send the second operator");
                Integer op2 = readOperand();

                sendMessage(op1 + " " + operator + " " + op2 + " = " + doOperation(op1, operator, op2));
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally {
            //Close connections
            try {
                pw.close();
                br.close();
                sSocket.close();
            }
            catch(IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream
    private void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
        System.out.println("Send message: " + msg);
    }

    private Integer readOperand() {
        Integer op = null;

        try {
            op = Integer.parseInt(br.readLine());
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }
        catch(NumberFormatException nfe) {
            System.err.print("Error: ");
            System.err.println(nfe.getMessage());
        }

        return op;
    }

    private char readOperator() {
        String input = "";
        char operator = '\0';

        try {
            input = br.readLine();

            if(input == null) {
                System.err.println("Error reading the input");
            }
            else {
                operator = input.charAt(0);
            }
        }
        catch(IOException ioe) {
            System.err.print("Error: ");
            System.err.println(ioe.getMessage());
        }

        return operator;
    }

    // this returns 0 by default also, which is wrong it should use Integer and
    // return null on an error but bleh
    private int doOperation(Integer op1, char operator, Integer op2) {
        System.out.print("op1: " + op1 + "\toperator: " + operator + "\top1: " + op1);
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

        System.out.println("\tresult: " + result);
        return result;
    }

 	public static void main(String args[]) {
        MyServer s = new MyServer();
        s.run();
    }
}
