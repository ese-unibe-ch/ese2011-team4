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
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
		assertEquals(7, Event.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, Event.count());
	}
	

	@Test
	public void postComments() {
	    
	    // Get a calendar
		Calendar calendar = Calendar.all().first();
	 
	    // Create a new event
	    Event event = new Event(calendar).save();
	    event.name = "Test 3";
		event.startDate = format.parseDateTime("20.10.2011 08:00");
		event.endDate = format.parseDateTime("20.10.2011 09:00");
		event.isPrivate = false;
		
		// Try to save it
		assertTrue(event.validateAndSave());
	 
	    // Post a first comment
		event.addComment("Jeff", "Nice post");
	    event.addComment("Tom", "I knew that !");
	 
	    // Retrieve all comments
	    List<Comment> bobEventComments = Comment.find("byEvent", event).fetch();
	 
	    // Tests
	    assertEquals(2, bobEventComments.size());
	 
	    Comment firstComment = bobEventComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice post", firstComment.content);
	    assertNotNull(firstComment.postedAt);
	 
	    Comment secondComment = bobEventComments.get(1);
	    assertNotNull(secondComment);
	    assertEquals("Tom", secondComment.author);
	    assertEquals("I knew that !", secondComment.content);
	    assertNotNull(secondComment.postedAt);
	}
	
	@Test
	public void useTheCommentsRelation() {
		
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		assertEquals(1, budCalendar.events.size());
		
		// Get a event
		Event bobEvent = Event.find("byName", "Cinema").first();
		assertFalse(bobEvent.calendars.contains(budCalendar));
	 
	    // Post a first comment
	    bobEvent.addComment("Jeff", "Nice post");
	    bobEvent.addComment("Tom", "I knew that !");
	 
	    // Count things
	    assertEquals(3, User.count());
	    assertEquals(7, Event.count());
	    assertEquals(5, Comment.count());
	 
	    // Retrieve Bob's event
	    bobEvent = Event.find("byName", "Cinema").first();
	    assertNotNull(bobEvent);
	 
	    // Navigate to comments
	    assertEquals(2, bobEvent.comments.size());
	    assertEquals("Jeff", bobEvent.comments.get(0).author);
	    
	    // Delete the post
	    bobEvent.delete();
	    
	    // Check that all comments have been deleted
	    assertEquals(3, User.count());
	    assertEquals(6, Event.count());
	    assertEquals(3, Comment.count());
	}
}