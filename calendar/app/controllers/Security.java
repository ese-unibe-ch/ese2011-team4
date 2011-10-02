package controllers;

import models.*;

public class Security extends Secure.Security {
	static boolean authenticate(String username, String password) {
	    return User.connect(username, password) != null;
	}
	
	static void onDisconnected() {
		Application.index();
	}
	
	static void onAuthenticated() {
	    Application.index();
	}
}
