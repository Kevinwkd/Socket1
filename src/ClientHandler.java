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
		
			clientOutput = new DataOutputStream(connectionSock.getOutputStream());
			clientInput = new DataInputStream(connectionSock.getInputStream());
			
			
			JSONParser parser = new JSONParser();
			
			while(true){
		  
		    		JSONObject command = (JSONObject) parser.parse(clientInput.readUTF());
		    		
		    		if(debugmode){
			    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());		    			
		    		} 
		    		
		    		//clientOutput.writeUTF(parseCommand(command).toString());
		    		if(!((String) command.get("command")).equals("FETCH")){
		    			for(JSONObject temp : parseCommand(command)){
			    			clientOutput.writeUTF(temp.toString());
			    		}
		    		}
		    		else{
		    			ArrayList<JSONObject> ParsedCommand=new ArrayList<JSONObject>();
		    			ParsedCommand=parseCommand(command);
		    			clientOutput.writeUTF(ParsedCommand.get(0).toString());
	    				clientOutput.flush();
		    			if(ParsedCommand.get(0).get("response").equals("success")){
			    			clientOutput.writeUTF(ParsedCommand.get(1).toString());
			    			clientOutput.flush();
			    			if(ParsedCommand.size()>2){
				    			for(int index=0;index<(ParsedCommand.get(2)).size();index++){
				    				clientOutput.write((((ParsedCommand.get(2))).get(String.valueOf(index))).toString().getBytes());
				    				clientOutput.flush();
				    			}
				    			clientOutput.writeUTF(ParsedCommand.get(3).toString());
				    			clientOutput.flush();
			    			}
		    			}
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
			case "PUBLISH":
				res.add(commandparse.ParsePUBLISHSHARE(command));
				return res;
			case "SHARE":
				res.add(commandparse.ParsePUBLISHSHARE(command));
				return res;
			case "FETCH":
				res = commandparse.ParseFETCH(command);
				return res;
			case "QUERY":
				res = commandparse.ParseQUERY(command);
				return res;
			case "REMOVE":
				res.add(commandparse.ParseREMOVE(command));
				return res;
			case "EXCHANGE":
				res.add(commandparse.ParseEXCHANGE(command));
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


