package rmic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TimeClient {

	public static void main(String args[]) {		 
		 
		TimeServerInterface timeServer;
	
		try {
			timeServer = (TimeServerInterface) Naming.lookup("rmi://localhost/time-server");
			String time = timeServer.time();
			System.out.println("Uhrzeit: " + time);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}