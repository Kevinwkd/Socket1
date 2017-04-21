import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

public class Server {
	//the port number server listens to
	public int port = 7654;
	//the server's secret
	private String secret = null; 
	//Server's host name
	public String hostname = null;
	//Connection interval limitation
	public int connectionintervallimit = 0;
	
	public int exchangeinterval = 0;
	//debug flag
	public boolean debugmode = true;
	
	public ServerSocket serverSock;
	public Socket connectionSock;
	
	//Counter to record the connected client number
	public int counter = 0;
	//command line argument
	public String [] Args;
	
	public ServerResourceList resourcelist;
	
	public Server(String [] Args){
		this.Args = Args;
		secret = SecretGenerating();
		resourcelist = new ServerResourceList();
		
	}
	
	public void run(){
		
		
		System.out.println("Starting the EZShare Server");
		Configuration( CommandLineOrganize(), Args);
		System.out.println("started");
		
		
		
		try {
			
			serverSock = new ServerSocket(port);
			
			while(true){
				connectionSock = serverSock.accept();
			
				counter ++;
				ClientHandler handler = new ClientHandler(resourcelist, connectionSock, counter, debugmode);
				new Thread(handler).start();

			}
			//serverSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**************************************************************
	 * This method is used to generate the secret of server
	 * @return the random key
	 **************************************************************/
	private String SecretGenerating(){
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
		String pwd = RandomStringUtils.random( 15, characters );
		return pwd;
	}

	/******************************************************************
	 * This method save all the server command line argument into Apache
	 * Options.
	 * @return  Options options
	 * ****************************************************************/
	public Options CommandLineOrganize(){
		
		Option port = Option.builder("port")
					  .required(false)
					  .hasArg()
					  .desc("specify the port server listens to")
					  .build();
		
		Option secret = Option.builder("secret")
				  		.required(false)
				  		.hasArg()
				  		.desc("specify the server secret")
				  		.build();
		
		Option hostname = Option.builder("advertisedhostname")
				  		  .required(false)
				  		  .hasArg()
				  		  .desc("specify the server host name")
				  		  .build();
		
		Option connection = Option.builder("connectionintervallimit")
				  		    .required(false)
				  		    .hasArg()
				  		    .type(Number.class)
				  		    .desc("specify the connection interval limitation")
				  		    .build();
		
		Option exchnge = Option.builder("exchangeinterval")
				  		 .required(false)
				  		 .hasArg()
				  		 .type(Number.class)
				  		 .desc("specify the exchange interval")
				  		 .build();
		
		Option debug = Option.builder("debug")
		  		 	   .required(false)
		  		 	   .desc("turn on the debug mode")
		  		 	   .build();
		
		Options options = new Options();
		options.addOption(port);
		options.addOption(secret);
		options.addOption(hostname);
		options.addOption(connection);
		options.addOption(hostname);
		options.addOption(exchnge);
		options.addOption(debug);
		
		return options;
		
	}

	/************************************************************************
	 * This method is used to parse server command line
	 * argument and store the configuration variable. 
	 * @param args: the client command line argument
	 * @param options: the Apache Options stores all the command line format
	 * @throws ParseException
	 ************************************************************************/
	public void Configuration(Options options, String[] args){
		
		try {
			
			CommandLineParser parser = new DefaultParser();
			CommandLine cmdLine = parser.parse(options, args);
			
			if(cmdLine.hasOption("port")){
				this.port = Integer.parseInt(cmdLine.getOptionValue("port"));
			}
			
			if(cmdLine.hasOption("secret")){
				this.secret = cmdLine.getOptionValue("secret");
			}
			
			if(cmdLine.hasOption("hostname")){
				this.hostname = cmdLine.getOptionValue("hostname");
			}
			
			if(cmdLine.hasOption("connectionintervallimit")){
				this.connectionintervallimit = Integer.parseInt(
						cmdLine.getOptionValue("connectionintervallimit"));
			}
			
			if(cmdLine.hasOption("exchangeinterval")){
				this.exchangeinterval = Integer.parseInt(
						cmdLine.getOptionValue("exchangeinterval"));
			}
			
			if(cmdLine.hasOption("debug")){
				this.debugmode = true;
			}
			
			if(debugmode){
				System.out.println("using secret:" + secret);
				System.out.println("using advertised hostname:" + hostname);
				System.out.println("bound to port:" + port);
			}
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
