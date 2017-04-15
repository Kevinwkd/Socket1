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

public class ClientHandler implements Runnable {
	public int name;
	public Date now;

	public Socket connectionSock;
	
	public DataInputStream clientInput;
	public DataOutputStream clientOutput;
	
	public ClientHandler(Socket connectionSock, int name){
		now = new Date();
		this.connectionSock = connectionSock;
		this.name = name;
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
			String fileName = clientInput.readUTF();
			System.out.println(fileName);
			long fileLength = clientInput.readLong();
			System.out.println(fileLength);
			
			Option uri = Option.builder("uri")
					.desc("specify the location")
					.hasArg()
					.argName("uri")
					.build();
			Option host = Option.builder("host")
					.desc("specify the ip address")
					.hasArg()
					.argName("ip address")
					.build();
			Options options = new Options();
			options.addOption(uri);
			options.addOption(host);
			
			
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

			
			fs.close();
			

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int parseCommand(JSONObject command){
		int result = 0;
		
		if(command.get("command name").equals("xxxxxxx")){
			int x = Integer.parseInt(command.get("xxxxxxx").toString());
			int y = Integer.parseInt(command.get("xxxxxxx").toString());
			
			switch (command.get("method_name").toString()){
				
			}

		}
		
		
		return result;
	}
	
}


