package models;

import org.joda.time.DateTime;

public class RepeatingEvent extends SingleEvent {
	public Long id;
	public EventSeries series;

	public RepeatingEvent(EventSeries series) {
		super(series.origin, series.name, series.startDate, series.endDate);
		this.location = series.location;
		this.description = series.description;
		this.comments = series.comments;
		this.type = series.type;
		this.series = series;
		id = series.id;
	}
}