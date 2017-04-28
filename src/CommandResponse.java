import org.json.simple.JSONObject;

public class CommandResponse {

	public CommandResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONObject SuccessResponse(){
		
		JSONObject response = new JSONObject();
		response.put("response", "success");
		return response;
	}
	
	public JSONObject InvaildCommand(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "invaild command");
		return response;
	}
	
	public JSONObject InvaildResource(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "invaild resource");
		return response;
	}
	
	public JSONObject MissingResource(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "missing resource");
		return response;
	}
	
	public JSONObject Cannot(String command){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "cannot " + command.toLowerCase() + "resource");
		return response;
		
	}
	
	public JSONObject IncorrectSecret(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "incorrect secret");
		return response;
	}
	
	public JSONObject InvaildResourceTemplate(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "invaild resourcetemplate");
		return response;
	}
	
	public JSONObject MissingResourceTemplate(){
		
		JSONObject response = new JSONObject();
		response.put("response", "error");
		response.put("errorMessage", "missing resourcetemplate");
		return response;
	}

}
