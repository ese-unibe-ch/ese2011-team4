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
	
	public SingleEvent editSingleEvent(String name, DateTime startDate, DateTime endDate) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		SingleEvent event = new SingleEvent(this);
		if(event.validateAndSave())
			return event;
		else
			return null;
	}
	
	public boolean editSeries(String name, DateTime startDate, DateTime endDate) {
		series.name = name;
		series.startDate = startDate;
		series.endDate = endDate;
		return series.validateAndSave();
	}
}