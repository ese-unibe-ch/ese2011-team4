package models;

@SuppressWarnings("serial")
public class InvalidEventException extends Exception {
	public String msg;
	
	public InvalidEventException(String msg) {
		super();
		this.msg = msg;
	}
}
