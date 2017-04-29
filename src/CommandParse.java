import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommandParse {
	
	public static String secret;
	public static String hostname;
	public static Socket connectionSock;
	
	public ServerResourceList resourcelist;
	public ArrayList<String> serverrecordslist;
	
	public CommandParse(String secret, Socket connectionSock, String hostname, ServerResourceList resourcelist,
			ArrayList<String> serverrecordslist) {
		CommandParse.secret = secret;
		CommandParse.hostname = hostname;
		CommandParse.connectionSock = connectionSock;
		this.resourcelist = resourcelist;
		this.serverrecordslist = serverrecordslist;
	}

	
	public JSONObject ParsePUBLISHSHARE(JSONObject command) 
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
			  ||(command.get("command").equals("SHARE") && resourcelist.ShareResource(temp))){
				jsonobject.put("response", "success");
				return jsonobject;
			}else{
				jsonobject.put("response", "error");
				jsonobject.put("errorMessage", "cannot " +  command.get("command") + " resource");
				return jsonobject;
			}
		}
		

	}

	public ArrayList<JSONObject> ParseQUERY(JSONObject command) {
		
			ArrayList<JSONObject> jsonlist = new ArrayList<JSONObject>();
			ArrayList<Resource> resourcefound = new ArrayList<Resource>();
			ArrayList<JSONObject> forwardResource = new ArrayList<JSONObject>();
			JSONObject jsontemp = new JSONObject();
			
			Resource temp_1 = new Resource();
			StoreResourceInfo(command, temp_1);
			resourcefound = resourcelist.queryResource(temp_1, false);
			if( !resourcefound.isEmpty()){
				jsontemp.put("response", "success");
				jsonlist.add((JSONObject)jsontemp.clone());
				jsontemp.clear();
				for(Resource temp_2 : resourcefound){   // print the resource information
					jsontemp.put("name", temp_2.resource_name);		
					jsontemp.put("tags", temp_2.resource_tags);
					jsontemp.put("description",  temp_2.resource_description);
					jsontemp.put("uri", temp_2.resource_uri);
					jsontemp.put("channel", temp_2.channel);
					jsontemp.put("owner", temp_2.owner);
					jsontemp.put("ezserver", temp_2.ezserver);
					jsonlist.add((JSONObject)jsontemp.clone());
					jsontemp.clear();
				}
				jsontemp.put("resultSize", jsonlist.size()-1);
				jsonlist.add((JSONObject) jsontemp.clone());
				jsontemp.clear();
				
			}else{
			    jsontemp.put("response", "success");
			    jsonlist.add((JSONObject) jsontemp.clone());
			    jsontemp.clear();
			    jsontemp.put("resultSize", 0);
			    jsonlist.add((JSONObject) jsontemp.clone());
			    jsontemp.clear();
			}
			
			if(command.get("relay").equals("true")){
				//when the relay area is true.
				JSONObject newCommand = (JSONObject)command.clone();
				newCommand.remove("relay");
				newCommand.put("relay", "false");
				JSONObject resource = (JSONObject)(newCommand.get("resourceTemplate"));
				resource.remove("channel");
				resource.put("channel", "");
				resource.remove("owner");
				resource.put("owner", "");
				forwardResource = ForwardQuery(newCommand);
				
				forwardResource.remove(0);
				forwardResource.remove(forwardResource.size() - 1);
				jsonlist.remove(jsonlist.size() - 1);
				for(JSONObject x: forwardResource){
					jsonlist.add((JSONObject)x.clone());
				}
				jsontemp.put("resultSize", jsonlist.size() - 1);
				jsonlist.add((JSONObject) jsontemp.clone());
				
			}
			
			return jsonlist;

	}
	
	public ArrayList<JSONObject> ParseFETCH(JSONObject command) throws URISyntaxException{
		ArrayList<JSONObject> jsonlist = new ArrayList<JSONObject>();
		JSONObject jsonobject = new JSONObject();
		JSONObject returnmap = new JSONObject();
		JSONObject tempobject;
		int resultSize;
		long resourceSize;
		
		Resource temp = new Resource();
		if(((HashMap) command.get("resourceTemplate")).get("uri") == null||!((HashMap) command.get("resourceTemplate")).get("uri") .toString().contains("file:")){
			jsonobject.put("response", "success");
			jsonlist.add((JSONObject) jsonobject.clone());
			jsonobject.clear();
			jsonobject.put("resultSize",0);
			jsonlist.add((JSONObject) jsonobject.clone());
			jsonobject.clear();
			return jsonlist;
		}
		if((tempobject = StoreResourceInfo(command, temp)) != null){
			jsonlist.add(tempobject);
			return jsonlist;
		}
		else {
			returnmap=resourcelist.fetchResource(temp);
			resourceSize=Long.parseLong(returnmap.get("resource_size").toString());
			jsonobject.put("response", "success");
			jsonlist.add((JSONObject) jsonobject.clone());
			jsonobject.clear();
			jsonobject.put("name", temp.resource_name);		
			jsonobject.put("tags", temp.resource_tags);
			jsonobject.put("description",  temp.resource_description);
			jsonobject.put("uri", temp.resource_uri);
			jsonobject.put("channel", temp.channel);
			jsonobject.put("owner", temp.owner);
			jsonobject.put("ezserver", temp.ezserver);
			jsonobject.put("resourceSize", resourceSize);
			jsonlist.add((JSONObject) jsonobject.clone());
			jsonobject.clear();
			
			if(resourceSize>0){
				returnmap.remove("result_size");
				returnmap.remove("resource_size");
				jsonlist.add((JSONObject) returnmap.clone());
			}
			jsonobject.put("resultSize", "1");
			jsonlist.add((JSONObject) jsonobject.clone());
			jsonobject.clear();
			return jsonlist;
		}
	}
	
	public JSONObject ParseREMOVE(JSONObject command){
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
	
	public JSONObject ParseEXCHANGE(JSONObject command){
		
		JSONObject jsonobject = new JSONObject();
		
		if(command.get("serverList") != null){
			JSONArray array = (JSONArray)(command.get("serverList"));
			for(int i = 0; i < array.size(); i++) {
			    JSONObject object = (JSONObject) array.get(i);
			    String item = object.get("hostname").toString() + ":" + object.get("port").toString();
			    if(!this.serverrecordslist.contains(item)){
			    	this.serverrecordslist.add(item);
			    }
			}
			jsonobject.put("response", "success");
		}else{
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage","missing or invalid server list");
		}
		
		return jsonobject;
	}
	
	private ArrayList<JSONObject> ForwardQuery(JSONObject command){
		
		String nextHost;
		int nextPort;
		ArrayList<JSONObject> arraylist = new ArrayList<JSONObject>();
		int count = 0;
		boolean x;
		JSONObject temporary = new JSONObject();
		
		if(!serverrecordslist.isEmpty()){
			for(String temp: serverrecordslist){
				String[] item = temp.split(":", 2);
				nextHost = item[0];
				nextPort = Integer.parseInt(item[1]);
				try{
					Socket client = new Socket(nextHost, nextPort);
					OutputStream outToServer = client.getOutputStream();
			        DataOutputStream out = new DataOutputStream(outToServer);
			        out.writeUTF(command.toJSONString());
			        InputStream fromServer = client.getInputStream();
			        DataInputStream in = new DataInputStream(fromServer);
			        JSONParser parser = new JSONParser();
			        x = true;
			        //Parse the string to the JSONObject
			        
			        while(in.available() > 0){
			        	//String piece = in.readUTF();
			        	if(x){
			        		x = false;
			        		String first = in.readUTF();
			        		continue;
			        	}else{
			        		JSONObject response = (JSONObject) parser.parse(in.readUTF());
			        		arraylist.add((JSONObject) response.clone());
			        	}
			        	
			        }
			        arraylist.remove(arraylist.size() - 1);
			        //close the socket
			        client.close();
				}catch(IOException | ParseException e){
					e.printStackTrace();
				}	
			}
			
			temporary.put("response", "success");
			arraylist.add(0, (JSONObject)temporary.clone());
			temporary.clear();
			temporary.put("resultSize", arraylist.size() - 1);
			arraylist.add((JSONObject)temporary.clone());
			temporary.clear();
		}else{
			temporary.put("response", "success");
			arraylist.add((JSONObject)temporary.clone());
			temporary.clear();
			temporary.put("resultSize", 0);
			arraylist.add((JSONObject)temporary.clone());
			temporary.clear();
		}
		
	
		return arraylist;
	}
	
	private static JSONObject StoreResourceInfo(JSONObject command,Resource resource){
		JSONObject jsonobject = new JSONObject();
		Resource temp = resource;
		JSONObject subcommand;
		
		if(command.get("command").equals("QUERY")||command.get("command").equals("FETCH")){
			subcommand = (JSONObject) command.get("resourceTemplate");
		}else{
			subcommand = (JSONObject) command.get("resource");
		}
		
		
		temp.resource_name = (subcommand.get("name") != null) ? ((String)subcommand.get("name")).trim() : "";
		temp.resource_description = (subcommand.get("description") != null) ? 
				((String)subcommand.get("description")).trim() : "";
		temp.resource_tags = (subcommand.get("tags") != null) ? subcommand.get("tags").toString().trim() : "";
		temp.channel = (subcommand.get("channel") != null) ? ((String)subcommand.get("channel")).trim() : "";
		temp.owner = (subcommand.get("owner") != null) ? ((String)subcommand.get("owner")).trim() : "";
		temp.ezserver = hostname +" : " + connectionSock.getPort();
		
		if(temp.owner.equals("*")){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "invaild resource");
			return jsonobject;
		}
		
		if(command.get("command").equals("SHARE") && !command.get("secret").equals(secret)){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "incorrect secret");
			return jsonobject;
		}
		
		if(subcommand.get("uri") == null && !command.get("command").equals("QUERY")){
			jsonobject.put("response", "error");
			jsonobject.put("errorMessage", "missing resource");
			return jsonobject;
		}else if(command.get("command").equals("QUERY")){
			temp.resource_uri = (subcommand.get("uri") != null) ? (String) subcommand.get("uri") : "";
		}else{
			
			try {
				URI uri = new URI((String) subcommand.get("uri"));
				//File f = new File(/*"F:" + */uri.getPath());
				
				if((uri.getScheme().equals("file") && ((String)command.get("command")).equals("publish"))
					||(!uri.getScheme().equals("file") && ((String)command.get("command")).equals("share"))
					||(((String)command.get("command")).equals("share") && (!uri.isAbsolute() || uri.getAuthority() != null))){
					
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
	}
	
	
}
