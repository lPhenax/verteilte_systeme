package abgabe2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Burger & Schleußner on 26.10.2015.
 */
public class Server3 {

    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(5678);
        } catch (IOException e) {
            System.err.println("Der Server konnte nicht gestartet werden!");
        }
        System.out.println("Der Server ist online!");

        clientBehandlung(server);

        try {
            server.close();
        } catch (IOException e) {
            System.err.println("Der Server konnte nicht gestoppt werden. --> Bitte Prozess terminieren.");
        }
    }

    /**
     * warteAufAnmeldung soll Clients beim Server akzeptieren.
     *
     * @param serverSocket Serveradresse
     * @return einen angemeldeten Client
     */
    static Socket warteAufAnmeldung(ServerSocket serverSocket) {
        Socket client = null;
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Der Client wurde nicht akzeptiert!");
        }
        System.out.println("Client hat sich angemeldet. --> " + client.getInetAddress() + ":" + client.getLocalPort());
        return client;
    }

    /**
     * clientBehandlung meldet Clients an und antwortet auf Nachrichten von ihnen.
     *
     * @param server Serveradresse
     */
    static void clientBehandlung(ServerSocket server) {
        Socket client = warteAufAnmeldung(server);
        try {
            while (true) {
                int zuBerechnendeZahl = leseNachrichten(client);
                System.out.println("--- Zahl vom Client die berechnet werden soll : '" + zuBerechnendeZahl + "'.");
                int ueberpruefteNachricht = ueberpruefeNacht(zuBerechnendeZahl);
                System.out.println("--- berechnete Zahl = '" + ueberpruefteNachricht + "' --> wird gesendet!");
                schickenNachrichten(client, ueberpruefteNachricht);
            }
        } catch (Exception ex) {
            System.out.println("Der Client hat den Server und die Anwendung verlassen!");
        }
        clientBehandlung(server);
    }

    /**
     * Überprüft die eingehende Nachricht vom Client auf ihren Wertebereich.
     *
     * @param nachricht vom Client
     * @return die berechnete Fibonaccizahl oder den Fehlercode -2.
     */
    static int ueberpruefeNacht(int nachricht) {
        if (nachricht < 1 || nachricht > 99) {
            return -2;
        }
        return Fibonacci3.StartFiboMitPara(nachricht);
    }

    /**
     * schreibenachricht schickt dem Client eine Antwort mit der berechneten Fibo-Zahl.
     *
     * @param skt  Socket vom Client
     * @param fibo berechnete Fibonaccizahl
     * @throws IOException wird in clientBehandlung gefangen und ausgewertet
     */
    static void schickenNachrichten(Socket skt, int fibo) throws IOException {
        OutputStream os = skt.getOutputStream();
        DataOutputStream don = new DataOutputStream(os);
        //System.out.println("schreiben..");
        don.writeInt(fibo);
        don.flush();
        //System.out.println("gesendet..");
    }

    /**
     * leseNachricht erwartet eine Zahl und blockiert so lange bis sie eine Nachricht erhält.
     * Geschieht dies nicht wird eine Exception geworfen und in der Methode clientBehandlung gefangen.
     *
     * @param skt Socket vom Client
     * @return gelesene Nachricht vom Client
     * @throws IOException wird in clientBehandlung gefangen und ausgewertet
     */
    static int leseNachrichten(Socket skt) throws IOException {
        InputStream is = skt.getInputStream();
        DataInputStream din = new DataInputStream(is);
        int zahl = din.readInt();
        //System.out.println("gelesen");
        return zahl;
    }

}
