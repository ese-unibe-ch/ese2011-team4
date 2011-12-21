package models;

import org.joda.time.DateTime;

/**
 * The BirthdayEvent class implements the abstract class {@link Event} and represents
 * birthdays of certain users. A specific user can see the visible birthdays of all his 
 * favorite users.
 * <p>
 * A birthday event includes following informations:
 * <ul>
 * <li>A birthday event has a name</li>
 * <li>A birthday event belongs to a calendar</li>
 * <li>A birthday event belongs to a user</li>
 * <li>A birthday event has a start and an end date</li>
 * <li>A birthday event has a description</li>
 * <li>A birthday event has a {@link RepeatingType}.</li>
 * </ul>
 *
 * @since Iteration-7
 * @see Calendar
 * @see Event
 * @see RepeatingType
 * @see User
 */
public class BirthdayEvent extends SingleEvent {

	/**
	 * This event's id
	 */
	public Long id;
	
	/**
	 * The event series from which this repeating event is created.
	 */
	public EventSeries series;
	
	/**
	 * User to whom this birthday belongs.
	 */
	public User user;
	
	/**
	 * BirthdayEvent's constructor. Default behavior is:
	 * <ul>
	 * <li>	<code>BirthdayEvent</code> belongs to a calendar which is defined by the argument 
	 * 		<code>EventSeries series</code></li>
	 * <li>	<code>BirthdayEvent</code> has a name which is defined by the argument <code>
	 * 		EventSeries series</code></li>
	 * <li> <code>BirthdayEvent</code> has a <code>User</code> to whom it belongs</li>
	 * <li>	<code>BirthdayEvent</code> has a start and an end date which are defined by the argument
	 * 		<code>EventSeries series</code></li>
	 * <li>	<code>BirthdayEvent</code> has a description which is defined by <code>EventSeries series</code></li>
	 * <li>	<code>BirthdayEvent</code> has a repeating type which is defined by <code>EventSeries series</code>.</li>
	 * </ul>
	 * 
	 * @param series : 	The <code>EventSeries</code> from which this <code>BirthdayEvent</code> is
	 * 					generated
	 * @param user :	The <code>User</code> to whom this <code>BirthdayEvent</code> belongs
	 * 
	 * @see Calendar
	 * @see EventSeries
	 * @see RepeatingType
	 * @see User
	 */
	public BirthdayEvent(EventSeries series, User user) {
		super(series.origin, series.name, series.startDate, series.endDate);
		this.description = series.description;
		this.type = series.type;
		this.series = series;
		id = series.id;
		this.user = user;
	}
}