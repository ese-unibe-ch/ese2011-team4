package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;


import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The Comment class represents a written remark related to an {@link Event}.
 * <p>
 * Comments have an author, a date at which the comment is posted, some content
 * and an event to which the comment is related to.
 * 
 * @see 	User 
 * @see 	Event
 * @since 	Iteration-2
*/
@Entity
public class Comment extends Model {
	
	/**
	 * Author of the comment.
	 */
	@Required
    public String author;
	
	/**
	 * Date at which the comment was posted.
	 */
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime postedAt;
    
	/**
	 * Content of the comment.
	 */
    @Lob
    public String content;
    
    /**
     * Event which belongs to the comment.
     */
    @Required
    @ManyToOne
    public Event event;
    
    /**
     * Comment's constructor. The default behaviour is:
     * <ul>
     * <li>Comment has an author</li>
     * <li>Comment belongs to an event</li>
     * <li>Comment has a date at which it was posted</li>
     * </ul>
     * 
     * @param 	author		user who posts the comment
     * @param 	event		event to which the comment belongs
     * @see 	User
     * @see 	Event
     */
    public Comment(String author, Event event) {
    	this.author = author;
        this.event = event;
        this.postedAt = new DateTime();
    }
}
