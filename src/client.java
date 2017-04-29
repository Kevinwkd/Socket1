import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
	
	public boolean FetchorNot=false,FileorNot=false;
	
	public String name="";
	
	public client(String [] Args){
		hostname = "localhost";//"sunrise.cis.unimelb.edu.au";
		port = 7654;//3781;
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
	
		Option servers = Option.builder("servers")
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
		options.addOption(servers);
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
		}else{
			subCommand.put("name", "");
		}
		
		if(cmdLine.hasOption("tags")){

			JSONArray arr = new JSONArray();
			for(String temp : cmdLine.getOptionValues("tags")){
				arr.add(temp);
			}
			subCommand.put("tags", arr);
		}else{
			subCommand.put("tags","");
		}

		if(cmdLine.hasOption("description")){
			subCommand.put("description", cmdLine.getOptionValue("description"));
		}else{
			subCommand.put("description", "");
		}
		
		if(cmdLine.hasOption("uri")){
			subCommand.put("uri", cmdLine.getOptionValue("uri"));
			if(cmdLine.getOptionValue("uri").contains("file:")){
				FileorNot=true;
				name=cmdLine.getOptionValue("uri").split("/")[cmdLine.getOptionValue("uri").split("/").length-1];
			}
		}else{
			subCommand.put("uri", "");
		}
		
		if(cmdLine.hasOption("channel")){
			subCommand.put("channel", cmdLine.getOptionValue("channel"));
		}else{
			subCommand.put("channel", "");
		}
		
		if(cmdLine.hasOption("owner")){
			subCommand.put("owner", cmdLine.getOptionValue("owner"));
		}else{
			subCommand.put("owner", "");
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
	public JSONObject CommandParse(Options options, String[] args){
		
		JSONObject newCommand = new JSONObject();
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmdLine;
		try {
			cmdLine = parser.parse(options, args);
		} catch (ParseException e) {
			newCommand.put("command","unknown");
			return newCommand;
		}
		
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
			newCommand.put("command", "PUBLISH");
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		if(cmdLine.hasOption("share")){
			newCommand.put("command", "SHARE");
			if(cmdLine.hasOption("secret")){
				newCommand.put("secret", cmdLine.getOptionValue("secret"));
			}
			
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		
		if(cmdLine.hasOption("query")){
			newCommand.put("command", "QUERY");
			if(cmdLine.hasOption("relay")){
				newCommand.put("relay", cmdLine.getOptionValue("relay"));
			}else{
				newCommand.put("relay", "true");
			}
			newCommand.put("resourceTemplate", ResourceTemplateStore(cmdLine));
		}
		
		if(cmdLine.hasOption("remove")){
			newCommand.put("command", "REMOVE");
			newCommand.put("resource", ResourceTemplateStore(cmdLine));
		}
		
		if(cmdLine.hasOption("exchange")){
			newCommand.put("command", "EXCHANGE");
			if(cmdLine.hasOption("servers")){
				JSONObject subCommand = new JSONObject();
				JSONArray subArray = new JSONArray();
				String list[] = cmdLine.getOptionValues("servers");
				
				for(String temp : list){
			        String array[] = temp.split(":",2);
					subCommand.put("hostname", array[0]);
					subCommand.put("port", array[1]);
					subArray.add(subCommand.clone());
					subCommand.clear();
				}
				
				newCommand.put("serverList", subArray);
				
			}
		}
		
		if(cmdLine.hasOption("fetch")){
			newCommand.put("command", "FETCH");
			newCommand.put("resourceTemplate", ResourceTemplateStore(cmdLine));
			FetchorNot=true;
		}
		
		return newCommand;
	}
	
	public void run(){
		try {
			Options options = CommandLineOrganize();
			String command = CommandParse(options,Args).toJSONString();
			
			connectionSock = new Socket(hostname, port);
			
			//serverOutput.close(); 
			//serverInput.close(); 
			//connectionSock.close(); */
			
			serverOutput = new DataOutputStream(
					connectionSock.getOutputStream());
			serverInput = new DataInputStream(
					connectionSock.getInputStream());
			
			
    		serverOutput.writeUTF(command);
    		serverOutput.flush();
    		
    		boolean Fetchflag=FetchorNot,Fileflag=FileorNot;
			String fileName = name;
			FetchorNot=false;
			FileorNot=false;
			name=null;
			String message;
			
    		 while(true){
    			 if(Fetchflag&&Fileflag&&serverInput.available() > 0){
    				 readFetch(fileName);
    			 }
                 if(serverInput.available() > 0) {
                     message = serverInput.readUTF();
                     System.out.println(message);
                 }
    		 }
    		 
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=80*1024;
		
		// If the file size remaining is less than the chunk size
		// then set the chunk size to be equal to the file size.
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		
		return chunkSize;
	}
	
	public void readFetch(String fileName) throws IOException{
		String message;
		int num=1;
		 message = serverInput.readUTF();
         System.out.println(message);//success or error
         if(message.contains("success")){
        	 message = serverInput.readUTF();
             System.out.println(message);//file and website: resource; not found: resultsize=0
             if(!message.contains("resultSize")){
            	 //exact bytes
     /*       	 while(num>=0){
                     num = serverInput.read();
                     System.out.println(num);
            	 }
                 //resultSize                        
                 System.out.println(serverInput.readUTF());
            	 // The file location*/
					JSONParser parser = new JSONParser();
				JSONObject commandback = null;
				try {
					commandback = (JSONObject) parser.parse(message);
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                  	
						
						// Create a RandomAccessFile to read and write the output file.
						RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");
						// Find out how much size is remaining to get from the server.
						long fileSizeRemaining =Long.parseLong(commandback.get("resourceSize").toString());//Long.parseLong(message);
						
						int chunkSize = setChunkSize(fileSizeRemaining);
						
						// Represents the receiving buffer
						byte[] receiveBuffer = new byte[chunkSize];

                  while((num=serverInput.read(receiveBuffer))>0){
							// Write the received bytes into the RandomAccessFile
							downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
							
							// Reduce the file size left to read..
							fileSizeRemaining-=num;
							
							// Set the chunkSize again
							chunkSize = setChunkSize(fileSizeRemaining);
							receiveBuffer = new byte[chunkSize];
							
							// If you're done then break
							if(fileSizeRemaining==0){
								break;
							}
						}
                  message = serverInput.readUTF();
                  System.out.println(message);
             }
            
         }
	}
}
