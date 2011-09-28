package models;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class User {
	private String name;

	public User(String name) {
		this.name = name;
	}

	public Calendar createCalendar(String name) {
		return new Calendar(name,  this);
	}
	
	public void createEvent(Calendar cal, String name, Date startDate, Date endDate, boolean isPrivate) throws EndDateBeforeStartDateException {
		cal.addEvent(name, startDate, endDate, isPrivate);
	}
	
	public Iterator<Event> getIterator(Calendar cal, Date date) {
		return cal.getIteratorForUser(this, date);
	}

	@Override
	public String toString() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Event> getList(Calendar cal, Date date) {
		return cal.getListForDate(this, date);
	}
}
