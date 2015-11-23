package abgabe1;

//Server2.java
//https://de.wikibooks.org/wiki/Java_Standard:_Socket_ServerSocket_%28java.net%29_UDP_und_TCP_IP

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void test() throws IOException {
        int port = 12222;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server wurde gestartet.");
        Socket client = warteAufAnmeldung(serverSocket);
        System.out.println("Anmeldung erfolgreich!");
        // Empfange die Nachricht des Clients
        boolean value = true;
        //while(true)-Implementierung fehlt noch beim Client
        /*while (value) {
			if(KeyStroke.getKeyStroke("ESCAPE").isOnKeyRelease()) {
				value = false;
				schreibeNachricht(client, "Serverwird herruntergefahren!");
				System.out.println("Serverwird herruntergefahren!");
			}*/
        String nachricht = leseNachricht(client);

        if (nachricht.equals("starteFolge")) {
            nachricht = Fibonacci.StartFibo() + "\nServer wird herruntergefahren!";
        } else if (nachricht.isEmpty() || nachricht.length() > 2 || nachricht.length() < 0 || Objects.equals(nachricht, "")) {
            nachricht = "Die \u00fcbergebene Nachricht ist nicht valide!\n" + "Die Eingabe war :'" + nachricht + "'.\n"
                    + "Es ist nur eine Zahl zwischen 1 und 99 erlaubt!\n"
                    + "Server wird herruntergefahren!";
        } else {
            nachricht = "Die Fibonacci-Zahl von '" + nachricht + "' ist : "
                    + String.valueOf(Fibonacci.StartFiboMitPara(Integer.parseInt(nachricht)))
                    + "\nServer wird herruntergefahren!";
        }
        // Gebe die Nachricht hier auch der Konsole aus
        System.out.println(nachricht);
        // und schicken sie dem Client zurück als Antwort
        schreibeNachricht(client, nachricht);
        // While-Schleife
        // }

    }

    Socket warteAufAnmeldung(ServerSocket serverSocket) throws IOException {
        // angemeldet hat
        return serverSocket.accept();
    }

    String leseNachricht(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[200];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert
        // bis
        // Nachricht
        // empfangen
        return new String(buffer, 0, anzahlZeichen);
    }

    void schreibeNachricht(Socket socket, String nachricht) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.print(nachricht);
        printWriter.flush();
    }

}