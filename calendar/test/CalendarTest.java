import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.*;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void createCalendar() {
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		new Calendar(jack, "Jacks Agenda").save();
		
		Calendar cal = Calendar.find("byOwner", jack).first();
		
		assertNotNull(cal);
		assertEquals("Jacks Agenda", cal.name);
	}
	
	@Test
	public void addAndDeleteEvents() throws InvalidEventException, ParseException {
		// Create a new user and save it
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		
		// Create a new Calendar and save it
		Calendar jacksCalendar = new Calendar(jack, "Jacks Agenda").save();
		
		// Create new events
		jacksCalendar.addEvent("Meet Lynn Bracken", dateFormat.parse("11.09.1953 13:00"), dateFormat.parse("11.09.1953 15:00"), false);
		jacksCalendar.addEvent("Hit Exley", dateFormat.parse("11.09.1953 17:00"), dateFormat.parse("11.09.1953 18:00"), true);
		
		// Count things
	    assertEquals(1, User.count());
	    assertEquals(1, Calendar.count());
	    assertEquals(2, Event.count());
	    
	    // Retrieve calendar
	    jacksCalendar = Calendar.find("byOwner", jack).first();
	    assertNotNull(jacksCalendar);
		
	    // Navigate to events
		assertEquals(2, jacksCalendar.events.size());
		
		Event firstEvent = jacksCalendar.events.get(0);
	    assertNotNull(firstEvent);
	    assertEquals(dateFormat.parse("11.09.1953 13:00"), firstEvent.startDate);
	    assertEquals(dateFormat.parse("11.09.1953 15:00"), firstEvent.endDate);
	    assertFalse(firstEvent.isPrivate);
	 
	    Event secondEvent = jacksCalendar.events.get(1);
	    assertNotNull(secondEvent);
	    assertEquals(dateFormat.parse("11.09.1953 17:00"), secondEvent.startDate);
	    assertEquals(dateFormat.parse("11.09.1953 18:00"), secondEvent.endDate);
	    assertTrue(secondEvent.isPrivate);
	    
	    // Delete Calendar
	    jacksCalendar.delete();
	    assertEquals(1, User.count());
	    assertEquals(0, Calendar.count());
	    assertEquals(0, Event.count());
	}
	
	@Test
	public void addEventWithDescription() throws InvalidEventException, ParseException {
		// Create a new user and save it
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		
		// Create a new Calendar and save it
		Calendar jacksCalendar = new Calendar(jack, "Jacks Agenda").save();
		
		// Create new events
		jacksCalendar.addEvent("Meet Lynn Bracken", dateFormat.parse("11.09.1953 13:00"), 
				dateFormat.parse("11.09.1953 15:00"), false, "Who is this mysterious women?");
		jacksCalendar.addEvent("Hit Exley", dateFormat.parse("11.09.1953 17:00"), 
				dateFormat.parse("11.09.1953 18:00"), true, "That's gonna be fun!");
		
		Event firstEvent = jacksCalendar.events.get(0);
		assertEquals("Who is this mysterious women?", firstEvent.description);
		
		Event secondEvent = jacksCalendar.events.get(0);
		assertEquals("That's gonna be fun!", secondEvent.description);
	}
}