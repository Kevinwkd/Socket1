import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class ServerResourceList {

	private ArrayList<Resource> completedlist;
	
	public ArrayList<Resource> publishedlist;
	
	
	
	public ServerResourceList() {
		// TODO Auto-generated constructor stub
		this.publishedlist = new ArrayList<Resource>();
		this.completedlist = new ArrayList<Resource>();
	}
	
	public boolean ShareResource(Resource resource) throws URISyntaxException{
		for(Resource temp : this.publishedlist){
			if(resource.resource_uri == temp.resource_uri && resource.channel == temp.channel ){
				if(resource.owner == temp.owner) return true;
				else return false;
			}
		}
		URI uri = new URI(resource.resource_uri);
		File f = new File(uri.getPath());
		if(f.exists()){
			publishedlist.add(resource);
		}else{ return false; }
		
		return true;
	}

	public boolean PublishResource(Resource resource){
		for(Resource temp : this.publishedlist){
			if(resource.resource_uri.equals(temp.resource_uri) && resource.channel.equals(temp.channel)){
				if(resource.owner == temp.owner) return true;
				else return false;
			}
		}
		publishedlist.add(resource);
		return true;
	}
	
	public boolean RemoveResource(Resource resource){
		for(Resource temp : this.publishedlist){
			if(resource.channel.equals(temp.channel) && resource.owner.equals(temp.owner)
			  && resource.resource_uri.equals(temp.resource_uri)){
				publishedlist.remove(temp);
				return true;
			}
		}
		return false;
	}
	
	public void fetchResource(){
		
	}
	
	public ArrayList<Resource> queryResource(Resource resource, boolean relay){
		ArrayList<Resource> res = new ArrayList<Resource>();
		for(Resource temp : this.publishedlist){
			/*if(!resource.channel.isEmpty() && resource.channel.equals(temp.channel)){
				if(!resource.owner.isEmpty() && resource.owner.equals(temp.owner)){
					if(!resource.resource_tags.isEmpty() && resource.resource_tags.contains(temp.resource_tags)){
						if(!resource.resource_uri.isEmpty() && resource.resource_uri.equals(temp.resource_uri)){
							if((!resource.resource_name.isEmpty() && resource.resource_name.contains(temp.resource_name)) 
								|| (!resource.resource_description.isEmpty() && resource.resource_description.contains(temp.resource_description)) 
								|| (resource.resource_name.isEmpty() && resource.resource_description.isEmpty())){
										  
							}else if(!resource.resource_name.contains(temp.resource_name)
									 ||!resource.resource_description.contains(temp.resource_description)){
								continue;
							}
							res.add(temp);
						}else if(!resource.resource_uri.equals(temp.resource_uri)){
							continue;
						}
						res.add(temp);
					}else if(!resource.resource_tags.contains(temp.resource_tags)){
						continue;
					}
					res.add(temp);
				}else if(!resource.owner.equals(temp.owner)){
					continue;
				}
				res.add(temp);
			}else if(!resource.channel.equals(temp.channel)){
				continue;
			}else if(resource.EmptyValue()){
				res.add(temp);
			}*/
			if((!resource.channel.isEmpty() && resource.channel.equals(temp.channel))
			  && (!resource.owner.isEmpty() && resource.owner.equals(temp.owner))
			  && (!resource.resource_tags.isEmpty() && resource.resource_tags.contains(temp.resource_tags))
			  && (!resource.resource_uri.isEmpty() && resource.resource_uri.equals(temp.resource_uri))
			  && ((!resource.resource_name.isEmpty() && resource.resource_name.contains(temp.resource_name)) 
				  || (!resource.resource_description.isEmpty() && resource.resource_description.contains(temp.resource_description)) 
				  || (resource.resource_name.isEmpty() && resource.resource_description.isEmpty()))){
				res.add(temp);

			}else if(resource.resource_tags.equals("")){
				res.add(temp);
			}else if(temp.EmptyValue()){
				res.add(temp);
			}
		}
		return res;
		

	}
	

}
