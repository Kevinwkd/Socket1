import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {
	public int port = 7654;
	public int counter = 0;
	
	public ServerSocket serverSock;
	public Socket connectionSock;
	
	public Server(){
		
	}
	
	public void run(){
		
		try {
			serverSock = new ServerSocket(port);
			while(true){
				connectionSock = serverSock.accept();
			
				counter ++;
				ClientHandler handler = new ClientHandler(connectionSock,counter);
				new Thread(handler).start();

			
			}
			//serverSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
