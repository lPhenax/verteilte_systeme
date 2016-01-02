package abgabe4_andi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 */
public class Server4 {

    private static int port;
    private static ArrayList<User> userList;
    private static final int MAXCONNECTIONS = 5;
    private static ServerSocket listen;
    private Gson gson = new Gson();
    private Gson gsbu = new GsonBuilder().create();
    private Response response = new Response(0, 0, null);
    private String[] antwort;

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

    private void clientBehandlung() {
        try {
            Socket uSkt = listen.accept();
            if (userList.size() < MAXCONNECTIONS) {

                User user = new User(uSkt);
                BufferedReader br = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
                new Thread(() -> warteAufBefehl(user, br, Thread.currentThread())).start();

                clientBehandlung();

            } else {
                antwort[0] = "Alle Pl\u00E3tze sind belegt, sorry";
                Response maxUser = new Response(503, 0, antwort);
//                schickeNachrichtAnClient(uSkt, "Alle Pl\u00E3tze sind belegt, sorry");
                schickeNachrichtAnClient(uSkt, maxUser);

                uSkt.close();
                clientBehandlung();
            }

        } catch (IOException e) {
            System.err.println("Fehler beim Warten auf Verbindungen:" + e);
            System.exit(1);
        }
    }

    private void warteAufBefehl(User user, BufferedReader br, Thread thread) {
        try {
            char[] buffer = new char[160];
            int anzahlZeichen = br.read(buffer, 0, 160); // blockiert bis empfangen Nachricht
            String befehl = new String(buffer, 0, anzahlZeichen);
            Request userBefehl = gson.fromJson(befehl, Request.class);

            if (userBefehl == null) {
                schickeNachrichtAnClient(user.getSocket(), response);
                warteAufBefehl(user, br, thread);
            }

            if (ueberpruefeBefehl(user, userBefehl)) {
                if (isLogedIn(user)) {
                    if (userBefehl.getCommand().startsWith("login ")) {
                        antwort[0] = user.getName() + ", Sie sind bereits eingeloggt ;)";
                        response.setStatusCode(400);
                        response.setSequence(userBefehl.getSequence());
                        response.setRes(antwort);
                        schickeNachrichtAnClient(user.getSocket(), response);
                    } else if (userBefehl.getCommand().equals("help")) {
                        hilfe(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("time")) {
                        getTime(user, userBefehl);
                    } else if (userBefehl.getCommand().startsWith("ls ")) {
                        lsCommand(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("who")) {
                        werIstAllesEingeloggt(user, userBefehl);
                    } else if (userBefehl.getCommand().startsWith("msg ")) {
                        nachricht(user, userBefehl);
                    } else if (userBefehl.getCommand().equals("exit")) {
                        ende(user,null, thread);
                    } else {
                        antwort[0] = "Sie müssen diese Befehle richtig schreiben...\n" +
                                "ls <Pfad>\n" +
                                "msg <Client> <message>\n" +
                                "Hinweis: Vergessen Sie die Leerzeichen nicht ;)";
                        response.setStatusCode(400);
                        response.setSequence(userBefehl.getSequence());
                        response.setRes(antwort);
//                        schickeNachrichtAnClient(user.getSocket(), "Sie müssen diese Befehle richtig schreiben...\n" +
//                                "ls <Pfad>\n" +
//                                "msg <Client> <message>\n" +
//                                "Hinweis: Vergessen Sie die Leerzeichen nicht ;)");
                    }
                } else {
                    if (userBefehl.getCommand().equals("exit")) {
                        ende(user, userBefehl, thread);
                    } else if (userBefehl.getCommand().equals("help")) {
                        hilfe(user, userBefehl);
                    } else if (userBefehl.getCommand().startsWith("login ")) {
                        //logIn(user, befehl.split(" ")[1]);
                        logIn(user, userBefehl);
                    } else {
//                        schickeNachrichtAnClient(user.getSocket(),
//                                "Korrekter Befehl, aber Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.");
                        antwort[0] = "Korrekter Befehl, aber Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
                        response.setStatusCode(401);
                        response.setSequence(userBefehl.getSequence());
                        response.setRes(antwort);
                    }

                }
            } else {
                if (user.getName().equals("undefiniert")) {
//                    schickeNachrichtAnClient(user.getSocket(),
//                            "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.");
                    antwort[0] = "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
                    response.setStatusCode(401);
                    response.setSequence(userBefehl.getSequence());
                    response.setRes(antwort);
                } else {
//                    schickeNachrichtAnClient(user.getSocket(), "Inkorrekter Befehl, bitte überprüfen!");
                    antwort[0] = "Sie müssen diese Befehle richtig schreiben...\n" +
                            "ls <Pfad>\n" +
                            "msg <Client> <message>\n" +
                            "Hinweis: Vergessen Sie die Leerzeichen nicht ;)";
                    response.setStatusCode(400);
                    response.setSequence(userBefehl.getSequence());
                    response.setRes(antwort);
                }
            }
            if (!user.getSocket().isClosed()) {
                warteAufBefehl(user, br, thread);
            }
        } catch (Exception e) {
            System.out.println("User is raus");
            //userList.remove(user);
            ende(user, null,thread);
// System.out.println(user.getName() + " hat ein Request geschickt.");
//            if (isLogedIn(user)) {
//                nachricht(user, "");
//            } else {
//            schickeNachrichtAnClient(user.getSocket(),
//                    "Korrekter Befehl, aber Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.");
            antwort[0] = "Sie m\u00E3ssen sich zuerst mit 'login <username>' einloggen.";
            response.setStatusCode(401);
            response.setSequence(0);
            response.setRes(antwort);
//            }
        }
    }

    private void nachricht(User user, Request befehl) {

        String otherUserName = befehl.getParams()[0];
        Time time = new Time();
        if (user.getName().equals(otherUserName)) {
            antwort[0] = "So geht´s nich, meen Jung! Sie k\u00D6nnen sich nicht selber schreiben..";
            response.setStatusCode(400);
            response.setSequence(0);
            response.setRes(antwort);

//            schickeNachrichtAnClient(user.getSocket(), "So geht´s nich, meen Jung! " +
//                    "Sie k\u00D6nnen sich nicht selber schreiben..");
        } else {
            int index = 0;
            for (User u : userList) {
                System.out.println(otherUserName);
                System.out.println(u.getName());
                if (otherUserName.equals("all")) {
                    if (!(u.getName().equals(user.getName()))) {
//                        schickeNachrichtAnClient(u.getSocket(),
//                                time.getTime() + " " + user.getName() +
//                                        " hat ihnen eine Nachricht geschickt.\n" + befehl + "\n");
                        antwort[0] = time.getTime() + " " + user.getName() + " hat ihnen eine Nachricht geschickt.\n" + befehl.getParams()[1] + "\n";
                        response.setStatusCode(200);
                        response.setSequence(befehl.getSequence());
                        response.setRes(antwort);
                        schickeNachrichtAnClient(u.getSocket(), response);
                        continue;
                    }


                }
                if (u.getName().equals(otherUserName)) {
//                    schickeNachrichtAnClient(u.getSocket(),
//                            time.getTime() + " " + user.getName() +
//                                    " hat ihnen eine Nachricht geschickt.\n" + befehl + "\n");
                    antwort[0] = time.getTime() + " " + user.getName() + " hat ihnen eine Nachricht geschickt.\n" + befehl.getParams()[1] + "\n";
                    response.setStatusCode(200);
                    response.setSequence(befehl.getSequence());
                    response.setRes(antwort);
                    schickeNachrichtAnClient(u.getSocket(), response);
                    break;
                }
                index++;
                if (index == userList.size() || userList.size() != 1) {
                    //schickeNachrichtAnClient(user.getSocket(), "Der angegebene Benutzername existiert nicht.");
                    antwort[0] = "Der angegebene Benutzername existiert nicht.";
                    response.setStatusCode(404);
                    response.setSequence(befehl.getSequence());
                    response.setRes(antwort);
                    schickeNachrichtAnClient(u.getSocket(), response);
                }

            }

        }

    }

//    private static void antworteMitGson(User user, Request req, Thread thread) {
//        try {
//            Response response = new Response();
//            response.setStatusCode(200);
//            response.setSequence(req.getSequence() + 1);
//
//            String[] reqParams = req.getParams();
//            String otherUserName = reqParams[0];
//            response.setRes(req.getParams());
//
//            Gson gsBuilder = new GsonBuilder().create();
//
//
//            if (user.getName().equals(otherUserName)) {
//                schickeNachrichtAnClient(user.getSocket(), "So geht´s nich, meen Jung! " +
//                        "Sie k\u00D6nnen sich nicht selber schreiben..");
//            } else {
//                for (User u : userList) {
//                    if (otherUserName.equals("all")) {
//                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(u.getSocket().getOutputStream()));
//                        gsBuilder.toJson(response, bw);
//                        bw.flush();
//                    } else if (u.getName().equals(otherUserName)) {
//                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(u.getSocket().getOutputStream()));
//                        gsBuilder.toJson(response, bw);
//                        bw.flush();
//                    } else {
//                        schickeNachrichtAnClient(user.getSocket(), "Der angegebene Benutzername existiert nicht.");
//                    }
//                }
//
//            }
//
//            warteAufBefehl(user, thread);
//
//        } catch (IOException e) {
//            System.err.println("Beim Schicken im Json-Format ist uns ein Fehler unterlaufen, sorry :/");
//        }
//    }

    private void lsCommand(User user, Request befehl) {
        //https://forum.ubuntuusers.de/topic/konsolenbefehl-mit-java/
        //http://openbook.rheinwerk-verlag.de/javainsel/javainsel_11_008.html
        //http://mrfoo.de/archiv/315-Verzeichnis-auslesen-in-Java.html
        try {
            String pfad = befehl.getParams()[0];
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "dir");
            builder.directory(new File(pfad));
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
            antwort[0] = stdout;
            response.setStatusCode(200);
            response.setRes(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
        } catch (IOException e) {
            System.out.println("Error");
        }

    }

    private void hilfe(User user, Request userBefehl) {
//        schickeNachrichtAnClient(user.getSocket(), "m\u00F6gliche Befehle:\n\n" +
//                "help                   - Bedienungshilfe wird ausgegeben\n" +
//                "login <username>       - Anmeldung mit Buntzernamen\n" +
//                "time                   - aktuelle Zeit\n" +
//                "ls <Pfad>              - Dateiliste von <Pfad>\n" +
//                "who                    - Liste mit verbundenen Clients\n" +
//                "msg <Client> <message> - sendet Nachricht an <Client >\n" +
//                "exit                   - Beenden und abmelden\n");
        antwort[0] = "m\u00F6gliche Befehle:\n\n" +
                "help                   - Bedienungshilfe wird ausgegeben\n" +
                "login <username>       - Anmeldung mit Buntzernamen\n" +
                "time                   - aktuelle Zeit\n" +
                "ls <Pfad>              - Dateiliste von <Pfad>\n" +
                "who                    - Liste mit verbundenen Clients\n" +
                "msg <Client> <message> - sendet Nachricht an <Client >\n" +
                "exit                   - Beenden und abmelden\n";
        response.setStatusCode(200);
        response.setRes(antwort);
        response.setSequence(userBefehl.getSequence());
        schickeNachrichtAnClient(user.getSocket(), response);
    }

    private void getTime(User user, Request userBefehl) {
        Time time = new Time();
        antwort[0] = "Die aktuelle Zeit ist " + time.getTime();
        response.setStatusCode(200);
        response.setSequence(userBefehl.getSequence());
        response.setRes(antwort);
        schickeNachrichtAnClient(user.getSocket(), response);
        //schickeNachrichtAnClient(user.getSocket(), "Die aktuelle Zeit ist " + time.getTime());
    }

    private void werIstAllesEingeloggt(User user, Request userBefehl) {
        for (User u : userList) {
            antwort[0] = u.getName() + ", ";
            response.setStatusCode(200);
            response.setSequence(userBefehl.getSequence());
            response.setRes(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
            //vllt muss man an dieser Stelle nochmal umschreiben um eine schönere Ausgabe zu erhalten
            //schickeNachrichtAnClient(user.getSocket(), u.getName() + ", ");
        }
    }

    private void logIn(User user, Request userBefehl) {
        boolean istDerUserSchonVorhanden = false;
        String username = userBefehl.getParams()[0];
        for (User u : userList) {
            if (u.getName().equals(username)) istDerUserSchonVorhanden = true;
        }
        if (istDerUserSchonVorhanden) {
            antwort[0] = "Der Benutzername existiert bereits!";
            response.setStatusCode(200);
            response.setSequence(userBefehl.getSequence());
            response.setRes(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
            //schickeNachrichtAnClient(user.getSocket(), "Der Benutzername existiert bereits!");
        } else {
            user.setName(username);
            user.setLogedIn(true);
            userList.add(user);
            System.out.println(" ---- LOGIN ---- Name: " + user.getName() + " IP: " + user.getIp() + " hat sich eingeloggt.");
            for (User u : userList) System.out.println(u.getName());

            antwort[0] = "Hallo " + username + ", Sie sind jetzt eingeloggt.\nViel Spaß mit der Mini Mailbox!";
            response.setStatusCode(200);
            response.setSequence(userBefehl.getSequence());
            response.setRes(antwort);
            schickeNachrichtAnClient(user.getSocket(), response);
//            schickeNachrichtAnClient(user.getSocket(), "Hallo " + username +
//                    ", Sie sind jetzt eingeloggt.\nViel Spaß mit der Mini Mailbox!");
        }
    }

    private void schickeNachrichtAnClient(Socket uSkt, Response nachricht) {
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(uSkt.getOutputStream()));
            pw.print(gsbu.toJson(nachricht));
            //pw.print(nachricht);
            pw.flush();
        } catch (IOException e) {
            System.err.println("Beim Schreiben ist etwas schiefgegangen :/");
        }
    }

    private static boolean isLogedIn(User user) {
        return user.isLogedIn();
    }

    private static boolean ueberpruefeBefehl(User user, Request befehl) {
        System.out.println("user : " + user.getName() + " --- Befehl : " + befehl.getCommand());
        return befehl.getCommand().startsWith("login ") ||
                befehl.getCommand().equals("login") ||
                befehl.getCommand().equals("help") ||
                befehl.getCommand().equals("time") ||
                befehl.getCommand().equals("ls") ||
                befehl.getCommand().startsWith("ls ") ||
                befehl.getCommand().equals("msg") ||
                befehl.getCommand().startsWith("msg ") ||
                befehl.getCommand().equals("who") ||
                befehl.getCommand().equals("exit");

//        return befehl.startsWith("login ") ||
//                befehl.equals("login") ||
//                befehl.equals("help") ||
//                befehl.equals("time") ||
//                befehl.equals("ls") ||
//                befehl.startsWith("ls ") ||
//                befehl.equals("msg") ||
//                befehl.startsWith("msg ") ||
//                befehl.equals("who") ||
//                befehl.equals("exit");
    }

    /**
     * Beendet die Anwendung des Clients.
     */
    private void ende(User user, Request userBefehl,Thread thread) {
        try {
            if(userBefehl == null){
                response.setStatusCode(204);
                response.setSequence(0);
                response.setRes(null);
                schickeNachrichtAnClient(user.getSocket(), response);
            }else{
                antwort[0] = "Bis zum n\u00e4chsten mal " + user.getName() + "!";
                response.setStatusCode(200);
                response.setSequence(userBefehl.getSequence());
                response.setRes(antwort);
                schickeNachrichtAnClient(user.getSocket(), response);
            }

            //schickeNachrichtAnClient(user.getSocket(), "Bis zum n\u00e4chsten mal " + user.getName() + "!");
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
