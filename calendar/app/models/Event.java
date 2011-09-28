package models;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Comparable<Event>{
	private String name;
	private Date startDate;
	private Date endDate;
	private Boolean privacy;
	
	public Event(String name, Date startDate, Date endDate, boolean privacy) throws EndDateBeforeStartDateException {
		if(startDate.after(endDate))
			throw new EndDateBeforeStartDateException();
		
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.privacy = privacy;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setPrivacy(Boolean isPrivate) {
		this.privacy = isPrivate;
	}
	
	public Boolean getPrivacy() {
		return privacy;
	}
	
	@Override
	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		StringBuffer sb = new StringBuffer();
		sb.append(name+"\t");
		sb.append(dateFormat.format(startDate)+"\t");
		sb.append(dateFormat.format(endDate)+"\t");
		sb.append(privacy);
		
		return sb.toString();
	}

	@Override
	public int compareTo(Event e) {
		return startDate.compareTo(e.getStartDate());
	}
}