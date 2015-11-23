package abgabe2;

import java.io.*;
import java.net.Socket;

/**
 * Created by Burger & Schleußner on 26.10.2015.
 */
public class Client3 {
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        Client3 client = new Client3();
        client.clientKlasse();

    }

    /**
     * clientKlasse meldet sich an einem Server an und lässt Nachricht erstellen.
     */
    void clientKlasse() {

        Socket skt = null;
        try {
            System.out.println("Geben Sie bitte eine g\u00FCltige Ip an.");
            System.out.println("Keine Eingabe bedeutet Localhost.");
            System.out.print("$> ");
            String ip = br.readLine();
            if (ip != null) {
                skt = new Socket(ip, 5678);
            } else {
                skt = new Socket("127.0.0.1", 5678);
            }
        } catch (IOException e) {
            System.err.println("Der Client konnte sich nicht anmelden!");
            clientKlasse();
        }
        System.out.println("angemeldet!");
        hilfe(skt);
        erstelleNachricht(skt);

        try {
            skt.close();
        } catch (IOException e) {
            System.err.println("Der Client konnte nicht geschlossen werden!");
        }
    }

    /**
     * erstelleNachricht lässt Konsolenbefehle erstellen und die diese an einen Server senden
     * und erwartet eine Antwort.
     *
     * @param skt Serveradresse
     */
    void erstelleNachricht(Socket skt) {
        try {
            int zuBerechnendeZahl = -2;

            //Befehle werden erstellt und zuBerechnendeZahl kann nur eine Zahl enthalten
            zuBerechnendeZahl = erstelleBefehl(skt);
            //zuBerechnendeZahl wird an den Server gesendet
            schickeNachrichten(skt, zuBerechnendeZahl);
            //vom Server berechnete Fibonaccizahl wird gelesen und ausgegeben auf der Konsole
            System.out.println("Zahl die berechnet werden soll ist: ' " + zuBerechnendeZahl + "'.\n" +
                    "Antwort vom Server: '" + leseNachrichten(skt) + "'.");
            //es ist alles korrekt gelaufen und es kann wieder eine Nachricht für den Server erstellt werden
            erstelleNachricht(skt);
        } catch (Exception ex) {
            System.out.println("Fehlercode: -3\n" +
                    "Grund: 'Der Server ist down!'");
            System.out.println("Soll das Programm beendet werden? (Y/N)");
            try {
                String programmBeenden = br.readLine();
                if (programmBeenden.equalsIgnoreCase("y")) ende();
                if (programmBeenden.equalsIgnoreCase("n")) erstelleNachricht(skt);
                System.out.println("Die Eingabe wurde jetzt als 'nein' interpretiert und auf 'erstelleNachricht'" +
                        " zur\u00FCckgesetzt!");
            } catch (IOException e) {
                System.err.println("Beim Schreiben in die Konsole ist etwas schiefgegangen...sorry...");
            }
        }
    }

    /**
     * erstelleBefehl erstellt Konsolenbefehle und lässt überprüfen.
     *
     * @param skt Serveradresse
     * @return eine Zahl die an dem Server übergeben werden soll
     */
    int erstelleBefehl(Socket skt) {
        System.out.println("Geben Sie bitte eine Zahl, die als Fibonacci-Zahl berechnet werden soll, ein:");
        System.out.print("$> ");
        String befehl = "keine Nachricht erstellt..";
        int zahl = 0;
        try {
            befehl = br.readLine();
        } catch (IOException ioEx) {
            System.err.println("Beim Schreiben in die Konsole ist etwas schiefgegangen...sorry...");
        }
        zahl = ueberpruefeNachicht(skt, befehl);
        return zahl;
    }

    /**
     * überprüft die Konsolenbefehle und ruft entsprechende Aktionen auf oder return eine Zahl.
     *
     * @param skt    Serveradresse
     * @param befehl der überprüft werden soll
     * @return eine Zahl im Berecih von 1 - 99 die zum Berechnen an den Server gesendet werden soll
     */
    int ueberpruefeNachicht(Socket skt, String befehl) {

        int ueberpruefteZahl = 0;
        if (befehl.equalsIgnoreCase("ende")) ende();
        if (befehl.equalsIgnoreCase("hilfe")) hilfe(skt);
        if (befehl.toLowerCase().startsWith("berechne")) {
            try {
                //in ueberpruefteZahl wird eine Zahl eingetragen, wenn es kein natürlich zahl ist,
                //tritt ein Fehler auf
                ueberpruefteZahl = Integer.parseInt(befehl.split(" ")[1]);

                //Werteberecih überprüfen
                //auskommentiert, sonst hat der Server bei Fehlern beim Befehl gar nichts zu tun..
                /*if (ueberpruefteZahl < 1 || ueberpruefteZahl > 99) {
                    System.out.println("(-2) \nDie Zahl sollte sich im Bereich zwischen 1 und 99 befinden" +
                            "! --> ung\u00FCltiger Zahlenbereich!");
                    return -2;
                }*/

            } catch (Exception ex) {
                int fehler = -1;
                System.out.println("(-1) - Fehler abgefangen bevor die Nachricht an den Server geht. \n" +
                        "Das war keine nat\u00FCrliche Zahl!\n" +
                        "Es wird die Hilfe aufgerufen.");
                //System.out.println("Die ausgehende Nachricht sollte : '" + s + "' sein. --> Fehlercode -1");
                hilfe(skt);
                return fehler;
            }
        } else {
            //Befehl 'berechne' wurde falsch geschrieben...
            System.out.println("(-1) - Fehler abgefangen bevor die Nachricht an den Server geht. \n" +
                    "Der Befehl 'berechne' wurde falsch geschrieben.\n" +
                    "Es wird die Hilfe aufgerufen.");
            hilfe(skt);
            return -1;
        }
        return ueberpruefteZahl;
    }

    /**
     * schickeNachrichten soll Nachrichten vom Client an den Server senden.
     *
     * @param skt               Serveradresse
     * @param zuBerechnendeZahl die Zahl, die vom Server berechnet werden soll
     * @throws IOException wird in erstelleNachricht gefangen und ausgewertet
     */
    void schickeNachrichten(Socket skt, int zuBerechnendeZahl) throws IOException {
        OutputStream os = skt.getOutputStream();
        DataOutputStream don = new DataOutputStream(os);
        don.writeInt(zuBerechnendeZahl);
        don.flush();
        //System.out.println("gesendet..");
    }

    /**
     * leseNachrichten liest die Nachrichten vom Server ein.
     *
     * @param skt Serveradresse
     * @return die Antwort vom Server
     * @throws IOException wird in erstelleNachricht gefangen und ausgewertet
     */
    int leseNachrichten(Socket skt) throws IOException {
        InputStream is = skt.getInputStream();
        DataInputStream din = new DataInputStream(is);
        //System.out.println("lese..");
        int fibo = din.readInt();
        // erste if-Anweisungen wird nicht aufgerufen (da vorher abgefangen!), ist aber vorhanden
        if (fibo == -1) {
            System.out.println("Fehlercode: -1\n" +
                    "Grund: 'Es war keine nat\u00FCrliche Zahl!'");
            erstelleNachricht(skt);
        }
        if (fibo == -2) {
            System.out.println("Fehlercode: -2\n" +
                    "Grund: 'Ung\u00FCltiger Zahlenbereich! [1 - 99]'");
            erstelleNachricht(skt);
        }

        return fibo;
    }

    /**
     * Löst Konsolenausgaben aus die dem Client die möglichen Befehle anzeigt.
     *
     * @param skt Serveradresse
     */
    void hilfe(Socket skt) {
        //System.out.println("Hilfe...");
        // Eingabemöglichkeiten auf der Konsole ausgeben und dem Client anweisen wieder eine
        // Konsoleneingabe zu tätigen
        System.out.println("m\u00F6gliche Befehle:\n\n" +
                "hilfe           - Bedienungshilfe wird ausgegeben\n" +
                "berechne <zahl> - berechnet fibonacci f\u00FCr <zahl>\n" +
                "ende            - beendet die Anwendung\n");
        erstelleNachricht(skt);
    }

    /**
     * Beendet die Anwendung.
     */
    void ende() {
        System.out.println("Die Anwendung wird beendet. Bis zum n\u00E4chsten mal.");
        try {
            br.close();
        } catch (IOException e) {
            System.err.println("Der Schreibemechanismus in 'erstelleBefehl' konnte nicht geschlossen werden ");
        }
        Runtime.getRuntime().exit(0);

    }

}
