package abgabe4_andi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 */
public class Client4 {

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private Gson gson = new Gson();
    private Gson gsbu = new GsonBuilder().create();
    private String[] parms;
    private Request req;


    public static void main(String[] args) throws IOException {

        Client4 client = new Client4();
        client.clientKlasse();

    }

    /**
     * clientKlasse meldet sich an einem Server an und l�sst Nachricht erstellen.
     */
    void clientKlasse() {

        Socket skt = null;
        try {
            System.out.println("Geben Sie bitte eine g\u00FCltige Ip an.");
            System.out.println("Keine Eingabe bedeutet Localhost.");
            System.out.print("$> ");
            String ip = br.readLine();
            System.out.println("Geben Sie bitte eine Portnummer an.");
            System.out.println("Keine Eingabe bedeutet 8090.");
            System.out.print("$> ");
            String portEingabe = br.readLine();

            int port = 0;
            int socket = 0;
            if (!ip.isEmpty() && !portEingabe.isEmpty()) {
                port = Integer.parseInt(portEingabe);
                socket = 1;
            }
            if (ip.isEmpty() && !portEingabe.isEmpty()) {
                port = Integer.parseInt(portEingabe);
                socket = 2;
            }
            if (!ip.isEmpty() && portEingabe.isEmpty()) socket = 3;

            switch (socket) {
                case 1:
                    skt = new Socket(ip, port);
                    break;
                case 2:
                    skt = new Socket("127.0.0.1", port);
                    break;
                case 3:
                    skt = new Socket(ip, 8090);
                    break;
                default:
                    skt = new Socket("127.0.0.1", 8090);
                    break;
            }
            System.out.println("Sie versuchen sich mit der Adresse " + skt.getInetAddress() + ":" + skt.getPort() +
                    " bei der Mini Mailbox anzumelden.");
        } catch (IOException e) {
            System.err.println("Der Client konnte sich nicht anmelden, da der Server noch nich on ist!");
            clientKlasse();
        }

        final Socket finalSkt = skt;
        new Thread(() -> leseNachrichten(finalSkt, Thread.currentThread())).start();

        erstelleNachricht(skt);

        try {
            assert skt != null;
            skt.close();
        } catch (IOException e) {
            System.err.println("Der Client konnte nicht geschlossen werden!");
        }
    }

    /**
     * erstelleNachricht l�sst Konsolenbefehle erstellen und die diese an einen Server senden
     * und erwartet eine Antwort.
     *
     * @param skt Serveradresse
     */
    void erstelleNachricht(Socket skt) {
        try {

            Request request = erstelleBefehl(skt);
            schickeNachrichten(skt, request);
            erstelleNachricht(skt);
//            String befehl = erstelleBefehl(skt);
//            schickeNachrichten(skt, befehl);
//            erstelleNachricht(skt);
        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println("Fehlercode: -3\n" +
                    "Grund: 'Der Server ist down!'");
            System.out.println("Soll das Programm beendet werden? (Y/N)");
            try {
                String programmBeenden = br.readLine();
                System.out.println(programmBeenden);
                if (programmBeenden.equalsIgnoreCase("y")) {
                    schickeNachrichten(skt, new Request("exit", null));
                    br.close();
                }
                if (programmBeenden.equalsIgnoreCase("n")) erstelleNachricht(skt);
                System.out.println("_Die Eingabe wurde jetzt als 'nein' interpretiert und auf 'erstelleNachricht'" +
                        " zur\u00FCckgesetzt!");
                br.close();
            } catch (IOException e) {
                System.err.println("Beim Schreiben in die Konsole ist etwas schiefgegangen...sorry...");
            }
        }
    }

    /**
     * erstelleBefehl erstellt Konsolenbefehle und l�sst �berpr�fen.
     *
     * @param skt Serveradresse
     * @return eine Zahl die an dem Server �bergeben werden soll
     */
    private Request erstelleBefehl(Socket skt) {
        System.out.print("$> ");
        String nachricht;
        try {
            nachricht = br.readLine();
            if (nachricht.isEmpty()) {
                erstelleNachricht(skt);
            }
            req = new Request();
            String befehl = nachricht.split(" ")[0];
            req.setCommand(befehl);
            parms = new String[1];
            parms[0] = nachricht.replace(befehl + " ", "");
//            for(int i = 0; i < nachricht.length(); i++){
//                parms[i] = nachricht.split(" ")[i+1];
//            }

            req.setParams(parms);

        } catch (IOException ioEx) {
            System.err.println("Beim Schreiben in die Konsole ist etwas schiefgegangen...sorry...");
        }
        return req;
    }

    /**
     * schickeNachrichten soll Nachrichten vom Client an den Server senden.
     *
     * @param skt Serveradresse
     * @throws IOException wird in erstelleNachricht gefangen und ausgewertet
     */
    void schickeNachrichten(Socket skt, Request nachricht) {
        try {
//            String command = nachricht.split(" ")[0];
//            if(command.equals("msg")){
//                String empfänger = nachricht.split(" ")[1];
//                System.out.println(nachricht);
//                nachricht = nachricht.replace("msg ", "");
//                System.out.println(nachricht);
//                nachricht = nachricht.replace(empfänger+" ", "");
//                System.out.println(nachricht);
//
//                String[] params = {empfänger, nachricht};
//
//                Request req = new Request();
//                req.setCommand(command);
//                req.setParams(params);
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));
//                Gson gsBuilder = new GsonBuilder().create();
//                gsBuilder.toJson(req, bw);
//                bw.close();
//
//            } else {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(skt.getOutputStream()));
            printWriter.print(gsbu.toJson(nachricht));
            printWriter.flush();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * leseNachrichten liest die Nachrichten vom Server ein.
     *
     * @param skt Serveradresse
     * @return die Antwort vom Server
     * @throws IOException wird in erstelleNachricht gefangen und ausgewertet
     */
    void leseNachrichten(Socket skt, Thread thread) {
        String mail = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            char[] buffer = new char[2000];
            int anzahlZeichen = br.read(buffer, 0, 2000); // blockiert bis empfangen Nachricht
            mail = new String(buffer, 0, anzahlZeichen);
            Response response = gson.fromJson(mail, Response.class);
            System.out.println("Antwort des Servers: " + response);
            if (response.getRes() != null) {

                if (response.getRes()[0].startsWith("Bis zum n")) {
                    System.out.println("abgemeldet..");
//                thread.stop();
                    Runtime.getRuntime().exit(0);
                }

            } else {
                System.out.println("leer");
            }
            /**
             TODO: Hier läuft was schief
             Der Thread scheißt ne Exception
             */
            leseNachrichten(skt, thread);

        } catch (IOException e) {
//            try {
//                System.out.println("Er ist wieder da!");
//                BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
//                Gson gson = new Gson();
//                Response response = gson.fromJson(br, Response.class);
//                System.out.println(response);
//                String[] resParams = response.getRes();
//                System.out.println(resParams);
//                String msg = resParams[1];
//                System.out.println(msg);
//            } catch (IOException e1) {
            System.out.println("Beim Lesen ist etwas schiefgegangen oder Sie haben sich abgemeldet :/");
            System.out.println("Verbindung zum Server verloren, Anwendung wird beendet.");
//            thread.stop();
            Runtime.getRuntime().exit(0);
//            }
        }
    }
}
