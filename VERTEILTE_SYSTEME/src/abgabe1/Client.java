package abgabe1;
//Client2.java

//https://de.wikibooks.org/wiki/Java_Standard:_Socket_ServerSocket_%28java.net%29_UDP_und_TCP_IP
//http://stackoverflow.com/questions/17314253/simple-jframe-with-a-combobox-and-a-textfield-and-a-result-in-a-label

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Client {

	private static JComboBox<String> comboBox;
	private static JTextField textField;

	public static void main(String[] args) {
		Client client = new Client();
		try {
			client.test();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methode, die den Client startet, am Server anmeldet und einen
	 * Ereignis-Dialog startet.
	 * 
	 * @throws IOException
	 */
	void test() throws IOException {
		String ip = "127.0.0.1"; // localhost
		int port = 12222;
		Socket socket = new Socket(ip, port); // verbindet sich mit Server

		// 1.Möglichkeit = Ereignisdialog
		// nach der Anmeldung am Server wird ein Ereignis-Dialog geöffnet
		//ergebnisDesDialogs(socket);

		// 2.Möglichkeit = Console..
		try {
			BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Geben sie Bitte eine Zahl ein:");
			String input = in2.readLine();
			schreibeNachricht(socket, input);
			System.out.println(leseNachricht(socket));
			in2.close();
		}
		catch(Exception e) {
			System.out.print("Whoops! It didn't work!\n");
		}

		/** ohne Ereignis-Dialog **/
		// String zuSendendeNachricht = "Hello, world!";
		// schreibeNachricht(socket, zuSendendeNachricht);
		// String empfangeneNachricht = leseNachricht(socket);
		// System.out.println(empfangeneNachricht);
	}

	/**
	 * Schreibt Nachrichten und schickt sie dann aun den Server.
	 * 
	 * @param socket Adresse des Servers
	 * @param nachricht Nachricht an den Server
	 * @throws IOException
	 */
	void schreibeNachricht(Socket socket, String nachricht) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(nachricht);
		printWriter.flush();
	}

	/**
	 * Wertet Nachrichten, die vom Server an den Client gesendet wurden aus.
	 * 
	 * @param socket Adresse vom Server
	 * @return Nachricht vom Server
	 * @throws IOException
	 */
	String leseNachricht(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
		return new String(buffer, 0, anzahlZeichen);
	}

	/**
	 * Ein Ereignis-Dialog, der zwei zur Auswahl stehende Optionen beinhaltet,
	 * 1. die ersten 10 Fibonacci-Zahlen als Folge ausgeben und 2. eine
	 * beliebige Zahl, wird als Fibonacci-Zahl berrechnet. Die Auswahl wird als
	 * String an den Server gesendet.
	 * 
	 * @param socket Bekommt die Adresse vom Server, um eine ausgewählte Nachricht vom Dialog an ihn weiter zu senden
	 */
	void ergebnisDesDialogs(Socket socket) {
		JFrame frame = new JFrame("Auswahl-Dialog");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Button submit
		JButton ok = new JButton("Best\u00e4tigen");
		ok.addActionListener(e -> {
            if (comboBox.getSelectedIndex() == 0) {
                try {
                    // schreibeNachricht(socket, Fibonacci.StartFibo());
                    schreibeNachricht(socket, "starteFolge");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String empfangeneNachricht = null;
                try {
                    empfangeneNachricht = leseNachricht(socket);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.out.println(empfangeneNachricht);
            } else {
                try {
                    schreibeNachricht(socket, textField.getText());
                } catch (NumberFormatException | IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                    System.out.println("nochmal schreiben..");
                }
                String empfangeneNachricht = null;
                try {
                    empfangeneNachricht = leseNachricht(socket);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    System.out.println("nochmal lesen..");
                }
                System.out.println(empfangeneNachricht);
            }
            frame.dispose();
        });

		// Frame for our test
		frame.getContentPane().add(panel(), BorderLayout.NORTH);

		// Panel with the button
		JPanel p = new JPanel();
		p.add(ok);
		frame.getContentPane().add(p, BorderLayout.SOUTH);

		// Show the frame
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Inhalt des JFrames
	 * 
	 * @return ein JPanel, das eine JComboBox und mehrere JLabels beinhaltet
	 */
	JPanel panel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		// Panel for the labels
		JPanel labelPanel = new JPanel(new GridLayout(2, 1)); // 2 rows 1 column
		panel.add(labelPanel, BorderLayout.WEST);

		// Panel for the fields
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1)); // 2 rows 1 column
		panel.add(fieldPanel, BorderLayout.CENTER);

		// Textfield
		JLabel labelTextField = new JLabel("Zahl die als Fibonacci-Zahl dargestellt werden soll.");
		textField = new JTextField();
		textField.setText("Eine Ganzzahl eintragen.");
		textField.setVisible(false);

		// Combobox
		JLabel labelCombo = new JLabel("Auswahl-Box zur Fibonacci-Folge.");

		// Options in the combobox
		String[] options = { "Zeige mir die ersten zehn Fibonacci-Zahlen.", "Zeige mir eine Fibonacci-Zahl." };
		comboBox = new JComboBox<>(options);
		comboBox.addActionListener(e -> {
            if (comboBox.getSelectedItem().equals("Zeige mir eine Fibonacci-Zahl.")) {
                textField.setVisible(true);
            } else {
                textField.setVisible(false);
            }

        });

		// Add labels
		labelPanel.add(labelCombo);
		labelPanel.add(labelTextField);

		// Add fields
		fieldPanel.add(comboBox);
		fieldPanel.add(textField);

		return panel;
	}
}
