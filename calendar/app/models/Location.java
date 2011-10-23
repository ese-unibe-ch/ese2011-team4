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
		return num + ", " + street + ", " + city + ", " + country + ", " + pincode;
	}
	
	public long numberOfEvents() {
		Query query = JPA.em().createQuery("SELECT COUNT(*) FROM Event e "+
				"WHERE e.location = ?1 ");
		query.setParameter(1, this);
		return (Long) query.getSingleResult();
	}
	
	public List<Event> getEvents(User visitor) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE e.location = ?1 " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2) ");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
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
}