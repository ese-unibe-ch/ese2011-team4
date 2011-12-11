import java.util.List;

import javax.persistence.Query;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class CommentTest extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
        // YAML can't load enum
        for(Event e : SingleEvent.all().<SingleEvent>fetch()) {
        	e.type = RepeatingType.NONE;
        	e.save();
        }
        
        EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		event.save();
		assertEquals(3, Comment.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, Comment.count());
	}

	@Test
	public void postComments() {
		// Get a event
		SingleEvent event = SingleEvent.find("byName", "Collections").first();
		
		// Get some users
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		User ed = User.find("byEmail", "ed.exley@lapd.com").first();
		
	    // Post a first comment
		event.addComment(bud, "Nice post");
	    event.addComment(ed, "I knew that !");
	 
	    // Retrieve all comments for this event
	    List<Comment> eventComments = Comment.find("byEvent", event).fetch();
	 
	    // Tests
	    assertEquals(2, eventComments.size());
	    assertEquals(5, Comment.count());
	 
	    Comment firstComment = eventComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice post", firstComment.content);
	    assertNotNull(firstComment.postedAt);
	 
	    Comment secondComment = eventComments.get(1);
	    assertNotNull(secondComment);
	    assertEquals("Tom", secondComment.author);
	    assertEquals("I knew that !", secondComment.content);
	    assertNotNull(secondComment.postedAt);
	}
	
	@Test
	public void deleteComments() {
		// Get a User
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get a comment
		Comment comment = Comment.find("byAuthor", bud).first();
		
		comment.delete();
		
		assertEquals(2, Comment.count());
	}
}