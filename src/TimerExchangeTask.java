//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
//import java.util.Timer;
import java.util.TimerTask;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TimerExchangeTask extends TimerTask {
	
	private ArrayList<String> serverList;
	
	public TimerExchangeTask(ArrayList<String> serverList){
		this.serverList = serverList;
	}
	
	@Override
	public void run(){
		//select a host randomly
		int size = serverList.size();
		int number = (int)((Math.random())*size);
		String selected = serverList.get(number);//The selected hostname and port	
		String[] nextServer = selected.split(":", 2);
		String serverName = nextServer[0];
		int port = Integer.parseInt(nextServer[1]);

		//send the exchange command
		JSONObject command = new JSONObject();
		JSONObject subcommand = new JSONObject();
		JSONArray array = new JSONArray();
		
		for(String temp: serverList){
			String[] item = temp.split(":", 2);
			subcommand.put("hostname", item[0]);
			subcommand.put("port", item[1]);
			array.add(subcommand);
			subcommand.clear();
		}
		
		command.put("command", "exchange");
		command.put("serverList", array);
		
		//establish the connection and send the JSONcommand.
		try{
			Socket client = new Socket(serverName,port);
			if(client != null){
		        OutputStream outToServer = client.getOutputStream();
		        DataOutputStream out = new DataOutputStream(outToServer);
//		        out.writeUTF(command.toString());
		        out.writeUTF(command.toJSONString());
		        out.flush();
		        client.close();
			}
			
		}catch(IOException e){
			serverList.remove(number);
			e.printStackTrace();
		}
		
	}

}
