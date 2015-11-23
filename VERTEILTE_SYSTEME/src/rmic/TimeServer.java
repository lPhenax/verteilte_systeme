package rmic;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

public class TimeServer extends UnicastRemoteObject
                        implements TimeServerInterface {

	private static final long serialVersionUID = 1L;

	protected TimeServer() throws RemoteException {
		super();
	}

	@Override
	public String time() throws RemoteException {
		return new Date().toString();
	}

	public static void main(String[] args){
		try {
			System.setSecurityManager(new RMISecurityManager());

			TimeServer timeServer = new TimeServer();
			Naming.rebind("rmi://localhost/time-server", timeServer);

		} catch(MalformedURLException | RemoteException ex){
			System.out.println(ex.getMessage());
		}
	}
}
