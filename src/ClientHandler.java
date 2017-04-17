import java.net.Socket;
import java.util.Date;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import org.apache.commons.cli.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientHandler implements Runnable {
	public int name;
	public Date now;
	public boolean debugmode = false;

	public Socket connectionSock;
	
	public DataInputStream clientInput;
	public DataOutputStream clientOutput;
	
	public ClientHandler(Socket connectionSock, int name, boolean debug){
		now = new Date();
		this.connectionSock = connectionSock;
		this.name = name;
		debugmode = debug;
	}
	
	public void run(){
		try {
			/*System.out.println("Connected to client" + name);
			
			clientInput = new DataInputStream(connectionSock.getInputStream());
			clientOutput = new DataOutputStream(connectionSock.getOutputStream());

			System.out.println("Waiting for client " + "to send file."); 
			String clientText = clientInput.readUTF();
			String replyText = "Welcome, " + clientText + ", Today is " + now.toString() + "\n";
			
			clientOutput.writeUTF(replyText); 
			System.out.println("Sent: " + replyText);
			
			//clientOutput.close();
			//clientInput.close();
			//connectionSock.close();*/
		
			clientOutput = new DataOutputStream(connectionSock.getOutputStream());
			clientInput = new DataInputStream(connectionSock.getInputStream());
			
			/*String fileName = clientInput.readUTF();
			System.out.println(fileName);
			long fileLength = clientInput.readLong();
			System.out.println(fileLength);
			
			
			
			File f = new File("F:/eclipse-workspace/Socket1/src/temp/" );
			if(!f.exists()){
				f.mkdir();
			}
			String filepath = "F:/eclipse-workspace/Socket1/src/temp/" + fileName;
			
			FileOutputStream fs = new FileOutputStream(new File(filepath));
			byte[] inputByte = new byte[1024];
			double suml = 0;
			int length = 0;
			while ((length = clientInput.read(inputByte)) > 0){
				double left = fileLength - suml;
				if(left < 1024){
					System.out.println("Already received: 100%");
					fs.write(inputByte, 0, (int) left);
					fs.flush();
					break;
				}else{
					suml += length;
					System.out.println("Already received:" + ((suml/fileLength)*100)+"%");
					fs.write(inputByte, 0, length);
					fs.flush();
				}
				
			}
			

			JSONObject json = new JSONObject();
			json.put("feedback", "Transfer complete");
			System.out.println("Transfer complete");
			clientOutput.writeUTF(json.toJSONString());
			
			String temp = clientInput.readUTF();
			Object obj = JSONValue.parse(temp);
			JSONObject jsonObject = (JSONObject)obj;
			String confirm = (String)jsonObject.get("command");
			System.out.println(confirm);

			
			fs.close();*/
			
			JSONParser parser = new JSONParser();
			
			while(true){
		    	if(clientInput.available() > 0){
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(clientInput.readUTF());
		    		
		    		if(debugmode){
			    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());		    			
		    		} 
		    		
		    		parseCommand(command);
		    		/*Integer result = parseCommand(command);
		    		JSONObject results = new JSONObject();
		    		results.put("result", result);
		    		output.writeUTF(results.toJSONString());*/
		    	}
		    }

			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void parseCommand(JSONObject command){
		
		switch((String) command.get("command")){
			case "publish":
				
				break;
			case "share":
				
				break;
			case "fetch":
				
				break;
			case "query":
				
				break;
			case "remove":
				
				break;
			case "exchange":
				
				break;
			default:
				
				try {
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
	}
	

	
}


