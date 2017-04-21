
public class Resource {
	
	public String resource_name;
	public String resource_description;
	public String resource_uri;
	public String channel;
	public String owner;
	public String ezserver;
	public String resourceSize;
	public String resource_tags;
	
	
	public Resource() {
		// TODO Auto-generated constructor stub
		//this.ezserver = 
	}
		

	public String getResource_tags() {
		return resource_tags;
	}



	public void setResource_tags(String resource_tags) {
		this.resource_tags = resource_tags;
	}


	
	public String getResource_name() {
		return resource_name;
	}



	public void setResource_name(String resource_name) {
		this.resource_name = resource_name;
	}



	public String getResource_description() {
		return resource_description;
	}



	public void setResource_description(String resource_description) {
		this.resource_description = resource_description;
	}



	public String getResource_uri() {
		return resource_uri;
	}



	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}



	public String getChannel() {
		return channel;
	}



	public void setChannel(String channel) {
		this.channel = channel;
	}



	public String getOwner() {
		return owner;
	}



	public void setOwner(String owner) {
		this.owner = owner;
	}



	public String getEzserver() {
		return ezserver;
	}

	
	public String getResourceSize() {
		return resourceSize;
	}



	public void setResourceSize(String resourceSize) {
		this.resourceSize = resourceSize;
	}




	public String toString(){
		return "Resource: name = " +
				resource_name + "tags:" + 
				resource_tags.toString() + "resource description"
				+ "resource_description" + resource_description
				+ "channel:" + channel + "owner" + owner +
				"ezserver:" + ezserver + "resource size" + resourceSize;
		
	}

}
