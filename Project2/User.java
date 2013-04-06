import java.net.InetAddress;
import java.net.Socket;

/**
 * Describes the important features of a user that will be required to
 * have clients connect to other clients.
 * @author Stephen Ranshous
 */
public class User {
    private InetAddress userAddress;
    private int userPort;
    private String name;

    /**
     * Create a User with the given address and port that they connected on.
     * @param name The name of the user
     * @param userAddress The InetAddress of the user. This will mostly be used
     * for getting their IP.
     * @param userPort The port the user connected on.
     */
    public User(String name, InetAddress userAddress, int userPort) {
        this.userAddress = userAddress;
        this.userPort = userPort;
    }

    /**
     * Get the InetAddress of this user.
     * @return The InetAddress for the user.
     */
    public InetAddress getAddress() {
        return userAddress;
    }

    /**
     * Get the port this user connected on.
     * @return The port they connected on.
     */
    public int getPort() {
        return userPort;
    }

    public String getName() {
        return name;
    }
}
