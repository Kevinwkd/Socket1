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
			if(resource.resource_uri == temp.resource_uri && resource.channel == temp.channel ){
				if(resource.owner == temp.owner) return true;
				else return false;
			}
		}
		publishedlist.add(resource);
		return true;
	}
	
	public void RemoveResource(){
		
	}
	
	public void fetchResource(){
		
	}
	
	private boolean isPublic(){
		return true;
	}
}
