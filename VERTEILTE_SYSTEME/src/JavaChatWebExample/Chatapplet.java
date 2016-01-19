package JavaChatWebExample;

import java.applet.Applet;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Chatapplet extends Applet implements Runnable {

    public static final int PORT = 8090;
    Socket socket;
    DataInputStream in;
    PrintStream out;
    TextField inputfield;
    TextArea outputarea;
    Thread thread;

    public void init()
    {
        inputfield = new TextField();
        outputarea = new TextArea();
        outputarea.setFont( new Font("Dialog", Font.PLAIN, 12));
        outputarea.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add("South", inputfield);
        this.add("Center", outputarea);
        this.setBackground(Color.lightGray);
        this.setForeground(Color.black);
        inputfield.setBackground(Color.white);
        outputarea.setBackground(Color.white);
    }

    public void start()
    {
        try
        {
            socket = new Socket(this.getCodeBase().getHost(), PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e)
        {
            this.showStatus(e.toString());
            say("Verbindung zum Server fehlgeschlagen!");
            System.exit(1);
        }

        say("Verbindung zum Server aufgenommen...");

        if (thread == null)
        {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    public void stop()
    {
        try
        {
            socket.close();
        } catch (IOException e)
        {
            this.showStatus(e.toString());
        }

        if ((thread !=null) && thread.isAlive())
        {
            thread.stop();
            thread = null;
        }
    }

    @Override
    public void run()
    {
        String line;

        try
        {
            while(true)
            {
                line = in.readLine();
                if(line!=null)
                    outputarea.appendText(line+'\n' );
            }
        } catch (IOException e) { say("Verbindung zum Server abgebrochen"); }
    }

    public boolean action(Event e, Object what)
    {
        if (e.target==inputfield)
        {
            String inp=(String) e.arg;
            out.println(inp);
            inputfield.setText("");
            return true;
        }
        return false;
    }

    public void say(String msg)
    {
        outputarea.appendText("*** "+msg+" ***\n");
    }
}
