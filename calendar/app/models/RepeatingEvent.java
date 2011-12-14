package models;

import org.joda.time.DateTime;

/**
 * The RepeatingEvent class implements the abstract class {@link Event} and represents
 * a repeating event within a certain time period.
 * <p>
 * A repeating event includes following informations:
 * <ul>
 * <li>A repeating event has a name</li>
 * <li>A repeating event belongs to a calendar</li>
 * <li>A repeating event has a start and an end date</li>
 * <li>A repeating event can take place at a certain location</li>
 * <li>A repeating event has a description</li>
 * <li>A repeating event contains zero or more comments</li>
 * <li>A repeating event has a {@link RepeatingType}.</li>
 * </ul>
 *
 * @since Iteration-4
 * @see Calendar
 * @see Comment
 * @see Event
 * @see Location
 * @see RepeatingType
 */
public class RepeatingEvent extends SingleEvent {
	
	/**
	 * This event's id.
	 */
	public Long id;
	
	/**
	 * The event series from which this repeating event is created.
	 */
	public EventSeries series;

	/**
	 * RepeatingEvent's constructor. Default behavior is:
	 * <ul>
	 * <li>	RepeatingEvent belongs to a calendar which is defined by the argument 
	 * 		<code>EventSeries series</code></li>
	 * <li>	RepeatingEvent has a name which is defined by the argument <code>
	 * 		EventSeries series</code></li>
	 * <li>	RepeatingEvent has a start and an end date which are defined by the argument
	 * 		<code>EventSeries series</code></li>
	 * <li>	RepeatingEvent can have a location which is defined by <code>EventSeries series
	 * 		</code></li>
	 * <li>	RepeatingEvent has a description which is defined by <code>EventSeries series</code></li>
	 * <li>	RepeatingEvent contains zero or more comments which are defined by <code>
	 * 		EventSeries series</code></li>
	 * <li>	RepeatingEvent has a repeating type which is defined by <code>EventSeries series</code>.</li>
	 * </ul>
	 * 
	 * @param series : 	The <code>EventSeries</code> from which this <code>RepeatingEvent</code> is
	 * 					generated
	 * @see Calendar
	 * @see Comments
	 * @see EventSeries
	 * @see Location
	 * @see RepeatingType
	 */
	public RepeatingEvent(EventSeries series) {
		super(series.origin, series.name, series.startDate, series.endDate);
		this.location = series.location;
		this.description = series.description;
		this.comments = series.comments;
		this.type = series.type;
		this.series = series;
		this.isPrivate = series.isPrivate;
		this.invitations = series.invitations;
		id = series.id;
	}
}