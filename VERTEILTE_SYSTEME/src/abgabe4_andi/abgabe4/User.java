package abgabe4_andi.abgabe4;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 *
 */
public class User {

    private Socket socket;
    private InetAddress ip;
    private String name;
    private boolean isLogedIn;

    User(Socket skt) {
        this.socket = skt;
        this.ip = socket.getInetAddress();
        this.name = "undefiniert";
        this.isLogedIn = false;
    }


    public Socket getSocket() { return socket; }

    public InetAddress getIp() {
        return ip;
    }

    public boolean isLogedIn() {
        return isLogedIn;
    }

    public void setLogedIn(boolean logedIn) {
        isLogedIn = logedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
