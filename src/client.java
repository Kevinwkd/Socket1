import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.simple.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class client {
	public String hostname;
	public int port;
	public Socket connectionSock;
	//public BufferedReader serverInput;
	public DataInputStream serverInput;
	//public ObjectOutputStream serverOOutput;
	public DataOutputStream serverOutput;
	//public PrintWriter serverOutput;
	
	public String [] Args;
	
	public client(String [] Args){
		hostname = "localhost";
		port = 7654;
		this.Args = Args;
	}
	
	public String InputContent(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
        System.out.println("Enter your value:"); 
        String str = null;
		try {
			str = br.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return str;     
	}
	
	
	
	public void run(){
		try {
			System.out.println("Connecting to server on port" + port);
			connectionSock = new Socket(hostname, port);
			/*//serverInput = new BufferedReader(
			//		 new InputStreamReader(connectionSock.getInputStream()));
			serverInput = new DataInputStream(connectionSock.getInputStream());
			serverOutput = new DataOutputStream(
					connectionSock.getOutputStream());
			//serverOutput = new PrintWriter(
			//		connectionSock.getOutputStream());
			
			
			
			String str = null;
			while(!(str = InputContent()).equals("Exit")){
				//System.out.println("Input your content to server:");
				//serverOutput.writeBytes("Dusty Rhodes\n");
				serverOutput.writeUTF(str);
				serverOutput.flush();
			
				System.out.println("Waiting for reply."); 
				String serverData = serverInput.readUTF();
				System.out.println("Received: " + serverData);
				str = null;
			}
			
			
			//serverOutput.close(); 
			//serverInput.close(); 
			//connectionSock.close(); */
			
			serverOutput = new DataOutputStream(
					connectionSock.getOutputStream());
			serverInput = new DataInputStream(
					connectionSock.getInputStream());
			
			

			
			File file = new File("F:/eclipse-workspace/Socket1/src/text.txt");
			
			serverOutput.writeUTF(file.getName());
			serverOutput.flush();
			serverOutput.writeLong(file.length());
			serverOutput.flush();
			
			FileInputStream fs = new FileInputStream(file);
			byte[] sendBytes = new byte[1024];
			
			int length = 0;
			long l =file.length();
			double suml = 0;
			while((length = fs.read(sendBytes, 0, sendBytes.length)) > 0) {
				suml += length;
				System.out.println("Already transferred:" + ((suml/l)*100) + "%");
				serverOutput.write(sendBytes);
				serverOutput.flush();
			}

			ArrayList<String> arr = new ArrayList<String>();
			for( String temp : Args){
				arr.add(temp);
			}
			String jsonText = JSONValue.toJSONString(arr);  
			JSONObject json = new JSONObject();
			json.put("command", jsonText);
			serverOutput.writeUTF(json.toJSONString());
			
			
			
			
			
			
			String temp = serverInput.readUTF();
			Object obj = JSONValue.parse(temp);
			JSONObject jsonObject = (JSONObject)obj;
			String confirm = (String)jsonObject.get("feedback");
			System.out.println(temp);
			
			
			fs.close();
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
