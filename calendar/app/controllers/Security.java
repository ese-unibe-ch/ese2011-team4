package controllers;

import models.*;

public class Security extends Secure.Security {
	static boolean authenticate(String username, String password) {
	    return User.connect(username, password) != null;
	}
	
	static boolean check(String profile) {
		if("owner".equals(profile.substring(0, 5))) {
			Calendar calendar = Calendar.findById(Long.valueOf(profile.substring(5)));
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
