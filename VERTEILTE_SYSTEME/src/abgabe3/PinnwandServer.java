package abgabe3;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by Andreas & Daniel on 22.11.2015.
 */
public class PinnwandServer extends UnicastRemoteObject implements Pinnwand {

    private static final long serialVersionUID = 1L;
    private static final String nameOfService = "Pinnwand";
    private static final String Password = "0815";
    private static final int maxNumMessages = 20;
    private static final int messageLifetime = 360000;
    private static final int maxLengthMessage = 160;
    private static int nfi = 0;
    private static String[] messageMemory = new String[maxNumMessages];
    private static ArrayList<String> sessionMemory = new ArrayList<>();
    private static final long sessionLifetime = 6;

    protected PinnwandServer() throws RemoteException {
        super();
    }

    @Override
    public int login(String password) throws RemoteException, ServerNotActiveException {
        if (password.equals(Password)) {
            session();
            return 1;
        }
        return -1;
    }

    private void session() throws ServerNotActiveException {
        String user = RemoteServer.getClientHost();
        System.out.println(user + " hat sich eingeloggt.");
        sessionMemory.add(user);
        countDown(user);
    }

    private void countDown(String user) {
        new Thread(() -> {
            try {
                for (long i = sessionLifetime; i >= 0; i--) {
                    Thread.sleep(1000);
                    /*
                    Die Sessions des Users laufen jetzt nur 6s(bisher).
                    TODO: Updater einbauen.
                     */
                    if (i == 0) {
                        sessionMemory.remove(user);
                        System.out.println(user + " wurde ausgeloggt.");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int getMessageCount() throws RemoteException {
        /*for(int i = 0; i < sessionMemory.size(); i++){
            try {
                if(sessionMemory.get(i).equals(RemoteServer.getClientHost()))
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
        }*/
        int msgCount = 0;
        for (String msg : messageMemory) {
            if (msg != null) msgCount++;
        }
        return msgCount;
    }

    @Override
    public String[] getMessages() throws RemoteException {
        return messageMemory;
    }

    @Override
    public String getMessage(int index) throws RemoteException {
        return messageMemory[index];
    }

    @Override
    public boolean putMessage(String msg) throws RemoteException {
        if (msg.length() <= maxLengthMessage && getMessageCount() <= maxNumMessages && nfi < maxNumMessages) {
            messageMemory[nfi] = msg;
            nfi++;
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(messageLifetime);
                    for (int i = 0; i < messageMemory.length - 1; i++) {
                        messageMemory[i] = messageMemory[i + 1];
                        if (i == messageMemory.length - 2) {
                            messageMemory[i + 1] = null;
                        }
                    }
                    //System.arraycopy(messageMemory, 0, messageMemory, 1, messageMemory.length - 1);
                    nfi--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            t.start();
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        try {
            System.setSecurityManager(new SecurityManager());
            PinnwandServer pinnwandServer = new PinnwandServer();
            System.out.println("Server wird rmiregistry bekannt gemacht");
            Naming.rebind(nameOfService, pinnwandServer);
            System.out.println("Server gestartet");
        } catch (MalformedURLException | RemoteException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

