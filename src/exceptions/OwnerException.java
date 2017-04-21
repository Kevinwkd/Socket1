package exceptions;

public class OwnerException extends Exception {

	public OwnerException() {
		super("Owner field cannot be \" * \"");
	}

}
