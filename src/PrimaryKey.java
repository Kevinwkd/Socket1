/**************************************************************************
 * This class is the primary key of a resource, it consists of three fields:
 * channel, owner and uri. A primary can identify only one specific resource
 *  @author Team Tiger
 *  @since 2017-4-28
 **************************************************************************/
public class PrimaryKey {
	
	public String channel;
	public String owner;
	public String uri;

	public PrimaryKey(String channel, String owner, String uri) {
		this.channel = channel;
		this.owner = owner;
		this.uri = uri;
	}

}
