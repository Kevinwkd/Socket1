import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

public class ServerResourceList {

	//public ArrayList<Resource> publishedlist;
	Hashtable<PrimaryKey, Resource> publishedList;
		
	public ServerResourceList() {
		// TODO Auto-generated constructor stub
		//this.publishedlist = new ArrayList<Resource>();
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
		File f = new File(uri.getPath());
		if(f.exists()){
			PrimaryKey v = new PrimaryKey(resource.channel, resource.owner, resource.resource_uri);
			publishedList.put(v, resource);
			return true;
		}else{ return false; }
		
		
		
			
	}

	public boolean PublishResource(Resource resource){
		/*for(Resource temp : this.publishedlist){
			if(resource.resource_uri.equals(temp.resource_uri) && resource.channel.equals(temp.channel)){
				if(resource.owner == temp.owner) return true;
				else return false;
			}
		}
		if(ResourceList.containsKey(resource.channel) 
		   && ResourceList.get(resource.channel).resource_uri.equals(resource.resource_uri)){
		   if(ResourceList.get(resource.channel).owner.equals(resource.owner)){
				ResourceList.remove(resource.channel);
				ResourceList.put(resource.channel, resource);
				return true;
			}else{ return false; }
		}*/
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
	
	public void fetchResource(Resource resource){
		
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
