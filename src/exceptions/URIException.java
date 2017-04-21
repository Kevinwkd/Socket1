package exceptions;

import org.json.simple.JSONObject;

public class URIException extends Exception {

	public URIException() {
		super("Missing URI");	
	}
	
}
