package models;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;

import controllers.Events;

import play.db.jpa.JPA;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The Calendar class represents a system of organizing days for social,
 * religious, commercial, or administrative purposes.
 * <p>
 * Calendars have a name, an owner and zero or more {@link Event}s. Owner is the {@link User}
 * who possesses the calendar.
 * <p>
 * The class Calendar includes methods for:
 * <ul>
 * <li>deleting the calendar
 * <li>showing the calendar's events at a specific day to a user 
 * <li>showing events at a specific day and a specific {@link Location} to a user
 * <li>showing events at a specific location to a user
 * <li>showing all events a user is allowed to see
 * <li>representing all days in a specific month
 * </ul>
 * 
 * @since Iteration-1
 * @see Event
 * @see Location
 * @see User
 */

@Entity
public class Calendar extends Model implements Printable{
	/**
	 * This calendar's name.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@Required
	public String name;
	
	/**
	 * User who owns this calendar.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@ManyToOne
	@Required
	public User owner;
	
	/**
	 * List of events which this calendar contains.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@ManyToMany(mappedBy="calendars")
	public List<Event> events;

	public boolean printPrivate;

	public int pages;

	public static Font font = new Font("Times New Roman",Font.PLAIN,12);

	public int ind;

	
	/** 
	 * Calendar's constructor. The default behavior is:
	 * <ul> 
	 * <li>Calendar has a name</li> 
	 * <li>Calendar has an owner</li>
	 * <li>Calendar contains zero or more events</li> 
	 * </ul> 
	 * 
	 * @param owner		user who possesses this calendar
	 * @param name		this calendar's name
	 */
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
		this.pages = 1;
		this.printPrivate = false;
		this.ind = 0;
	}
	
	/**
	 * This method first deletes all events that were initially 
	 * created in this calendar. After that, it deletes this calendar itself.
	 * 
	 * @since 	Iteration-1
	 * @see 	models.Event#origin
	 */
	@Override
	public Calendar delete() {
		// First delete the event - calendar relation
		for(Event e : events) {
			if(e.origin.equals(this))
				e.delete();
			else {
				e.calendars.remove(this);
				e.save();
			}
		}
		
		return super.delete();
	}
		
	/**
	 * Returns a list of all events available in this calendar 
	 * at a specific day for a certain user.
	 * <p>
	 * The event is only visible when the given user is the owner of this event
	 * or the event itself is public.
	 * 
	 * @param 	day			the day to check for events
	 * @param 	visitor		the user who wants to see the events
	 * @return	list of available events in this calendar under the defined constrictions
	 * @see 	Event
	 * @see		User
	 * @since 	Iteration-1
	 */
	public List<SingleEvent> events(User visitor, DateTime day) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		
		return events(visitor, start, end);
	}
	
	
	/**
	 * Returns a list of all events starting between (now + 10min) and (now + 15min)
	 * <p>
	 * @return	list of events in this calendar starting between (now + 10min) and (now + 15min)
	 * @see 	Event
	 * @since 	Iteration-5
	 */
	public List<SingleEvent> eventsRemind() {
		DateTime now= new DateTime();
		DateTime remindTime = now.plusMinutes(10);
		DateTime tillTime = now.plusMinutes(15);
		
		Query query = JPA.em().createQuery("SELECT e FROM SingleEvent e " +
				"WHERE ?1 MEMBER OF e.calendars " +
				"AND (e.startDate >= ?2 " +
				"AND e.startDate < ?3)");
		query.setParameter(1, this);
		query.setParameter(2, remindTime);
		query.setParameter(3, tillTime);
		
		return query.getResultList();
	}
	/**
	 * Returns a list of all events available in this calendar 
	 * at a specific day for a certain user.
	 * <p>
	 * The event is only visible when the given user is the owner of this event
	 * or the event itself is public.
	 * 
	 * @param 	day			the day to check for events
	 * @param	start		the start date (with time) to check for events
	 * @param	end			the end date (with time) to check for events
	 * @param 	visitor		the user who wants to see the events
	 * @return	list of available events in this calendar under the defined constrictions
	 * @see 	Event
	 * @see		User
	 * @since 	Iteration-1
	 */
	public List<SingleEvent> events(User visitor, DateTime start, DateTime end) {
		// Get single events
		Query query = JPA.em().createQuery("SELECT e FROM SingleEvent e " +
				"WHERE ?1 MEMBER OF e.calendars "+
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		
		List<SingleEvent> list = query.getResultList();
		
		// Get Repeating events
		query = JPA.em().createQuery("SELECT e FROM EventSeries e " +
				"WHERE ?1 MEMBER OF e.calendars " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		
		for(EventSeries e : (List<EventSeries>) query.getResultList()) {
			if(e.isThisDay(start))
				list.add(e.createDummyEvent(start));
		}
		
		return list;
	}

	/**
	 * Returns a list of all events available at a specific location for a certain user.
	 * <p>
	 * An event is only visible to the user if he is the owner of that event or the event
	 * itself is public.
	 * 
	 * @param 	visitor	the user who wants to see the events
	 * @param 	loc		the location to check for events
	 * @return list of available events under the defined constraints
	 * @see		Event
	 * @see 	Location
	 * @see		User
	 * @since 	Iteration-1
	 */
	public List<Event> events(User visitor, Location loc) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE (e.isPrivate = false OR e.creator = ?1)" +
				"AND e.location.getLocation().contains(loc.getLocation())");
		query.setParameter(1, visitor);
		return query.getResultList();
	}

	/**
	 * Returns a list of all events available at a specific day and a specific location
	 * for a certain user.
	 * <p>
	 * An event is only visible when the given user is the owner of that event or the
	 * event itself is public.
	 * 
	 * @param 	day		the day to check for events
	 * @param 	visitor	the user who wants to see the events
	 * @param 	loc		the location to check for events
	 * @return	list of available events under the defined constraints
	 * @see		Event
	 * @see 	Location
	 * @see 	User
	 * @since 	Iteration-1
	 */
	public List<SingleEvent> events(User visitor, DateTime day, Location location) {
		List<SingleEvent> list = events(visitor, day);
		List<SingleEvent> copy = new LinkedList<SingleEvent>();
		for(SingleEvent e : list)
			if(e.location.equals(location))
				copy.add(e);
		return copy;
	}
	
	/**
	 * Returns a list of upcoming events in the next 30 days for a specific user.
	 * <p>
	 * The events are only available if the user is the owner of the event
	 * or the event itself is public and it always uses the current time.
	 * 
	 * @param 	user	the user for whom the method checks for available events
	 * @return 	list of available events for the user in the next 30 days
	 * @see		Event
	 * @see 	User
	 * @since 	Iteration-1
	 */
	public List<Event> visibleEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE ?1 MEMBER OF e.calendars " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)" +
				"AND e.startDate > ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, new DateTime());
		query.setParameter(4, new DateTime().plusDays(30));
		return query.getResultList();
	}
	
	/**
	 * Returns a list of all days of a certain month.
	 * 
	 * @param 	dt	the month for which to get the days
	 * @return a list of all days for this month
	 * @since 	Iteration-1
	 */
	public static List<DateTime> getDaysInMonth(DateTime dt) {
		DateTime first = dt.withDayOfMonth(1);
		DateTime last = first.plusMonths(1).minusDays(1);
		List<DateTime> days = new ArrayList<DateTime>(Days.daysBetween(first, last).getDays());
		
		for(DateTime day = first; day.isBefore(last); day = day.plusDays(1))
			days.add(day);
		days.add(last);
		
		return days;
	}
	
	/**
	 * Returns this calendar's name.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the number of all events in this calendar which take place at a certain day and time.
	 * 
	 * @param start		day and time of the date's start which is to be checked for events
	 * @param end		day and time of the date's end which is to be checked for events
	 * @return number of all events in this calendar which take place at a certain day and time
	 * @since Iteration-4
	 */
	public long numberOfAllEventsInCalendarByDayAndTime(DateTime start, DateTime end) {		
		Long number = (long) 0;
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM SingleEvent e "+
				"WHERE ?1 MEMBER OF e.calendars " +
				"AND e.endDate >= ?2 " +
				"AND e.startDate < ?3");
		query.setParameter(1, this);
		query.setParameter(2, start);
		query.setParameter(3, end);
		number = (Long) query.getSingleResult();
		
		// Get Repeating events
		query = JPA.em().createQuery("SELECT e FROM EventSeries e " +
				"WHERE ?1 MEMBER OF e.calendars");
		query.setParameter(1, this);	
		
		for(EventSeries e : (List<EventSeries>) query.getResultList()) {
			if(e.isThisDay(start) && start.isBefore(e.endDate.withYear(end.getYear()).withDayOfYear(end.getDayOfYear())) && end.isAfter(e.startDate.withYear(start.getYear()).withDayOfYear(start.getDayOfYear())))
				number++;
		}
		return number;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public int print(Graphics gfx, PageFormat pageFormat, int pageIndex) {
		if ( pageIndex >= pages)
			return Printable.NO_SUCH_PAGE;
		else{
			int x = 10;
			int y = 50;
			gfx.setFont(new Font("Times New Roman", Font.BOLD, 20));
			//Print name
			gfx.drawString(name, x, y);
			gfx.setFont(new Font("Times New Roman", Font.PLAIN, 10));
			//Print date
			gfx.drawString(new Date().toGMTString().replace("GMT", ""), (int)pageFormat.getWidth()-110, y);
			gfx.setFont(font);
			//Print Lines
			gfx.drawLine(x, y+font.getSize(), (int)pageFormat.getWidth()-x, y+font.getSize());
			gfx.drawLine(x, (int)pageFormat.getHeight()-50, (int)pageFormat.getWidth()-x, (int)pageFormat.getHeight()-50);
			//Print PageIndex
			gfx.drawString(""+pageIndex, (int)pageFormat.getWidth()-50, (int)pageFormat.getHeight()-50+gfx.getFont().getSize()*2);
			//Print events
			printEvents(gfx,pageFormat,x+20,y+50,ind,pageIndex);
			return Printable.PAGE_EXISTS;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void printEvents(Graphics gfx,PageFormat pageFormat, int x, int y, int index, int pageIndex){
		int yTemp = y;
		for(int i = index; i < events.size(); i++){
    		Event e = events.get(i);
    		if(printPrivate || !e.isPrivate){
    			String[] str = new String[6];
    			Date start = e.startDate.toDate();
    			Date end = e.endDate.toDate();
    			if(e.isPrivate)
    				str[0] = e.name+"(private):";
    			else
    				str[0] = e.name+":";
    			str[1] = "     -Starts: "+start.toGMTString().replace("GMT", "");
    			str[2] = "     -Ends: "+end.toGMTString().replace("GMT", "");
    			if(e.location != null)
    				str[3] = "     -Location: "+e.location;
    			else
    				str[3] = "     -Location: -";
    			str[4] = "     -Cycle: "+e.type.toString().toLowerCase();
    			str[5] = "     -Description: "+e.description;
    			gfx.setFont(new Font("Times New Roman", Font.BOLD, 14));
    			for(int j = 0; j < str.length; j+=1){
    				gfx.drawString(str[j],x,yTemp);
    				yTemp += gfx.getFont().getSize();
    				gfx.setFont(font);
    			}
    			yTemp += font.getSize();
    			if(yTemp >= gfx.getClipBounds().height-50){
    				yTemp = y;
    				pages += 1;
    				this.ind = i;
    				break;
    			}
    		}
    	}
	}
}