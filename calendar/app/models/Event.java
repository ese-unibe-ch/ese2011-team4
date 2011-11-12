package models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;


/**
 * The Event class represents a temporary and scheduled happening with a defined 
 * goal or intention, which can be added to calendars. The Event class is abstract
 * and therefor only its implementation can be used, which are {@link SingleEvent}
 * and {@link EventSeries}.
 * <p>
 * An event includes the following informations:
 * <ul>
 * <li>Origin: The {@link Calendar} to which the event belongs</li>
 * <li>The event's name</li>
 * <li>A list of zero or more calendars in which the event occurs</li>
 * <li>A start and an end time between which the event takes place</li>
 * <li>A {@link RepeatingType} which is used by the {@link EventSeries}</li>
 * <li>An event can be public or private. A private event is only visible to its owner</li>
 * <li>A {@link Location} at which the event takes place</li>
 * <li>A description</li>
 * <li>A list of {@link Comment}s related to this event</li>
 * </ul>
 * <p>
 * The class Event includes methods for:
 * <ul>
 * <li>joining this event</li>
 * <li>adding comments to this event</li>
 * <li>testing if this event is available for a join</li>
 * <li>getting the visibility of this event</li>
 * <li>getting a list of participants of this event</li>
 * </ul>
 * 
 * @since Iteration-1
 * @see SingleEvent
 * @see EventSeries
 * @see User
 * @see Location
 * @see Comment
 * @see Calendar
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="EVENTTYPE", discriminatorType=DiscriminatorType.STRING)
@Table(name="Event")
public abstract class Event extends Model implements Comparable<Event>, Serializable{
	
	/**
	 * Calendar to which this events initially belongs.
	 */
	@Required
	@ManyToOne
	public Calendar origin;
	
	/**
	 * This event's name.
	 */
	@Required
	public String name;
	
	/**
	 * Calendars which joined this event.
	 */
	@Required
	@ManyToMany
	public List<Calendar> calendars;
	
	/**
	 * Date on which this event begins.
	 */
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;	

	/**
	 * Date on which this even ends.
	 */
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	@CheckWith(EndAfterBeginCheck.class)
	public DateTime endDate;
	
	/**
	 * Repeating type if any
	 */
	@Required
	public RepeatingType type;
	
	/**
	 * Visibility of this event.
	 */
	public Boolean isPrivate;
	
	/**
	 * Location at which this event takes place.
	 */
	@OneToOne
	public Location location;	
	
	/**
	 * A written remark to describe this event.
	 */
	@Lob
	public String description;
	
	/**
	 * Comments added to this event.
	 */
	@OneToMany(mappedBy="event", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	/**
	 * Event's constructor. The default behaviour is:
	 * <ul>
	 * <li>Event has zero or more comments</li>
	 * <li>Event belongs to a calendar</li>
	 * <li>Event has zero or more other calendars which joined it</li>
	 * <li>Event has a repeating type.
	 * </ul>
	 * 
	 * @param 	calendar		calendar to which this event belongs
	 * @param	name			name of the event
	 * @param	startDate		startDate of the event
	 * @param	endDate			endDate of the event
	 * @param	repeatingType	Type of the repeating rule if any
	 * @see 	Calendar
	 * @see 	Comment
	 */
	public Event(Calendar calendar, String name, DateTime startDate, DateTime endDate, RepeatingType repeating) {
		this.comments = new LinkedList<Comment>();
		this.origin = calendar;
		this.startDate = startDate;
		this.endDate = endDate;
		this.name = name;
		this.calendars = new LinkedList<Calendar>();
		this.calendars.add(calendar);
		this.isPrivate = false;
		this.type = repeating;
	}
	/**
	 * Static method to generate events depending on the type. The default behaviour is:
	 * <ul>
	 * <li>Event has zero or more comments</li>
	 * <li>Event belongs to a calendar</li>
	 * <li>Event has zero or more other calendars which joined it</li>
	 * <li>Event has a repeating type.
	 * </ul>
	 * 
	 * @param 	calendar		calendar to which this event belongs
	 * @param	name			name of the event
	 * @param	startDate		startDate of the event
	 * @param	endDate			endDate of the event
	 * @param	repeatingType	Type of the repeating rule if any
	 * @see 	Calendar
	 * @see 	Comment
	 */
	public static Event createEvent(	Calendar calendar, 
										String name, 
										DateTime startDate, 
										DateTime endDate, 
										RepeatingType repeating,
										DateTime periodEnd,
										int repeatingInterval) {
		if(repeatingInterval == 0) {
			repeatingInterval = 1;
		}
		if(repeating == RepeatingType.NONE) {
			return new SingleEvent(calendar, name, startDate, endDate);
		} else {
			EventSeries series = new EventSeries(calendar, name, startDate, endDate, repeating);
			series.setPeriodEnd(periodEnd);
			series.setRepeatingInterval(repeatingInterval);
			return series;
		}
	}
	
	/**
	 * Converts a single event to an event series and returns the converted event
	 * 
	 * @param	
	 * @return
	 */
	public static SingleEvent convertFromSeries(EventSeries series) {
		Calendar origin = (Calendar) ObjectCloner.deepCopy(series.origin);
		String name = (String) ObjectCloner.deepCopy(series.name);
		DateTime startDate = (DateTime) ObjectCloner.deepCopy(series.startDate);
		DateTime endDate = (DateTime) ObjectCloner.deepCopy(series.endDate);
		SingleEvent event = new SingleEvent(origin, name, startDate, endDate);
		event.description = (String) ObjectCloner.deepCopy(series.description);
		event.isPrivate = series.isPrivate ? true: false;
		event.location = (Location) ObjectCloner.deepCopy(series.location);
		for (Comment comment : series.comments){
			Comment commentCopy = new Comment(	(String) ObjectCloner.deepCopy(comment.author),
												event);
			commentCopy.content = (String) ObjectCloner.deepCopy(comment.content);
			event.comments.add(commentCopy);
		}
		/*for (Calendar calendar : series.calendars){
			Calendar calendarCopy = new Calendar(	(User) ObjectCloner.deepCopy(calendar.owner), 
													(String) ObjectCloner.deepCopy(calendar.name));
			calendarCopy.events.add(event);
			event.calendars.add(calendarCopy);
		}*/
		//JPA.em().persist(event);
		series.delete();
		return event;
	}
	public static Event convertFromSingleEvent(SingleEvent event, RepeatingType repeatingType) {
		Calendar origin = (Calendar) ObjectCloner.deepCopy(event.origin);
		String name = (String) ObjectCloner.deepCopy(event.name);
		DateTime startDate = (DateTime) ObjectCloner.deepCopy(event.startDate);
		DateTime endDate = (DateTime) ObjectCloner.deepCopy(event.endDate);
		EventSeries series = new EventSeries(origin, name, startDate, endDate, repeatingType);
		series.description = (String) ObjectCloner.deepCopy(event.description);
		series.isPrivate = event.isPrivate ? true: false;
		series.location = (Location) ObjectCloner.deepCopy(event.location);
		for (Comment comment : event.comments){
			Comment commentCopy = new Comment(	(String) ObjectCloner.deepCopy(comment.author),
												series);
			commentCopy.content = (String) ObjectCloner.deepCopy(comment.content);
			series.comments.add(commentCopy);
		}
		/*for (Calendar calendar : event.calendars){
			Calendar calendarCopy = new Calendar(	(User) ObjectCloner.deepCopy(calendar.owner),
													(String) ObjectCloner.deepCopy(calendar.name));
			calendarCopy.events.add(series);
			series.calendars.add(calendarCopy);
		}*/
		//JPA.em().persist(series);
		event.delete();
		return series;
	}
	/**
	 * Adds the given calendar to this event's calendar list. The calendar list
	 * represents all calendars which have joined this event.
	 * 
	 * @param 	calendar		calendar to add to the event's calendar list
	 * @see 	models.Event#calendars
	 * @since 	Iteration-2
	 */
	public void joinCalendar(Calendar calendar) {
		calendars.add(calendar);
		calendar.events.add(this);
		calendar.save();
		this.save();
	}

	/**
	 * Adds a comment to this event. The comment has an author and some content.
	 * 
	 * @param 	author		user who posts the comment
	 * @param 	content		the text contained in the comment
	 * @return 	this event
	 * @since 	Iteration-2
	 * @see 	Comment
	 */
	public void addComment(String author, String content) {
	    Comment newComment = new Comment(author, this);
	    newComment.content = content;
	    newComment.save();
	    this.comments.add(newComment);
	    this.save();
	}
	
	/**
	 *
	 * Returns a list of all calendars available for joining the event given for a certain user
	 * 
	 * @param	user 	The user which requests the join 
	 * @return	List<Calendar> List of possible calendars for a join	
	 * @see		models.SingleEvent#joinCalendar(Calendar calendar)
	 * @since	Iteration-1
	 */	
	public List<Calendar> availableJoins(User user) {
		if(!isPrivate) {
			Query query = JPA.em().createQuery("SELECT c FROM Calendar c " +
					"WHERE c.owner = ?1 "+
					"AND ?2 NOT MEMBER OF c.events");
			query.setParameter(1, user);
			query.setParameter(2, this);
			return query.getResultList();
		} else
			return new LinkedList<Calendar>();
	}
	
	/**
	 *
	 * Returns a list of users participating this event
	 * 
	 * @param	day 	The day to test
	 * @return	<code>true</code> if the start date of the event is at this day, otherwise <code>false</code>
	 * @see		models.Event#isThisDayandLocation(DateTime day, Location loc)
	 * @since	Beta-v1.2
	 */
	public abstract boolean isThisDay(DateTime day);
	
	/**
	 * Returns true if the given user is the owner of this event or the
	 * event is public.
	 * 
	 * @param 	visitor who wants to see this event
	 * @return <code>true</code> if visitor is owner of this event or if this event is public, otherwise <code>false</code>
	 * @since 	Iteration-1
	 * @see 	User
	 */
	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
	
	/**
	 * Returns true if the given date is the start date of this event and
	 * if the given location equals the event's location.
	 * 
	 * @param 	day		date to check whether it's equal to this event's start date or not
	 * @param 	loc		location to check whether it's equal to this event's location
	 * @return <code>true</code> if day equals this event's start date and if loc equals 
	 * this event's location, otherwise <code>false</code>
	 * @since 	Iteration-2
	 * @see 	Location
	 */
	public abstract boolean isThisDayandLocation(DateTime day, Location loc);

	/**
	 * Checks if the argument event's start date equals this event's start date.
	 * 
	 * @param e	event which's start date is compared to this event's start date
	 * @return the value 0 if the argument event's start date is equal to this
	 * event's start date; a value less than 0 if this event's start date is before 
	 * the argument event's start date; a value greater than 0 if this event's start
	 * date is after the argument event's start date.
	 * @since Iteration-1
	 */
	@Override
	public int compareTo(Event e) {
		return startDate.compareTo(e.startDate);
	}
	
	/**
	 *
	 * Returns a list of users participating this event
	 * 
	 * @return	a list of all users that contain this event in one of their calendars
	 * @since	Iteration-2
	 */
	public List<User> participants(){		
		List<User> parts = new LinkedList<User>();
		for(Calendar cal: calendars)
			if(!cal.owner.equals(origin.owner) && !parts.contains(cal.owner))
				parts.add(cal.owner);
		return parts;
	}	
	
	/**
	 * 
	 * Deletes the event
	 * 
	 * @return 	the object deleted
	 * @since	Iteration-1
	 */
	@Override
	public Event delete() {
		for(Comment p : Comment.find("byEvent", this).<Comment>fetch()) {
		    p.delete();
		    JPA.em().merge(this);
		}
		return super.delete();
	}
	
	/**
	 * Returns this event's name.
	 * 
	 * @return this event's name
	 * @since Iteration-1
	 * @return	a list of all users that contain this event in one of their calendars
	 * @since	Iteration-2
	 */
	@Override
	public String toString() {
		return name;
	}

	private static class ObjectCloner {

        /**
         * Perform a deep copy.
         * 
         * @param oldObj
         *            The old object.
         * @return The new object.
         */
        public static Object deepCopy(Object oldObj) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                
                // serialize and pass the object
                oos.writeObject(oldObj);
                oos.flush();
                
                final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
                ois = new ObjectInputStream(bin);
                
                // return the new object
                return ois.readObject();
                
            } catch (final Exception e) {} 
            finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (final Exception e) {}
            }
			return ois;
        }

        /**
         * Private constructor.
         */
        private ObjectCloner() {
        }
    }
	
	private static class EndAfterBeginCheck extends Check {
		public boolean isSatisfied(Object event_, Object end_) {
			Event event = (Event) event_;
			DateTime end = (DateTime) end_;
			setMessage("validation.EndAfterBeginCheck");
			return event.startDate.isBefore(end);
		}
	}
}