package models;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Calendar {
	private List<Event> events;
	private String name;
	private User owner;
	
	public Calendar(String name, User owner) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	public void addEvent(String name, Date startDate, Date endDate, boolean privacy) throws InvalidEventException {
		addEvent(new Event(name, startDate, endDate, privacy));
	}
	
	public void addEvent(Event e) {
		events.add(e);
		Collections.sort(events);
	}

	public LinkedList<Event> getListForDate(User user, Date date) {
		LinkedList<Event> list = new LinkedList<Event>();
		
		for(Event e : events)
			if(e.isThisDay(date) && (isVisible(user, e)))
				list.add(e);
		
		return list;
	}
	
	public boolean isVisible(User user, Event event) {
		return events.contains(event) && !event.isPrivate() || user == owner;
	}

	public Iterator<Event> getIteratorForUser(User user, Date date) {
		LinkedList<Event> list = new LinkedList<Event>();
		
		for(Event e : events)
			if(e.getEndDate().after(date) && isVisible(user, e))
				list.add(e);
		
		return list.iterator();
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Event> getEvents() {
		return events;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for(Event e : events)
			sb.append(e.toString()+"\n");
		
		return sb.toString();
	}
}