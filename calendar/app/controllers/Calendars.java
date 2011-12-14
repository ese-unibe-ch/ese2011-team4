package controllers;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import models.BirthdayEvent;
import models.Calendar;
import models.EventSeries;
import models.RepeatingEvent;
import models.RepeatingType;
import models.SingleEvent;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Calendars extends Controller {
	
    public static void index(Long userId) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	User user = (User) ((userId==null)?connectedUser:User.findById(userId));
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
        render(calendars, connectedUser, user);
    }
    
    public static void add() {
    	User connectedUser = User.find("email", Security.connected()).first();
	    render(connectedUser);
    }
	
	public static void showCurrentMonth(Long id, boolean print) {
		if(print) {
			print((Calendar)Calendar.findById(id));
		}
		DateTime dt = new DateTime();
		show(id, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
	}

    public static void show(Long id, Integer year, Integer month, Integer day) {
    	// Get calendar
    	Calendar calendar = Calendar.findById(id);
    	assert calendar != null;
    	assert year != null;
    	assert month != null;
    	assert day != null;
    	
    	// Get connected user
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	
    	DateTime dt = new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    	
    	// Get favorites birthday
    	List<BirthdayEvent> birthdays = calendar.birthdays(connectedUser, dt);
    	
    	// Get events
    	List<SingleEvent> events = calendar.events(connectedUser, dt);
    	User calendarOwner = calendar.owner;
    	
    	render(calendar, dt, events, birthdays, connectedUser, calendarOwner);
    }
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	if(Security.check(calendar)) {
	    	calendar.delete();
	    	Calendars.index(calendar.owner.id);
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void addCalendar(String name) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	
    	Calendar calendar = new Calendar(connectedUser, name);
    	if (calendar.validateAndSave())
    		Calendars.index(connectedUser.id);
    	else {
    		params.flash();
    		validation.keep();
    		Calendars.add();
    	}
    }
    
    private static void print(Calendar cal) {
    	PrinterJob pjob = PrinterJob.getPrinterJob();
    	pjob.setJobName(cal.name);
    	PageFormat pf = pjob.defaultPage();
    	cal.pages = (int) Math.max(Calendar.font.getSize()*cal.events.size()/pf.getHeight(),1);
    	cal.printPrivate = cal.owner.equals((User)User.find("byEmail", Security.connected()).first());
	    if ( pjob.printDialog() != false ){
	    	pjob.setPrintable(cal);
	    	try{
	    		pjob.print();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
    }
}
