package models;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	public String email;
	public String password;
	public String fullname;
	public boolean isAdmin;

	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
	}
	
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public Calendar createCalendar(String name) {
		return new Calendar(this,  name).save();
	}
	
	public List<Event> getEventsForCalendar(Calendar cal, Date date) {
		return cal.getListForDate(this, date);
	}

	public Iterator<Event> getIterator(Calendar cal, Date date) {
		return cal.getIteratorForUser(this, date);
	}

	@Override
	public String toString() {
		return fullname+"("+email+")";
	}
}
