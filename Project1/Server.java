import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
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
                System.out.println("Waiting for connection...");

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
                    try {
                        sendMessage("Send the first integer operand: ");
                        op1 = readOperand();
                    }
                    catch(IOException ioe) {
                        System.err.print("Error: ");
                        System.err.println(ioe.getMessage());
                        ioe.printStackTrace();
                        System.exit(1);
                    }
                } while(op1 == null);
                System.out.println("operand 1: " + op1);

                // get the operator
                char operator = '\0';
                do {
                    try {
                        sendMessage("Enter the operation you would like performed (+,-,*,/): ");
                        operator = readOperator();
                    }
                    catch(IOException ioe) {
                        System.err.print("Error: ");
                        System.err.println(ioe.getMessage());
                        ioe.printStackTrace();
                        System.exit(1);
                    }
                } while(operator == '\0');
                System.out.println("operator: " + operator);

                // get second operand
                Integer op2 = null;
                do {
                    try {
                        sendMessage("Send the second integer operand: ");
                        op2 = readOperand();
                    }
                    catch(IOException ioe) {
                        System.err.print("Error: ");
                        System.err.println(ioe.getMessage());
                        ioe.printStackTrace();
                        System.exit(1);
                    }
                } while(op2 == null);
                System.out.println("operand 2: " + op2);

                sendMessage(op1 + " " + operator + " " + op2 + " = " + doOperation(op1, operator, op2));
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally {
            //Close connections
            try {
                if(pw != null)
                    pw.close();
                if(br != null)
                    br.close();
                if(sSocket != null)
                    sSocket.close();
            }
            catch(IOException ioe) {
                System.err.print("Error: ");
                System.err.println(ioe.getMessage());
                ioe.printStackTrace();
                System.exit(1);
            }
        }
    }

    //send a message to the output stream
    private void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
    }

    private Integer readOperand() throws IOException {
        String input = "";
        Integer op = null;

        try {
            input = br.readLine();

            if(input == null) {
                System.err.println("Error reading the input");
                throw new IOException("Input read was null");
            }
            else {
                op = Integer.parseInt(input);
            }
        }

        catch(NumberFormatException nfe) {
            System.err.print("Error: ");
            System.err.println(nfe.getMessage());
            nfe.printStackTrace();
            System.exit(1); // exit with an error
        }

        return op;
    }

    private char readOperator() throws IOException {
        String input = "";
        char operator = '\0';

        input = br.readLine();

        if(input == null) {
            System.err.println("Error reading the input");
        }
        else {
            operator = input.charAt(0);

            if(operator != '+' && operator != '-' && operator != '*' && operator != '/')
                operator = '\0';    // invalid operator
        }
        return operator;
    }

    // this returns 0 by default also, which is wrong it should use Integer and
    // return null on an error but bleh
    private int doOperation(Integer op1, char operator, Integer op2) {
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

        System.out.println("Result: " + result);
        return result;
    }

 	public static void main(String args[]) {
        Server s = new Server();
        s.run();
    }
}
