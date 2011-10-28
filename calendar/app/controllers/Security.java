package controllers;

import models.*;

public class Security extends Secure.Security {
	static boolean authenticate(String username, String password) {
	    return User.connect(username, password) != null;
	}
	
	static boolean check(SingleEvent event) {
		if(event != null) {
			return User.find("byEmail", connected()).first().equals(event.origin.owner);
		}
		return false;
	}
	
	static boolean check(Calendar calendar) {
		if(calendar != null) {
			return User.find("byEmail", connected()).first().equals(calendar.owner);
		}
		return false;
	}
	
	static void onDisconnected() {
		Application.index();
	}
	
	static void onAuthenticated() {
	    Application.index();
	}
}
