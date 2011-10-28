package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Comment extends Model {
	@Required
    public String author;
    
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime postedAt;
    
    @Lob
    public String content;
    
    @Required
    @ManyToOne
    public Event event;
    
    public Comment(String author, Event event) {
    	this.author = author;
        this.event = event;
        this.postedAt = new DateTime();
    }
}
