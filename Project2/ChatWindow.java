import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * The window to use for a chat session between two clients.
 * @author Stephen Ranshous
 */
public class ChatWindow extends JFrame {
    // this will be the container panel for the GUI, using BorderLayout
    private JPanel main;

    /* Center Panel */
    private JPanel center;  // container for the display
    private JScrollPane chatScroller;    // scrolling pane for the display pane
    // TODO: Change this to a textarea so I can use the append method (along
    // with set caret position)
    private JEditorPane chatDisplay;    // area the results will be displayed

    /* South Panel */
    private JPanel south;   // south panel that will hold the text fields for user input
    private JScrollPane messageScroller;    // scroll in case they type a novel
    private JTextArea message;  // type the message to send in here
    private JButton send;   // send the typed message

    /* Constructor */
    public ChatWindow() {
        super("Name of friend you're chatting with");
        main = new JPanel();
        main.setLayout(new BorderLayout());
        add(main);
        createCenter();
        createSouth();
        main.add(south, BorderLayout.SOUTH);
        main.add(center, BorderLayout.CENTER);
    }


    /* Creation methods */
    /**
     * Creates the center panel which will have the scroll pane that holds the
     * pane to display the chat text.
     */
    private void createCenter() {
        center = new JPanel();
        chatDisplay = new JEditorPane();
        chatScroller = new JScrollPane(chatDisplay);
        chatDisplay.setPreferredSize(new Dimension(580, 330));
        chatDisplay.setEditable(false);
        center.add(chatScroller);
    }

    /**
     * Creates the south panel which will hold the text area for the user to
     * enter their text to send and the send button itself.
     */
    private void createSouth() {
        south = new JPanel();
        message = new JTextArea();
        message.setLineWrap(true);
        message.setPreferredSize(new Dimension(400, 50));
        messageScroller = new JScrollPane(message);
        send = new JButton("Send");
        send.setPreferredSize(new Dimension(75, 50));
        south.add(messageScroller);
        south.add(send);
    }

    /**
     * Set the location and size for the window and make it visible.
     */
    public void showWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE); // close on exit
        setSize(new Dimension(600, 400));        // set the size to be 800x600 (pixels)
        setResizable(false);                     // dont allow resizing so we can not worry about relative sizing
        setLocationRelativeTo(null);             // center the frame (note: after sizing so it is really centered)
        setVisible(true);                        // show it off to the world
    }

    /* Main method */
    public static void main(String[] args) {
        ChatWindow cw = new ChatWindow();
        cw.showWindow();
    }
}
