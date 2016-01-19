package JavaChatWebExample;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Connection extends Thread {

    protected Socket client;
    protected DataInputStream in;
    protected PrintStream out;
    protected Chatserver server;

    public Connection(Chatserver server, Socket client)
    {
        this.server=server;
        this.client=client;

        try
        {
            in = new DataInputStream(client.getInputStream());
            out = new PrintStream(client.getOutputStream());
        } catch (IOException e)
        {
            try { client.close(); } catch (IOException e2) {} ;
            System.err.println("Fehler beim Erzeugen der Streams: " + e);
            return;
        }

        this.start();
    }

    public void run()
    {
        String line;

        try
        {
            while(true)
            {
                line=in.readLine();
                if(line!=null)
                    server.broadcast(line);
            }
        } catch (IOException e)
        {
            System.out.println("Fehler:" + e);
        }
    }
}
