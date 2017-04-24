import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ServerResourceList {

	//public ArrayList<Resource> publishedlist;
	Hashtable<String, Resource> ResourceList;
		
	public ServerResourceList() {
		// TODO Auto-generated constructor stub
		//this.publishedlist = new ArrayList<Resource>();
		ResourceList = new Hashtable<String, Resource>();
	}
	
	public boolean ShareResource(Resource resource) throws URISyntaxException{
		/*for(Resource temp : this.publishedlist){
			if(resource.resource_uri.equals(temp.resource_uri) && resource.channel.equals(temp.channel)){
				if(resource.owner.equals(temp.owner)) return true;
				else return false;
			}
		}*/
		if(ResourceList.containsKey(resource.channel) 
		   && ResourceList.get(resource.channel).resource_uri.equals(resource.resource_uri)){
			if(ResourceList.get(resource.channel).owner.equals(resource.owner)){
				ResourceList.remove(resource.channel);
				ResourceList.put(resource.channel, resource);
				return true;
			}else{ return false; }
		}
		URI uri = new URI(resource.resource_uri);
		File f = new File(uri.getPath());
		if(f.exists()){
			//publishedlist.add(resource);
			ResourceList.put(resource.channel, resource);
			return true;
		}else{ return false; }
			
	}

	public boolean PublishResource(Resource resource){
		/*for(Resource temp : this.publishedlist){
			if(resource.resource_uri.equals(temp.resource_uri) && resource.channel.equals(temp.channel)){
				if(resource.owner == temp.owner) return true;
				else return false;
			}
		}*/
		if(ResourceList.containsKey(resource.channel) 
		   && ResourceList.get(resource.channel).resource_uri.equals(resource.resource_uri)){
		   if(ResourceList.get(resource.channel).owner.equals(resource.owner)){
				ResourceList.remove(resource.channel);
				ResourceList.put(resource.channel, resource);
				return true;
			}else{ return false; }
		}
		//publishedlist.add(resource);
		ResourceList.put(resource.channel, resource);
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
		if(ResourceList.containsKey(resource.channel) 
			&& ResourceList.get(resource.channel).resource_uri.equals(resource.resource_uri)
			&& ResourceList.get(resource.channel).owner.equals(resource.owner)){
				ResourceList.remove(resource.channel);
				return true;
		}
		return false;
	}
	
	public void fetchResource(){
		
	}
	
	public ArrayList<Resource> queryResource(Resource resource, boolean relay){
		ArrayList<Resource> res = new ArrayList<Resource>();
		
		/*for(Resource temp : this.publishedlist){
			if(!resource.channel.isEmpty() && !resource.channel.equals(temp.channel)){
				continue;
			}
			
			if(!resource.owner.isEmpty() && !resource.owner.equals(temp.owner)){
				continue;
			}
			
			if(!resource.resource_tags.isEmpty() && !resource.resource_tags.equals(temp.resource_tags)){
				continue;
			}
			
			if(!resource.resource_uri.isEmpty() && !resource.resource_uri.equals(temp.resource_uri)){
				continue;
			}
			
			if((!resource.resource_name.isEmpty() && !resource.resource_name.contains(temp.resource_name)) 
			    || (!resource.resource_description.isEmpty() && !resource.resource_description.contains(temp.resource_description)) ){
				continue;
			}
			
			res.add(temp);
		}*/
		
		if(!ResourceList.containsKey(resource.channel) && !resource.channel.isEmpty()){
			return res;
		}
		
		for(Resource temp : ResourceList.values()){
			if(!resource.owner.isEmpty() && !resource.owner.equals(temp.owner)){
				continue;
			}
			
			if(!resource.resource_tags.isEmpty() && !resource.resource_tags.equals(temp.resource_tags)){
				continue;
			}
			
			if(!resource.resource_uri.isEmpty() && !resource.resource_uri.equals(temp.resource_uri)){
				continue;
			}
			
			if((!resource.resource_name.isEmpty() && !resource.resource_name.contains(temp.resource_name)) 
			    || (!resource.resource_description.isEmpty() && !resource.resource_description.contains(temp.resource_description)) ){
				continue;
			}
			res.add(temp);
		}
		
		return res;
		
		

	}
	

}
