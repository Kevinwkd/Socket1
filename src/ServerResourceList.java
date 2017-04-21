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
	
	public void ShareResource(){
		
	}

	public boolean publishresource(Resource resource){
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
