package abgabe4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 */
public class Server4 {

    private static int port;
    private static ArrayList<User> userList;
    private static final int MAXCONNECTIONS = 5;
    private static ServerSocket listen;

    public static void main(String[] args) {
        new Server4();
    }

    public Server4() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Geben Sie bitte eine Portnummer ein (z.B. 8090) \n" +
                    "Falls nichts eingeben wird, wird der Port auf 8090 gesetzt.\n" +
                    "$> ");
            String portNr = br.readLine();
            if (portNr.isEmpty() || portNr.length() <= 0 || portNr.equals("")) {
                port = 8090;
            } else {
                port = Integer.parseInt(portNr);
            }
            listen = new ServerSocket(port);
            userList = new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Der Server konnte nicht gestartet werden!");
        }
        System.out.println("Der Server ist online!");
        clientBehandlung();
    }

    private static void clientBehandlung() {
        try {
            if (userList.size() < MAXCONNECTIONS) {
                Socket uSkt = listen.accept();

                User user = new User(uSkt);
                BufferedReader br = new BufferedReader(new InputStreamReader(uSkt.getInputStream()));

                new Thread(() -> warteAufBefehl(user, br, Thread.currentThread())).start();

                clientBehandlung();

            } else {
                //TODO: Verbindung wird verweigert + privateNachrichtAnClient mit alle Server-Slots sind schon belegt
                System.out.println(" --- Server ist voll.");

            }

        } catch (IOException e) {
            System.err.println("Fehler beim Warten auf Verbindungen:" + e);
            System.exit(1);
        }
    }

    private static void warteAufBefehl(User user, BufferedReader br, Thread thread) {
        try {
            char[] buffer = new char[160];
            int anzahlZeichen = br.read(buffer, 0, 160); // blockiert bis empfangen Nachricht
            String befehl = new String(buffer, 0, anzahlZeichen);

            if (befehl.isEmpty()) {
                schickeNachrichtAnClient(user.getSocket(), "");
                warteAufBefehl(user, br, thread);
            }

            if (ueberpruefeBefehl(user, befehl)) {
                if (isLogedIn(user)) {
                    if (befehl.startsWith("login ")) {
                        schickeNachrichtAnClient(user.getSocket(), user.getName() + ", Sie sind bereits eingeloggt ;)");
                    } else if (befehl.equals("help")) {
                        hilfe(user);
                    } else if (befehl.equals("time")) {
                        getTime(user);
                    } else if (befehl.startsWith("ls ")) {
                        lsCommand(user, befehl);
                    } else if (befehl.equals("who")) {
                        werIstAllesEingeloggt(user);
                    } else if (befehl.startsWith("msg ")) {
                        nachricht(user, befehl);
                    } else if (befehl.equals("exit")) {
                        ende(user, thread);
                    } else {
                        schickeNachrichtAnClient(user.getSocket(), "Sie müssen diese Befehle richtig schreiben...\n" +
                                "ls <Pfad>\n" +
                                "msg <Client> <message>\n" +
                                "Hinweis: Vergessen Sie die Leerzeichen nicht ;)");
                    }
                } else {
                    if (befehl.equals("exit")) {
                        ende(user, thread);
                    } else if (befehl.equals("help")) {
                        hilfe(user);
                    } else if (befehl.startsWith("login ")) {
                        logIn(user, befehl.split(" ")[1]);
                    } else {
                        schickeNachrichtAnClient(user.getSocket(),
                                "Korrekter Befehl, aber Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.");
                    }

                }
            } else {
                if (user.getName().equals("undefiniert")) {
                    schickeNachrichtAnClient(user.getSocket(),
                            "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.");
                } else {
                    schickeNachrichtAnClient(user.getSocket(), "Inkorrekter Befehl, bitte überprüfen!");
                }
            }
            if (user.getSocket() != null) {
                warteAufBefehl(user, br, thread);
            }
        } catch (IOException e) {
            ende(user, thread);
        }
    }

    //TODO: Message-Format als JSON-Obj
    private static void nachricht(User user, String msg) {
        System.out.println(msg);
        String otherUserName = msg.split(" ")[1];
        System.out.println(otherUserName);
        msg = msg.split(" ")[2];
        System.out.println(msg);
        if (user.getName().equals(otherUserName)) {
            schickeNachrichtAnClient(user.getSocket(), "So geht´s nich, meen Jung! " +
                    "Sie k\u00D6nnen sich nicht selber schreiben..");
        } else {
            for (User u : userList) {
                if (otherUserName.equals("all")) {
                    schickeNachrichtAnClient(u.getSocket(), msg);
                } else if (u.getName().equals(otherUserName)) {
                    schickeNachrichtAnClient(u.getSocket(), msg);
                } else {
                    schickeNachrichtAnClient(user.getSocket(), "Der angegebene Benutzername existiert nicht.");
                }
            }
        }
    }


    private static void lsCommand(User user, String befehl) {
        //https://forum.ubuntuusers.de/topic/konsolenbefehl-mit-java/
        //http://openbook.rheinwerk-verlag.de/javainsel/javainsel_11_008.html
        //http://mrfoo.de/archiv/315-Verzeichnis-auslesen-in-Java.html
        try {
            befehl = befehl.split(" ")[1];
//        befehl = befehl.replace("\\", "");


//        File dir = new File(befehl);
//        File[] fileList = dir.listFiles();
//        for(File f : fileList) {
//            System.out.println(f.getName());
//        }
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "dir");
            builder.directory(new File(befehl));
            Process p = builder.start();

            BufferedReader str = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String cache;
            String stdout = "";
            String stderr = "";

            while ((cache = str.readLine()) != null) {
                stdout += cache + "\n";
            }

            while ((cache = err.readLine()) != null) {
                stderr += cache + "\n";
            }

            str.close();
            err.close();

//        System.out.println(stdout);
            System.out.println(stderr);
            schickeNachrichtAnClient(user.getSocket(), stdout);
        } catch (IOException e) {
            System.out.println("Error");
        }

    }

    private static void hilfe(User user) {
        schickeNachrichtAnClient(user.getSocket(), "m\u00F6gliche Befehle:\n\n" +
                "help                   - Bedienungshilfe wird ausgegeben\n" +
                "login <username>       - Anmeldung mit Buntzernamen\n" +
                "time                   - aktuelle Zeit\n" +
                "ls <Pfad>              - Dateiliste von <Pfad>\n" +
                "who                    - Liste mit verbundenen Clients\n" +
                "msg <Client> <message> - sendet Nachricht an <Client >\n" +
                "exit                   - Beenden und abmelden\n");
    }

    private static void getTime(User user) {
        Time time = new Time();
        schickeNachrichtAnClient(user.getSocket(), "Die aktuelle Zeit ist " + time.getTime());
    }

    private static void werIstAllesEingeloggt(User user) {
        for (User u : userList) {
            //vllt muss man an dieser Stelle nochmal umschreiben um eine schönere Ausgabe zu erhalten
            schickeNachrichtAnClient(user.getSocket(), u.getName());
        }
    }

    private static void logIn(User user, String username) {
        boolean istDerUserSchonVorhanden = false;
        for (User u : userList) {
            if (u.getName().equals(username)) istDerUserSchonVorhanden = true;
        }
        if (istDerUserSchonVorhanden) {
            schickeNachrichtAnClient(user.getSocket(), "Der Benutzername existiert bereits!");
        } else {
            user.setName(username);
            user.setLogedIn(true);
            userList.add(user);
            System.out.println(" ---- LOGIN ---- Name: " + user.getName() + " IP: " + user.getIp() + " hat sich eingeloggt.");
            for (User u : userList) System.out.println(u.getName());
            schickeNachrichtAnClient(user.getSocket(), "Hallo " + username +
                    ", Sie sind jetzt eingeloggt.\nViel Spaß mit der Mini Mailbox!");
        }
    }

    private static void schickeNachrichtAnClient(Socket uSkt, String nachricht) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(uSkt.getOutputStream()));
            pw.print(nachricht);
            pw.flush();
        } catch (IOException e) {
            System.err.println("Beim Schreiben ist etwas schiefgegangen :/");
        }
    }

    private static boolean isLogedIn(User user) {
        return user.isLogedIn();
    }

    private static boolean ueberpruefeBefehl(User user, String befehl) {
        System.out.println("user : " + user.getName() + " --- Befehl : " + befehl);
        return befehl.startsWith("login ") ||
                befehl.equals("login") ||
                befehl.equals("help") ||
                befehl.equals("time") ||
                befehl.equals("ls") ||
                befehl.startsWith("ls ") ||
                befehl.equals("who") ||
                befehl.equals("msg") ||
                befehl.startsWith("msg ") ||
                befehl.equals("exit");
    }

    /**
     * Beendet die Anwendung des Clients.
     */
    private static void ende(User user, Thread thread) {
        try {
            schickeNachrichtAnClient(user.getSocket(), "Bis zum n\u00e4chsten mal " + user.getName() + "!");
            userList.remove(user);
            user.getSocket().close();
            for (User u : userList) System.out.println(u.getName());
            if ((thread != null) && thread.isAlive()) {
                try {
                    System.out.println(user.getName() + " hat sich abgemeldet! (1)");
                    thread.stop();
                } catch (Exception e) {
                    System.out.println(user.getName() + " hat sich abgemeldet! (2)");
                }
            } else {
                System.out.println("thread == null");
            }
        } catch (IOException e) {
            System.err.println("Beim Schließen der Verbindung ist etwas schiefgegeangen :/");
        }
    }
}
