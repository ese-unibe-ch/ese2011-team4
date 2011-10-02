package controllers;

import play.*;
import play.mvc.*;

import java.util.List;

import models.*;

public class Application extends Controller {
    public static void index() {
    	List<Calendar> calendars = Calendar.all().fetch();
    	List<User> users = User.all().fetch();
        render(calendars, users);
    }
}