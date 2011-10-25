package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Query;

import org.joda.time.DateTime;

import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public class Location extends Model{
	public String street;
	public String num;
	public String city;
	public String country;
	public String pincode;

	@Override
	public String toString(){
		return street + " " + num + ", " + pincode + " " + city + ", " + country;
	}
	

	public List<Event> getAllEvents() {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 ");
		query.setParameter(1, this);
		return query.getResultList();
	}
	
	public long numberOfAllEvents() {
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 ");
		query.setParameter(1, this);
		return (Long) query.getSingleResult();
	}
	
	public List<Event> getVisibleEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		return query.getResultList();
	}
	
	public long numberOfVisibleEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1  " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		return (Long) query.getSingleResult();
	}
	
	public List<Event> getEventsByDay(DateTime day, User visitor) {
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
	
	public long numberOfEventsByDay(DateTime day) {
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
	
	public long getEventsByDayAndTime(DateTime start, DateTime end, User visitor) {		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		return (Long) query.getSingleResult();
	}
	
	public long numberOfEventsByDayAndTime(DateTime start, DateTime end) {		
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND e.endDate >= ?2 " +
				"AND e.startDate < ?3");
		query.setParameter(1, this);
		query.setParameter(2, start);
		query.setParameter(3, end);
		return (Long) query.getSingleResult();
	}
	
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