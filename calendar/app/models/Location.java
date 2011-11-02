package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Query;

import org.joda.time.DateTime;

import play.db.jpa.JPA;
import play.db.jpa.Model;


/**
 * The Location class represents a position on earth.
 * <p>
 * A Location has a street, a street number, a city, a country and a pincode. Furthermore a location can
 * contain zero or more events which take place at this location
 * <p>
 * The class Location includes methods for
 * <ul>
 * <li>providing a list of all events which take place at this location</li>
 * <li>providing a list of all events which take place at this location and are public</li>
 * <li>providing a list of all events which take place at this location at a specific date and are
 * visible for a certain user</li>
 * <li>providing a list of all upcoming events which are visible for a certain user</li>
 * <li>providing the number of all events which take place at this location</li>
 * <li>providing the number of all upcoming events which take place at this location</li>
 * <li>providing the number of all events which take place at this location and are
 *visible for a certain user</li>
 * <li>providing the number of all events which take place at this location at a certain day</li>
 * <li>providing the number of all events which take place at this location at a certain day and time</li>
 * <li>providing the number of all events which take place at this location at a certain day
 * and are visible for a certain user</li>
 * <li>searching for a certain location</li>
 * </ul>
 * 
 * @since Iteration-2
 * @see User
 */
@Entity
public class Location extends Model{
	
	
	/**
	 * This location's street.
	 */
	public String street;
	
	/**:
	 * This location's street number.
	 */
	public String num;
	
	/**
	 * City of this location.
	 */
	public String city;
	
	/**
	 * Country of this location.
	 */
	public String country;
	
	/**
	 * Zip code of this location's city.
	 */
	public String pincode;

	
	/**
	 * Returns this locations address written as "street number pincode city country".
	 * 
	 * @return this location's address
	 * @since iteration-2
	 */
	@Override
	public String toString(){
		return street + " " + num + ", " + pincode + " " + city + ", " + country;
	}
	

