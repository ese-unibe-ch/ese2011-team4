package models;

import org.joda.time.DateTime;

import play.db.jpa.Model;

public class RepeatingEvent extends Model {
	public RepeatingType type;
	public DateTime periodStart;
	public DateTime periodEnd;
}
