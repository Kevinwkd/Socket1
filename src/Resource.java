
public class Resource {
	
	public String resource_name;
	public String resource_description;
	public String resource_uri;
	public String channel;
	public String owner;
	public String ezserver;
	public String resource_tags;
	
	
	public Resource() {
		// TODO Auto-generated constructor stub
		//this.ezserver = 
	}
		
	public String toString(){
		return "Resource: name = " +
				resource_name + "tags:" + 
				resource_tags.toString() + "resource description"
				+ "resource_description" + resource_description
				+ "channel:" + channel + "owner" + owner +
				"ezserver:" + ezserver;
		
	}
	
	public boolean FullValue(){
		if(!resource_name.isEmpty() && !resource_description.isEmpty() 
		   && !resource_tags.isEmpty() && !resource_uri.isEmpty()
		   && !channel.isEmpty() && !owner.isEmpty()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean EmptyValue(){
		if(resource_name.isEmpty() && resource_description.isEmpty() 
		   && resource_tags.isEmpty() && resource_uri.isEmpty()
		   && channel.isEmpty() && owner.isEmpty()){
			return true;
		}else{
			return false;
		}
	}

}
