import java.net.Socket;
import java.net.UnknownHostException;
import org.json.simple.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import org.apache.commons.cli.*;

public class client {
	//Server host name
	public String hostname;
	//Server port
	public int port;
	//debug flag
	public Boolean debugmode = false;
	
	public Socket connectionSock;
	//public DataInputStream serverInput;
	public DataInputStream serverInput;
	//public DataOutputStream serverOOutput;
	public DataOutputStream serverOutput;
	
	//store commamd line arguments
	public String [] Args;
	
	public client(String [] Args){
		hostname = "localhost";
		port = 7654;
		this.Args = Args;
	}
	
	/******************************************************************
	 * This method save all the client command line argument into Apache
	 * Options.
	 * @return  Options options
	 * ****************************************************************/
	public Options CommandLineOrganize(){
		
		Option debug = Option.builder("debug")
	  		 	   	   .required(false)
	  		 	   	   .desc("turn on the debug mode")
	  		 	   	   .build();
		
		Option publish = Option.builder("publish")
						.required(false)
						.desc("publish resource on server")
						.build();
		
		Option name = Option.builder("name")
					 .hasArg()
					 .required(false)
					 .desc("resource name")
					 .build();
		
		Option tags = Option.builder("tags")
					 .required(false)
					 .hasArgs()
					 .desc("resource tags, tag1,tag2,tag3,...")
					 .build();
		
		Option description = Option.builder("description")
				 			 .required(false)
				 			 .hasArg()
				 			 .desc("resource description")
				 			 .build();
		
		Option uri = Option.builder("uri")
				 	 .required(false)
				 	 .hasArg()
				 	 .desc("resource URI")
				 	 .build();
		
		Option channel = Option.builder("channel")
						 .required(false)
						 .hasArg()
						 .desc("adding the channel info of resource")
						 .build();
		
		Option owner = Option.builder("owner")
					   .required(false)
					   .hasArg()
					   .desc("adding the owner info of resource")
					   .build();
		
		Option secret = Option.builder("secret")
			  			.required(false)
			  			.hasArg()
			  			.desc("specify the server secret")
			  			.build();
	
		Option serverList = Option.builder("serverList")
		  		  	  .required(false)
		  		  	  .hasArgs()
		  		  	  .desc("server list, host1:port1,host2:port2,...")
		  		  	  .build();
		
		Option exchange = Option.builder("exchange")
	  		  	  		  .required(false)
	  		  	  		  .desc("exchange server list with server")
	  		  	  		  .build();
		
		Option fetch = Option.builder("fetch")
	  	  		       .required(false)
	  	  		       .desc("fetch resources from server ")
	  	  		       .build();
		
		Option remove = Option.builder("remove")
	  		       	    .required(false)
	  		       	    .desc("remove resource from server")
	  		       	    .build();
		
		Option query = Option.builder("query")
		       	       .required(false)
		       	       .desc("query resource from server")
		       	       .build();
		
		Option relay = Option.builder("relay")
	       	       	   .required(false)
	       	       	   .hasArg()
	       	       	   .desc("set relay value")
	       	       	   .build();
		
		Option share = Option.builder("share")
		       	       .required(false)
		       	       .desc("share resource on server")
		       	       .build();
		
		Option port = Option.builder("port")
			  	  .required(false)
			  	  .hasArg()
			  	  .desc("specify the client connects to")
			  	  .build();
		
		Option host = Option.builder("host")
		  		  .required(false)
		  		  .hasArg()
		  		  .desc("server host, a domain name or IP address")
		  		  .build();
		
		
		Options options = new Options();
		
		options.addOption(publish);
		options.addOption(name);
		options.addOption(tags);
		options.addOption(description);
		options.addOption(uri);
		options.addOption(debug);
		options.addOption(owner);
		options.addOption(channel);
		options.addOption(secret);
		options.addOption(serverList);
		options.addOption(exchange);
		options.addOption(fetch);
		options.addOption(remove);
		options.addOption(share);
		options.addOption(query);
		options.addOption(relay);
		options.addOption(host);
		options.addOption(port);
		
		return options;
	}
	
	/****************************************************************************
	 * This method read the resource part of client command line and convert them
	 * into JSON format.
	 * @param cmdLine: the command line parser of the client command line argument
	 * @return the json format object of resource argument
	 ***************************************************************************/
	public JSONObject ResourceTemplateStore(CommandLine cmdLine){
		
		JSONObject subCommand = new JSONObject();
		
		if(cmdLine.hasOption("name")){
			subCommand.put("name", cmdLine.getOptionValue("name"));
		}
		
		if(cmdLine.hasOption("tags")){

			JSONArray arr = new JSONArray();
			for(String temp : cmdLine.getOptionValues("tags")){
				arr.add(temp);
			}
			subCommand.put("tags", arr);
		}

		if(cmdLine.hasOption("description")){
			subCommand.put("description", cmdLine.getOptionValue("description"));
		}
		
		if(cmdLine.hasOption("uri")){
			subCommand.put("uri", cmdLine.getOptionValue("uri"));
		}
		
		if(cmdLine.hasOption("channel")){
			subCommand.put("channel", cmdLine.getOptionValue("channel"));
		}
		
		if(cmdLine.hasOption("owner")){
			subCommand.put("owner", cmdLine.getOptionValue("owner"));
		}
		
		subCommand.put("ezserver", null);
		
		return subCommand;
	}
	
	/*************************************************************************************
	 * This method  parse the connection part of client command line
	 * argument and store the configuration variable.
	 * And parse the client command line argument and convert them into respective
	 * JSON format
	 * @param options: the Options object that stores the format of all the client command line
	 * @param args: the client command line argument
	 * @return newCommand: the JSON format of client command line argument
	 * @throws ParseException
	 ************************************************************************************/
	public JSONObject CommandParse(Options options, String[] args) throws ParseException{
		
		JSONObject newCommand = new JSONObject();
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmdLine = parser.parse(options, args);
		
		if(cmdLine.hasOption("port")){
			this.port = Integer.parseInt(cmdLine.getOptionValue("port"));
		}
		
		if(cmdLine.hasOption("host")){
			this.hostname = cmdLine.getOptionValue("host");
		}
		
		System.out.println("Connecting to server:" + hostname + " on port " + port);
		
		if(cmdLine.hasOption("debug")){
			this.debugmode = true;
		}
		
		if(cmdLine.hasOption("publish")){
			newCommand.put("command", "publish");
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		if(cmdLine.hasOption("share")){
			newCommand.put("command", "share");
			if(cmdLine.hasOption("secret")){
				newCommand.put("secret", cmdLine.getOptionValue("secret"));
			}
			
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		
		if(cmdLine.hasOption("query")){
			newCommand.put("command", "query");
			if(cmdLine.hasOption("relay")){
				newCommand.put("relay", cmdLine.getOptionValue("relay"));
			}else{
				newCommand.put("relay", "true");
			}
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		if(cmdLine.hasOption("remove")){
			newCommand.put("command", "remove");
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		
		return newCommand;
	}
	
	public void run(){
		try {
			Options options = CommandLineOrganize();
			String command = CommandParse(options,Args).toJSONString();
			
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
			
			

			
			/*File file = new File("F:/eclipse-workspace/Socket1/src/text.txt");
			
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
			
			
			fs.close();*/
			
    		serverOutput.writeUTF(command);
    		serverOutput.flush();
    		
    		 while(true){
                 if(serverInput.available() > 0) {
                     String message = serverInput.readUTF();
                     System.out.println(message);
                 }
 		
	    }
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
