package abgabe3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by Daniel & Andreas on 11.11.2015.
 */
public class PinnwandClient {

    private static Pinnwand pinnwand;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] agrs) {
        try {
            pinnwand = (Pinnwand) Naming.lookup("Pinnwand");
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.err.println("Beim LogIn-Vorgang ist etwas schiefgegangen, sorry!");
        }

        PinnwandClient client = new PinnwandClient();
        client.client();

    }

    /**
     * Hier wird der Client ausgeführt und als zu erst wird der Login aufgerufen.
     */
    void client() {
        if (login()) {
            System.out.println("angemeldet!");
            hilfe();
            terminal();
        }
    }

    /**
     * Hier wird die Verbindung zum Server hergestellt und der Login ausgeführt.
     * Es wird eine Passworteingaben angefordert und dann zum Server geschickt und als
     * Antwort kommt entweder
     * 0 fuer falsches Passwort (false) oder
     * 1 fuer richtiges Passwort (true)
     *
     * @return den Wert, ob der Login erfolgreich war oder nicht
     */
    boolean login() {
        try {
            while (true) {
                System.out.println("Geben Sie bitte ein Passwort ein.");
                System.out.print("$> ");
                String password = br.readLine();
                int pw = pinnwand.login(password);
                //if the password is correct --> return true (continue)
                if (pw == 1) return true;
                System.out.println("Falsches Passwort bitte versuchen sie es erneut!");
            }
        } catch (IOException ex) {
            System.err.println("Bei der Eingabe ist etwas schiefgegangen!");
        } catch (ServerNotActiveException e) {
            System.err.println("erneut anmelden...\n" + e.getMessage());
        }
        return false;
    }

    /**
     * Hier können Befehle erstellt werden und danach zur Ueberpruefung weitergegeben.
     * Ruft sich anschließend selber wieder auf.
     */
    private void terminal() {
        System.out.println("Geben Sie bitte etwas ein...");
        System.out.print("$> ");
        String befehl;
        try {
            befehl = br.readLine();
            befehl = ueberpruefeBefehl(befehl);
            System.out.println("ung\u00FCltiger Befehl : '" + befehl + "'\n" +
                    "Das Terminal wird aufgerufen...");
            terminal();
        } catch (IOException ioEx) {
            System.err.println("Beim Schreiben in die Konsole ist etwas schiefgegangen...sorry...");
        }
    }

    /**
     * Hier werden die Terminalbefehle ueberprueft und entsprechende Aktionen gestartet.
     *
     * @param befehl ist der Terminalbefehl
     * @return return nur, wenn kein gueltiger Befehl eingegeben wurde
     */
    String ueberpruefeBefehl(String befehl) {

        try {
            if(pinnwand.getMessageCount()==-1) {
                System.out.println("Sie waren zu lange inaktiv und wurden ausgeloggt!");
                login();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (befehl.equalsIgnoreCase("ende")) ende();
        if (befehl.equalsIgnoreCase("anzahl")) nachrichtenZaehler();
        if (befehl.equalsIgnoreCase("alleMsg")) alleNachrichten();
        if (befehl.startsWith("nachricht ")) {
            int index = Integer.parseInt(befehl.split(" ")[1]);
            bestimmteNachricht(index);
        }
        if (befehl.equalsIgnoreCase("posten")) {
            try {
                int index = pinnwand.getMessageCount();
                if (index < 0 || index > 19) {
                    System.out.println("Die Pinnwand ist voll bitte warte ein wenig (maximal 60 sek).");
                    terminal();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            postenMsg();
        }
        if (befehl.equalsIgnoreCase("hilfe")) hilfe();
        return befehl;
    }

    /**
     * Loest Konsolenausgaben aus die dem Client die moeglichen Befehle anzeigt.
     */
    private void hilfe() {
        //System.out.println("Hilfe...");
        // Eingabemoeglichkeiten auf der Konsole ausgeben und dem Client anweisen wieder eine
        // Konsoleneingabe zu taetigen
        System.out.println("m\u00F6gliche Befehle:\n\n" +
                "hilfe           - Bedienungshilfe wird ausgegeben\n" +
                "anzahl          - z\u00E4hlt alle Pinnwandeintr\u00E4ge\n" +
                "alleMsg         - gibt alle Nachrichten an der Pinnwand aus\n" +
                "nachricht <Zahl>- Ruft die Nachricht mit dem eingegeben Index aus\n" +
                "posten          - An die Pinnwand posten\n" +
                "ende            - beendet die Anwendung\n");
        terminal();
    }

    /**
     * Beendet die Anwendung.
     */
    private void ende() {
        System.out.println("Die Anwendung wird beendet. Bis zum n\u00E4chsten mal.");
        try {
            br.close();
        } catch (IOException e) {
            System.err.println("Der Schreibemechanismus in 'erstelleBefehl' konnte nicht geschlossen werden ");
        }
        Runtime.getRuntime().exit(0);

    }

    /**
     * Gibt die Anzahl der Nachrichten auf der Pinnwand aus
     */
    private void nachrichtenZaehler() {
        int count = 0;
        System.out.println("Es werden die Nachrichten gezaehlt");
        try {
            count = pinnwand.getMessageCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("Anzahl: " + count);
        terminal();
    }

    /**
     * Gibt alle Nachrichten auf der Pinnwand aus
     */
    private void alleNachrichten() {
        String[] alleNach = null;
        try {
            alleNach = pinnwand.getMessages();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (alleNach != null) {
            for (String msg : alleNach) {
                if (msg != null) System.out.println(msg);
            }
        }
        terminal();
    }

    /**
     * Gibt eine Nachricht von der Pinnwand
     * @param index, soll x. Nachricht sein
     */
    private void bestimmteNachricht(int index) {
        if (index < 1 && index > 20) {
            System.out.println("Bitte geben sie einen Indexwert von 1 bis 20 ein.");
            terminal();
        }
        try {
            int anzahl = pinnwand.getMessageCount();
            if (index > 0 && index <= anzahl) {
                String msg;
                System.out.println("Es wird die Nachrichte mit dem index " + index + " ausgegeben.");
                msg = pinnwand.getMessage(index - 1);
                System.out.println("Nachricht: " + msg);
            } else {
                System.out.println("Der eingegeben Index bringt keine g\u00FCltige Nachricht.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        terminal();
    }

    /**
     * Postet einen String an die Pinnwand
     */
    private void postenMsg() {
        System.out.println("Bitte geben Sie Ihre Nachricht ein.");
        String nachricht;
        try {
            nachricht = br.readLine();
            if (pinnwand.putMessage(nachricht)) {
                System.out.println("Nachricht erfolgreich an die Pinnwand genagelt!");
            } else {
                System.out.println("Ihre Nachricht ist zulang oder die Pinnwand ist voll.\n" +
                        "Bitte versuchen sie es in 6 Sek. nochmal.");
                postenMsg();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Upps beim Schreiben ist was schiefgegangen");
        }
        terminal();
    }
}
