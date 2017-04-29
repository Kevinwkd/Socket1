import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import org.json.simple.JSONObject;

/*************************************************************************
 * This class mainly provides all the operations on the shared/published 
 * resource list
 * @author Team Tiger
 * @since 2017-4-28
 *************************************************************************/
public class ServerResourceList {

	Hashtable<PrimaryKey, Resource> publishedList;
		
	public ServerResourceList() {
		publishedList = new Hashtable<PrimaryKey,  Resource>();
	}
	
	public boolean ShareResource(Resource resource) throws URISyntaxException{

		Set<PrimaryKey> keys = publishedList.keySet();
		for(PrimaryKey temp : keys){
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			   && !resource.resource_uri.equals(temp.uri)){
				return false;
			}
			
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			   && resource.resource_uri.equals(temp.uri)){
				publishedList.put(temp, resource);
				return true;
			}
		}
		
		URI uri = new URI(resource.resource_uri);
		File f = new File(uri.getPath().substring(1, uri.getPath().length()));
		if(f.exists()){
			PrimaryKey v = new PrimaryKey(resource.channel, resource.owner, resource.resource_uri);
			publishedList.put(v, resource);
			return true;
		}else{ return false; }
	}

	public boolean PublishResource(Resource resource){
		Set<PrimaryKey> keys = publishedList.keySet();
		for(PrimaryKey temp : keys){
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			   && !resource.resource_uri.equals(temp.uri)){
				return false;
			}
			
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			   && resource.resource_uri.equals(temp.uri)){
				publishedList.put(temp, resource);
				return true;
			}
		}
		PrimaryKey v = new PrimaryKey(resource.channel, resource.owner, resource.resource_uri);
		publishedList.put(v, resource);
		return true;
	}
	
	public boolean RemoveResource(Resource resource){
		/*for(Resource temp : this.publishedlist){
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			  && resource.resource_uri.equals(temp.resource_uri)){
				publishedlist.remove(temp);
				return true;
			}
		}*/
		
		Set<PrimaryKey> keys = publishedList.keySet();
		for(PrimaryKey temp : keys){
			if(!resource.channel.isEmpty() && ! resource.channel.equals(temp.channel)){
				continue;
			}
			
			if(!resource.owner.isEmpty() && ! resource.owner.equals(temp.owner)){
				continue;
			}
			
			if(!resource.resource_uri.isEmpty() && ! resource.resource_uri.equals(temp.uri)){
				continue;
			}
			publishedList.remove(temp);
			return true;
		}
		return false;
	}
	
	public JSONObject fetchResource(Resource resource) throws URISyntaxException{
		JSONObject trigger = new JSONObject();
		boolean find=false;
		Set<PrimaryKey> keys = publishedList.keySet();
		for(PrimaryKey temp : keys){
			if((resource.channel.equals(null)&&temp.channel.equals(null)||resource.channel.equals(temp.channel))&&resource.resource_uri.equals(temp.uri)){
				find=true;
				break;
			}
		}
		
		if(find){
			URI uri = new URI(resource.resource_uri);
			File f = new File(uri.getPath());
			trigger.put("result_size", 1);
			trigger.put("resource_size",f.length());
			RandomAccessFile byteFile = null;
			try {
				byteFile = new RandomAccessFile(f,"r");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] sendingBuffer = new byte[80*1024];
			int num,line=0;
			// While there are still bytes to send..
			try {
				while((num = byteFile.read(sendingBuffer)) > 0){
					trigger.put(String.valueOf(line),new String(Arrays.copyOf(sendingBuffer, num)));
					line++;
				}
				byteFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			trigger.put("result_size", 0);
		}
		return trigger;
	}
	
	
	public ArrayList<Resource> queryResource(Resource resource, boolean relay){
		ArrayList<Resource> res = new ArrayList<Resource>();
		
		Set<PrimaryKey> keys = publishedList.keySet();
		for(PrimaryKey temp : keys){
			if(!resource.channel.isEmpty() && ! resource.channel.equals(temp.channel)){
				continue;
			}
			
			if(!resource.owner.isEmpty() && ! resource.owner.equals(temp.owner)){
				continue;
			}
			
			if(!resource.resource_uri.isEmpty() && ! resource.resource_uri.equals(temp.uri)){
				continue;
			}
			
			if(!resource.resource_tags.isEmpty() && 
			   !resource.resource_tags.equals(publishedList.get(temp).resource_tags)){
				continue;
			}
			
			if(!resource.resource_name.isEmpty() && !resource.resource_name.contains(publishedList.get(temp).resource_name)
			  ||(!resource.resource_description.isEmpty() && !resource.resource_description.contains(publishedList.get(temp).resource_description))){
				continue;
			}
			res.add(publishedList.get(temp));
		}
		return res;
	}
	
	
}
