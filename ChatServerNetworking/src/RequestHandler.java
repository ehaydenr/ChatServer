import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * RequestHandler Thread

	Notes
		Loop and accept all connections and then submit them to Interrogator thread with reference to Network Manager
 * @author ehaydenr
 *
 */
public class RequestHandler extends Thread{
	private int port;
	private NetworkHandler server;
	private ServerSocket skt;
	
	public RequestHandler(NetworkHandler server, int port){
		this.server = server;
		this.port = port;
	}
	
	public void run(){
		Printer.println(this.getClass(), false, "Listening on port: " + port);
		extractIp();
		try{
			skt = new ServerSocket(port);
			Socket connection;
			while(!this.isInterrupted()){
				if(!server.clientCapReached()){
					Printer.println(this.getClass(), false, "Waiting for request");
					connection = skt.accept();
					Printer.println(this.getClass(), false, "Potential Client: " + connection.getInetAddress().toString());
					server.interrogate(connection);
				}
			}
		} catch (IOException e) {
			try {
				skt.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			if(!this.isInterrupted())
				e.printStackTrace();
		}
	}

	private void extractIp() {
		try {
			NetworkInterface i = NetworkInterface.getByName("eth0");
			if(i == null) i = NetworkInterface.getByName("en0");
			if(i == null) i = NetworkInterface.getByName("lo0");
			Enumeration<InetAddress> addresses = i.getInetAddresses();
			for(InetAddress inetAddress : Collections.list(addresses)){
				Printer.println(this.getClass(), false, "Inet Address: " + inetAddress);
				server.setIpString(inetAddress.toString());	//Take last one for now
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void interrupt(){
		super.interrupt();
		try {
			this.skt.close();
		} catch (IOException e) {}
	}
}
