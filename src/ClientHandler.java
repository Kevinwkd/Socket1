import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.cli.CommandLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ClientHandler implements Runnable {
	public int clientnum;
	public boolean debugmode = true;
	public String secret;
	public String hostname;
	
	public Socket connectionSock;
	
	public DataInputStream clientInput;
	public DataOutputStream clientOutput;
	
	public static ServerResourceList resourcelist;
	public ArrayList<String> serverrecordslist;
	
	public CommandParse commandparse;
	
	public ClientHandler(ServerResourceList resourcelist, Socket connectionSock, String hostname, 
			int clientnum, String secret, ArrayList<String> serverrecordslist, boolean debug){
		ClientHandler.resourcelist = resourcelist;
		this.serverrecordslist = serverrecordslist;
		this.connectionSock = connectionSock;
		this.clientnum = clientnum;
		debugmode = debug;
		this.secret = secret;
		this.hostname = hostname;
		System.out.println("Client " + clientnum + " connected to the server");
		
		commandparse = new CommandParse(secret,connectionSock,hostname,resourcelist,serverrecordslist);
	}
	
	public void run(){
		try {
			//clientOutput.close();
			//clientInput.close();
			//connectionSock.close();
		
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
		    	//if(clientInput.available() > 0){
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(clientInput.readUTF());
		    		
		    		if(debugmode){
			    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());		    			
		    		} 
		    		
		    		//clientOutput.writeUTF(parseCommand(command).toString());
		    		
		    		
		    		for(JSONObject temp : parseCommand(command)){
		    			clientOutput.writeUTF(temp.toString());
		    		}

		    }

			
		} catch (IOException | ParseException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<JSONObject> parseCommand(JSONObject command) 
			throws ParseException, URISyntaxException{
		
		ArrayList<JSONObject> res = new ArrayList<JSONObject>();
		switch((String) command.get("command")){
			case "publish":
				res.add(commandparse.ParsePUBLISHSHARE(command));
				return res;
			case "share":
				res.add(commandparse.ParsePUBLISHSHARE(command));
				return res;
			case "fetch":
				
				break;
			case "QUERY":
				res = commandparse.ParseQUERY(command);
				return res;
			case "remove":
				res.add(commandparse.ParseREMOVE(command));
				return res;
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
		return res;
		
	
	}
	
		
}


