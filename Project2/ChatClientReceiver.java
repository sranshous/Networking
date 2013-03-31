import java.io.BufferedReader;
import java.io.IOException;

public class ChatClientReceiver implements Runnable {
    private final BufferedReader br;
    private volatile boolean running = true;

    public ChatClientReceiver(BufferedReader br) {
        this.br = br;
    }

    public void run() {
        try {
            String line = br.readLine();
            while(true) {
                System.out.println(line);
                line = br.readLine();

                if(line == null) {
                    // assume the server quit abruptly so we should exit
                    System.err.println("Server failed abruptly, exiting.");
                    System.exit(1);
                }
            }
        }
        catch(IOException ioe) {
                System.out.print("Error: ");
                System.out.println(ioe.getMessage());
                // possibly want to end the thread
        }
    }
}
