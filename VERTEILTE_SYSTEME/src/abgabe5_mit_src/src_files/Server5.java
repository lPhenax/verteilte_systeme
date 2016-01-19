package abgabe5_mit_src.src_files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 */
public class Server5 {

    private static int port;
    private static ArrayList<User5> userList;
    private static final int MAXCONNECTIONS = 5;
    private static ServerSocket listen;
    private Gson gson = new Gson();
    private Gson gsbu = new GsonBuilder().create();
    private Response5 response = new Response5(0, 0, null);
    private String[] antwort = new String[1];

    public static void main(String[] args) {
        new Server5();
    }

    public Server5() {
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

    private void clientBehandlung() {
        try {
            Socket uSkt = listen.accept();
            antwort[0] = "Willkommen bei der Min-Mailbox.\nVerbindung steht.";
            response.setStatuscode(200);
            response.setSequence(0);
            response.setResponse(antwort);
            schickeNachrichtAnClient(uSkt, response);
            if (userList.size() < MAXCONNECTIONS) {

                User5 user = new User5(uSkt);
                BufferedReader br = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
                new Thread(() -> warteAufBefehl(user, br, Thread.currentThread())).start();

                clientBehandlung();

            } else {
                antwort[0] = "Alle Pl\u00E3tze sind belegt, sorry";
                Response5 maxUser = new Response5(503, 0, antwort);
                schickeNachrichtAnClient(uSkt, maxUser);

                uSkt.close();
                clientBehandlung();
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Warten auf Verbindungen:" + e);
            System.exit(1);
        }
    }

    private void warteAufBefehl(User5 user, BufferedReader br, Thread thread) {
        System.out.println("ich bin am warten");
        try {
            char[] buffer = new char[2000];
            int anzahlZeichen = br.read(buffer, 0, 2000); // blockiert bis empfangen Nachricht
            String befehl = new String(buffer, 0, anzahlZeichen);
            Request5 userBefehl = gson.fromJson(befehl, Request5.class);
//            System.out.println(userBefehl);
//            System.out.println(userBefehl.getParams()[0]);

            if (ueberpruefeBefehl(user, userBefehl)) {
                if (isLogedIn(user)) {
                    if (userBefehl.getCommand().equals("login")) {
                        antwort[0] = user.getName() + ", Sie sind bereits eingeloggt ;)";
                        response.setStatuscode(400);
                        response.setSequence(userBefehl.getSequence());
                        response.setResponse(antwort);
                        schickeNachrichtAnClient(user.getSocket(), response);
                    } else if (userBefehl.getCommand().equals("help")) {
                        hilfe(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("time")) {
                        getTime(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("ls")) {
                        lsCommand(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("who")) {
                        werIstAllesEingeloggt(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("msg")) {
                        nachricht(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("exit")) {
                        ende(user, userBefehl, thread);
                    } else {
                        antwort[0] = "Sie müssen diese Befehle richtig schreiben...\n" +
                                "ls <Pfad>\n" +
                                "msg <Client> <message>\n" +
                                "Hinweis: Vergessen Sie die Leerzeichen nicht ;)";
                        response.setStatuscode(400);
                        response.setSequence(userBefehl.getSequence());
                        response.setResponse(antwort);
                        schickeNachrichtAnClient(user.getSocket(), response);
                    }
                } else {
                    if (userBefehl.getCommand().equals("exit")) {
                        ende(user, userBefehl, thread);
                    } else if (userBefehl.getCommand().equals("help")) {
                        hilfe(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("login")) {
                        logIn(user, userBefehl);
                    } else {
                        antwort[0] = "Korrekter Befehl, aber Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
                        response.setStatuscode(401);
                        response.setSequence(userBefehl.getSequence());
                        response.setResponse(antwort);
                        schickeNachrichtAnClient(user.getSocket(), response);
                    }

                }
            } else {
                if (user.getName().equals("undefiniert")) {
                    antwort[0] = "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
                    response.setStatuscode(401);
                    response.setSequence(userBefehl.getSequence());
                    response.setResponse(antwort);
                    schickeNachrichtAnClient(user.getSocket(), response);

                } else {
                    antwort[0] = "Sie müssen diese Befehle richtig schreiben...\n" +
                            "ls <Pfad>\n" +
                            "msg <Client> <message>\n" +
                            "Hinweis: Vergessen Sie die Leerzeichen nicht ;)";
                    response.setStatuscode(400);
                    response.setSequence(userBefehl.getSequence());
                    response.setResponse(antwort);
                    schickeNachrichtAnClient(user.getSocket(), response);
                }
            }
            if (!user.getSocket().isClosed()) {
                warteAufBefehl(user, br, thread);
            }
        } catch (Exception e) {
            System.out.println("User '" + user.getName() + "' ist raus.");
            System.out.println();
            userList.remove(user);
            ende(user, null, thread);
            antwort[0] = "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
            response.setStatuscode(401);
            response.setSequence(0);
            response.setResponse(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
        }
    }

    private void nachricht(User5 user, Request5 befehl) {
//        System.out.println("User '" + user.getName() + "' hat folgenden Befehl gesendet: '" + befehl + "'");
//        System.out.println("befehl.getParams()[0]: " + befehl.getParams()[0]);

        String otherUserName = befehl.getParams()[0].split(" ")[0];
        String msg = befehl.getParams()[0].replace(otherUserName + " ", "");
        Time5 time = new Time5();
        boolean all = false;
        if (user.getName().equals(otherUserName)) {
            antwort[0] = "So geht´s nich, meen Jung! Sie k\u00D6nnen sich nicht selber schreiben..";
            response.setStatuscode(400);
            response.setSequence(0);
            response.setResponse(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
        } else {
            for (User5 u : userList) {
                System.out.println(otherUserName);
                System.out.println(u.getName());
                if (otherUserName.equals("all")) {
                    if (!(u.getName().equals(user.getName()))) {
                        antwort[0] = time.getTime() + " " + user.getName() + " hat ihnen eine Nachricht geschickt.\n" +
                                msg + "\n";
                        response.setStatuscode(200);
                        response.setSequence(befehl.getSequence());
                        response.setResponse(antwort);
                        schickeNachrichtAnClient(u.getSocket(), response);
                        continue;
                    }

                    all = true;
                }
                if(!all){
                    if (u.getName().equals(otherUserName)) {
                        if(isLogedIn(u)){
                            antwort[0] = time.getTime() + " " + user.getName() + " hat ihnen eine Nachricht geschickt.\n" +
                                    msg + "\n";
                            response.setStatuscode(200);
                            response.setSequence(befehl.getSequence());
                            response.setResponse(antwort);
                            schickeNachrichtAnClient(u.getSocket(), response);
                        } else {
                            antwort[0] = "Der angegebene Benutzername existiert nicht bzw. ist nicht eingeloggt.";
                            response.setStatuscode(404);
                            response.setSequence(befehl.getSequence());
                            response.setResponse(antwort);
                            schickeNachrichtAnClient(u.getSocket(), response);
                        }

                        break;
                    }
                }

            }

        }

    }

    private void lsCommand(User5 user, Request5 befehl) {
//        System.out.println(user.getName());
//        System.out.println(befehl);

        String pfad = befehl.getParams()[0];
//        System.out.println(befehl.getParams()[0]);
        if (pfad.equals("")|| pfad.isEmpty()) {
            pfad = "c:\\";
        }
        System.out.println("pfad: " +pfad);
        File file = new File(pfad);
        File[] fileArray = file.listFiles();
        try{
            String[] liste = new String[fileArray.length];
            for (int i = 0; i <= fileArray.length - 1; i++) {
//                if (fileArray[i].isDirectory()) {
                    liste[i] = fileArray[i].toString();
//                }
            }
            response.setSequence(befehl.getSequence());
            response.setStatuscode(200);
            response.setResponse(liste);
            schickeNachrichtAnClient(user.getSocket(), response);
        } catch (Exception ex){
            response.setSequence(befehl.getSequence());
            response.setStatuscode(200);
            String[] liste = new String[1];
            liste[0] = "Diesen Pfad gibts nicht.";
            response.setResponse(liste);
            schickeNachrichtAnClient(user.getSocket(), response);
        }


    }

    private void hilfe(User5 user, Request5 userBefehl) {
        antwort[0] = "m\u00F6gliche Befehle:\n\n" +
                "help                   - Bedienungshilfe wird ausgegeben\n" +
                "login <username>       - Anmeldung mit Buntzernamen\n" +
                "time                   - aktuelle Zeit\n" +
                "ls <Pfad>              - Dateiliste von <Pfad>\n" +
                "who                    - Liste mit verbundenen Clients\n" +
                "msg <Client> <message> - sendet Nachricht an <Client >\n" +
                "exit                   - Beenden und abmelden\n";
        response.setStatuscode(200);
        response.setResponse(antwort);
        response.setSequence(userBefehl.getSequence());
        schickeNachrichtAnClient(user.getSocket(), response);
    }

    private void getTime(User5 user, Request5 userBefehl) {
        Time5 time = new Time5();
        antwort[0] = "Die aktuelle Zeit ist " + time.getTime();
        response.setStatuscode(200);
        response.setSequence(userBefehl.getSequence());
        response.setResponse(antwort);
        schickeNachrichtAnClient(user.getSocket(), response);
    }

    private void werIstAllesEingeloggt(User5 user, Request5 userBefehl) {
        String uNameList = "";
        for (User5 u : userList) {
            if(uNameList.equals("") || uNameList.isEmpty()){
                uNameList = uNameList + u.getName();
            } else {
                uNameList = uNameList + ", " + u.getName();
            }
        }
        antwort[0] = uNameList;
        response.setStatuscode(200);
        response.setSequence(userBefehl.getSequence());
        response.setResponse(antwort);
        schickeNachrichtAnClient(user.getSocket(), response);
    }

    private void logIn(User5 user, Request5 userBefehl) {
        boolean istDerUserSchonVorhanden = false;
        String username = userBefehl.getParams()[0];
        for (User5 u : userList) {
            if (u.getName().equals(username)) istDerUserSchonVorhanden = true;
        }
        if (istDerUserSchonVorhanden) {
            antwort[0] = "Der Benutzername existiert bereits!";
            response.setStatuscode(200);
            response.setSequence(userBefehl.getSequence());
            response.setResponse(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
        } else {
            user.setName(username);
            user.setLogedIn(true);
            userList.add(user);
            System.out.println(" ---- LOGIN ---- Name: " + user.getName() + " IP: " + user.getIp() + " hat sich eingeloggt.");
            for (User5 u : userList) System.out.println(u.getName());

            antwort[0] = "Hallo " + username + ", Sie sind jetzt eingeloggt.\nViel Spaß mit der Mini Mailbox!";
            response.setStatuscode(200);
            response.setSequence(userBefehl.getSequence());
            response.setResponse(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);

        }
    }

    private void schickeNachrichtAnClient(Socket uSkt, Response5 nachricht) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(uSkt.getOutputStream()));
            pw.print(gsbu.toJson(nachricht));
            pw.flush();
        } catch (IOException e) {
            System.err.println("Beim Schreiben ist etwas schiefgegangen :/");
        }
    }

    private static boolean isLogedIn(User5 user) {
        return user.isLogedIn();
    }

    private static boolean ueberpruefeBefehl(User5 user, Request5 befehl) {
        System.out.println("user : " + user.getName() + " --- Befehl : " + befehl.getCommand());
        return befehl.getCommand().equals("login") ||
                befehl.getCommand().equals("help") ||
                befehl.getCommand().equals("time") ||
                befehl.getCommand().equals("ls") ||
                befehl.getCommand().equals("msg") ||
                befehl.getCommand().equals("who") ||
                befehl.getCommand().equals("exit");
    }

    /**
     * Beendet die Anwendung des Clients.
     */
    private void ende(User5 user, Request5 userBefehl, Thread thread) {
        try {
            antwort[0] = "Bis zum n\u00e4chsten mal " + user.getName() + "!";
            response.setStatuscode(200);
            response.setSequence(userBefehl.getSequence());
            response.setResponse(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
            userList.remove(user);
            user.getSocket().close();
            for (User5 u : userList) System.out.println(u.getName());
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
