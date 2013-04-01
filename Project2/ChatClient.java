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
    private Thread sender = null;

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