	/**
	 * Returns a list of all events taking place at this location.
	 * 
	 * @return a list of all events taking place at this location.
	 * @since Iteration-2
	 */
	public List<Event> getAllEvents() {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 ");
		query.setParameter(1, this);
		return query.getResultList();
	}
	
	
	/**
	 * Returns the number of all events taking place at this location.
	 * 
	 * @return number of all events taking place at this location.
	 * @since Iteration-2
	 */
	public long numberOfAllEvents() {
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 ");
		query.setParameter(1, this);
		return (Long) query.getSingleResult();
	}
	
	
	/**
	 * Returns a list of all events which take place at this location and are
	 * visible for a certain user.
	 * <p>
	 * An event is visible for a user if the user is the owner of this event or
	 * the event is public.
	 * 
	 * @param visitor	user who wants to see the events at this location
	 * @return <code>List<Event></code> of all events which take place at this location
	 * and are visible for a certain user
	 * @since Iteration-2
	 * @see User
	 */
	public List<Event> getVisibleEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		return query.getResultList();
	}
	
	
	/**
	 * Returns the number of all events which take place at this location and are
	 * visible for a certain user.
	 * <p>
	 * An event is visible for a user if the user is the owner of this event or
	 * the event is public.
	 * 
	 * @param visitor	user who wants to see the events at this location
	 * @return number of all events which take place at this location
	 * and are visible for a certain user
	 * @since Iteration-2
	 * @see User
	 */
	public long numberOfVisibleEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1  " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		return (Long) query.getSingleResult();
	}
	
	
	/**
	 * Returns a list of all events which take place at this location at a certain day and are
	 * visible for a certain user.
	 * <p>
	 * An event is visible for a user if the user is the owner of this event or
	 * the event is public.
	 * 
	 * @param day		day to check for visible events at this location
	 * @param visitor	user who wants to see the events at this location
	 * @return <code>List<Event></code> of all events which take place at this location at a certain day
	 * and are visible for a certain user
	 * @since Iteration-2
	 * @see User
	 */
	public List<Event> getVisibleEventsByDay(DateTime day, User visitor) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		return query.getResultList();
	}
	
	
	/**
	 * Returns the number of all events which take place at this location at a certain day.
	 * 
	 * @param day	day to check for events at this location
	 * @return number of all events which take place at this location at a certain day
	 * @since Iteration-2
	 */
	public long numberOfAllEventsByDay(DateTime day) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND e.endDate >= ?2 " +
				"AND e.startDate < ?3");
		query.setParameter(1, this);
		query.setParameter(2, start);
		query.setParameter(3, end);
		return (Long) query.getSingleResult();
	}
	
	
	/**
	 * Returns a list of all events which take place at this location at a certain day and time 
	 * and which are visible for a certain user.
	 * <p>
	 * An event is visible for a user if the user is the owner of this event or
	 * the event is public.
	 * 
	 * @param start		day and time of the date's start which is to be checked for events
	 * @param end		day and time of the date's end which is to be checked for events
	 * @param visitor	user who wants to see the events at this location
	 * @return <code>List<Event></code> of all events which take place at this location at the specific
	 * day and time
	 * and are visible for a certain user
	 * @since Iteration-2
	 * @see User
	 */
	public List<Event> getVisibleEventsByDayAndTime(DateTime start, DateTime end, User visitor) {		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		return query.getResultList();
	}
	
	
	/**
	 * Returns the number of all events which take place at this location at a certain day and time.
	 * 
	 * @param start		day and time of the date's start which is to be checked for events
	 * @param end		day and time of the date's end which is to be checked for events
	 * @return number of all events which take place at this location at the specific day and time
	 * @since Iteration-2
	 */
	public long numberOfAllEventsByDayAndTime(DateTime start, DateTime end) {		
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND e.endDate >= ?2 " +
				"AND e.startDate < ?3");
		query.setParameter(1, this);
		query.setParameter(2, start);
		query.setParameter(3, end);
		return (Long) query.getSingleResult();
	}
	
	
	/**
	 * Returns a list of all upcoming events which take place at this location and are visible
	 * for a certain user.
	 * <p>
	 * An event is visible for a user if the user is the owner of this event or
	 * the event is public.
	 * <p>
	 * This method first generates a new date of the actual day and time and then 
	 * checks which events will take place at this location from the generated date on.
	 * 
	 * @param visitor	user who wants to see the events at this location
	 * @return <code>List<Event></code> of all upcoming events which take place at this location
	 * and are visible for a certain user
	 * @since Iteration-2
	 * @see User
	 */
	public List<Event> getVisibleUpcomingEvents(User visitor) {		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate > ?3");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, new DateTime());
		return query.getResultList();
	}
	
	
	/**
	 * Returns the number of all upcoming events at this location.
	 * <p>
	 * This method first generates a new date of the actual day and time and then 
	 * checks which events will take place at this location from the generated date on.
	 * 
	 * @return number of all upcoming events at this location
	 * @since Iteration-2
	 */
	public long numberOfAllUpcomingEvents() {		
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND e.endDate > ?2 ");
		query.setParameter(1, this);
		query.setParameter(2, new DateTime());
		return (Long) query.getSingleResult();
	}
	
	
	/**
	 * Returns the searched location, if it exists.
	 * <p>
	 * All argument data have to match the specific location.
	 * 
	 * @param street	street of the searched location
	 * @param num		street number of the searched location
	 * @param city		city of the searched location
	 * @param country	country of the searched location
	 * @param pincode	pincode of the searched location
	 * @return the searched location, if it exists; null if the location doesn't exist. 
	 * @since Iteration-2
	 */
	public static Location find(String street, String num, String city, String country, String pincode) {
		Query query = JPA.em().createQuery("SELECT e FROM Location e "+
				"WHERE UPPER(e.street) = UPPER(?1) " +
				"AND UPPER(e.num) = UPPER(?2) " +
				"AND UPPER(e.city) = UPPER(?3) " +
				"AND UPPER(e.country) = UPPER(?4) " +
				"AND UPPER(e.pincode) = UPPER(?5)");
		query.setParameter(1, street.trim());
		query.setParameter(2, num.trim());
		query.setParameter(3, city.trim());
		query.setParameter(4, country.trim());
		query.setParameter(5, pincode.trim());
		query.setMaxResults(1);
		return (query.getResultList().size() > 0)?(Location) query.getResultList().get(0):null;
	}
	
}