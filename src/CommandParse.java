import java.io.File;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
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

	public ArrayList<JSONObject> ParseQUERY(JSONObject command) {
		
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
	}
	
	
}
