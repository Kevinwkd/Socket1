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
		    		
		    		clientOutput.writeUTF(parseCommand(command).toString());
		    		System.out.println("hhhhhh");
		    		/*Integer result = parseCommand(command);
		    		JSONObject results = new JSONObject();
		    		results.put("result", result);
		    		output.writeUTF(results.toJSONString());*/
		    	//}
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
			case "query":
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
	
	/*private static JSONObject ParsePUBLISHSHARE(JSONObject command, ServerResourceList resourcelist) 
			throws ParseException,URISyntaxException{
		
		JSONObject tempobject;
		JSONObject jsonobject = new JSONObject();
		Resource temp = new Resource();
		
		if(command.get("resource") != null && (tempobject = StoreResourceInfo(command, temp)) != null){
			return tempobject;
		}else if(command.get("resource") == null){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "missing field");
			return jsonobject;
		}else{
			if((command.get("command").equals("publish") && resourcelist.PublishResource(temp))
			  ||(command.get("command").equals("share") && resourcelist.ShareResource(temp))){
				jsonobject.put("response", "success");
				return jsonobject;
			}else{
				jsonobject.put("response", "error");
				jsonobject.put("errorMessage", "cannot " +  command.get("command") + " resource");
				return jsonobject;
			}
		}
		

	}

	private static ArrayList<JSONObject> ParseQUERY(JSONObject command, ServerResourceList resourcelist) {
		
			ArrayList<JSONObject> jsonlist = new ArrayList<JSONObject>();
			ArrayList<Resource> resourcefound = new ArrayList<Resource>();
			JSONObject jsontemp = new JSONObject();
			
			Resource temp_1 = new Resource();
			StoreResourceInfo(command, temp_1);
			if(command.get("relay").equals("false")){
				resourcefound = resourcelist.queryResource(temp_1, false);
			}
			
			if(!resourcefound.isEmpty()){
				jsontemp.put("response", "success");
				jsonlist.add(jsontemp);
				jsontemp.clear();
				for(Resource temp_2 : resourcefound){
					jsontemp.put("name", temp_2.resource_name);		
					jsontemp.put("tags", temp_2.resource_tags);
					jsontemp.put("description",  temp_2.resource_description);
					jsontemp.put("uri", temp_2.resource_uri);
					jsontemp.put("channel", temp_2.channel);
					jsontemp.put("owner", temp_2.owner);
					jsontemp.put("ezserver", temp_2.ezserver);
					jsonlist.add(jsontemp);
					jsontemp.clear();
				}
				jsontemp.put("responseSize", jsonlist.size());
				jsonlist.add(jsontemp);
			}
			
			return jsonlist;

	}
	
	private static JSONObject ParseREMOVE(JSONObject command,ServerResourceList resourcelist){
		JSONObject jsonobject = new JSONObject();
		Resource temp = new Resource();
		
		JSONObject subcommand = (JSONObject) command.get("resource");
		
		if(subcommand.get("channel") == null || subcommand.get("owner") == null
		   ||subcommand.get("uri") == null){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "missing resource");
			return jsonobject;
		}
		
		temp.resource_name = (subcommand.get("name") != null) ? ((String)subcommand.get("name")).trim() : "";
		temp.resource_description = (subcommand.get("description") != null) ? 
				((String)subcommand.get("description")).trim() : "";
		temp.resource_tags = (subcommand.get("tags") != null) ? subcommand.get("tags").toString().trim() : "";
		temp.ezserver = (subcommand.get("ezserver") != null) ? ((String)subcommand.get("ezserver")).trim() : null;
		
		if(resourcelist.RemoveResource(temp)){
			jsonobject.put("response", "success");
			return jsonobject;
		}else{
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "cannot remove resource");
			return jsonobject;
		}
	}
	
	private static JSONObject StoreResourceInfo(JSONObject command,Resource resource){
		JSONObject jsonobject = new JSONObject();
		Resource temp = resource;
		
		JSONObject subcommand = (JSONObject) command.get("resource");
		
		temp.resource_name = (subcommand.get("name") != null) ? ((String)subcommand.get("name")).trim() : "";
		temp.resource_description = (subcommand.get("description") != null) ? 
				((String)subcommand.get("description")).trim() : "";
		temp.resource_tags = (subcommand.get("tags") != null) ? subcommand.get("tags").toString().trim() : "";
		temp.channel = (subcommand.get("channel") != null) ? ((String)subcommand.get("channel")).trim() : "";
		temp.owner = (subcommand.get("owner") != null) ? ((String)subcommand.get("owner")).trim() : "";
		temp.ezserver = hostname +" : " + connectionSock.getPort();
		
		if(command.get("command").equals("share") && !command.get("secret").equals(secret)){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "incorrect secret");
			return jsonobject;
		}
		
		if(subcommand.get("uri") == null && !command.get("command").equals("query")){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "missing resource");
			return jsonobject;
		}else if(command.get("command").equals("query")){
			temp.resource_uri = (subcommand.get("uri") != null) ? (String) subcommand.get("uri") : "";
		}else{
			
			try {
				URI uri = new URI((String) subcommand.get("uri"));
				File f = new File("F:" + uri.getPath());
				
				if((uri.getScheme().equals("file") && ((String)command.get("command")).equals("publish"))
					||(!uri.getScheme().equals("file") && ((String)command.get("command")).equals("share"))
					||(((String)command.get("command")).equals("share") && (!f.isAbsolute() || uri.getAuthority() != null))){
					
					jsonobject.put("response", "error");
					jsonobject.put("errorMessage", "invaild resource");
					return jsonobject;
				}
				temp.resource_uri = (String) subcommand.get("uri");
			} catch (URISyntaxException e) {
				System.out.println("URIEXCEption");
				jsonobject.put("response", "error");
				jsonobject.put("errorMessage", "invaild resource");
				return jsonobject;
			}
		}
		
		return null;
	}*/
		
}


