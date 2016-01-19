package abgabe5_mit_src.src_files;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 *
 */
public class User5 {

    private Socket socket;
    private InetAddress ip;
    private String name;
    private boolean isLogedIn;

    User5(Socket skt) {
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
