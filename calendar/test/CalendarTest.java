import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	
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
	public void addAndDeleteEvents() {
		// Create a new user and save it
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		
		// Create a new Calendar and save it
		Calendar jacksCalendar = new Calendar(jack, "Jacks Agenda").save();
		
		// Create new events
		Event e1 = new Event(jacksCalendar);
		e1.name = "Meet Lynn Bracken";
		e1.startDate = format.parseDateTime("11.09.1953 13:00");
		e1.endDate = format.parseDateTime("11.09.1953 15:00");
		e1.isPrivate = false;
		e1.description = "Who is this mysterious women?";
		
		assertTrue(e1.validateAndSave());
		
		Event e2 = new Event(jacksCalendar);
		e2.name = "Hit Exley";
		e2.startDate = format.parseDateTime("11.09.1953 17:00");
		e2.endDate = format.parseDateTime("11.09.1953 18:00");
		e2.isPrivate = true;
		e2.description = "That's gonna be fun!";
		
		assertTrue(e2.validateAndSave());
		
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
	    assertEquals(format.parseDateTime("11.09.1953 13:00"), firstEvent.startDate);
	    assertEquals(format.parseDateTime("11.09.1953 15:00"), firstEvent.endDate);
	    assertFalse(firstEvent.isPrivate);
	    assertEquals("Who is this mysterious women?", firstEvent.description);
	 
	    Event secondEvent = jacksCalendar.events.get(1);
	    assertNotNull(secondEvent);
	    assertEquals(format.parseDateTime("11.09.1953 17:00"), secondEvent.startDate);
	    assertEquals(format.parseDateTime("11.09.1953 18:00"), secondEvent.endDate);
	    assertTrue(secondEvent.isPrivate);
	    assertEquals("That's gonna be fun!", secondEvent.description);
	    
	    // Delete Calendar
	    jacksCalendar.delete();
	    assertEquals(1, User.count());
	    assertEquals(0, Calendar.count());
	    assertEquals(0, Event.count());
	}
	
	@Test
	public void validationError() {
		// Create a new user and save it
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		
		// Create a new Calendar and save it
		Calendar jacksCalendar = new Calendar(jack, "Jacks Agenda").save();
		
		// Create invalid event
		Event e = new Event(jacksCalendar);
		e.name = "";
		e.startDate = format.parseDateTime("11.09.1953 13:00");
		e.endDate = format.parseDateTime("11.09.1953 15:00");
		
		assertFalse(e.validateAndSave());
		assertEquals(1, User.count());
		assertEquals(1, Calendar.count());
		assertEquals(0, Event.count());
	}
	
	@Test
	public void eventsPerMonth() {
		Fixtures.loadModels("data.yml");
		
		// Get calendar
		Calendar edsCalendar = Calendar.find("owner.email", "ed.exley@lapd.com").first();
		
		DateTime dt = new DateTime();
		// Get events for September 1953
		dt = dt.withYear(1953);
		dt = dt.withMonthOfYear(9);
		List<Event> events = edsCalendar.eventsByMonth(dt, edsCalendar.owner);
		assertTrue(events.isEmpty());
		
		// Get events for October 2011 as Ed
		dt = dt.withYear(2011);
		dt = dt.withMonthOfYear(10);
		events = edsCalendar.eventsByMonth(dt, edsCalendar.owner);
		assertEquals(2, events.size());
		
		// Get events for October 2011 as not Ed
		dt = dt.withYear(2011);
		dt = dt.withMonthOfYear(10);
		events = edsCalendar.eventsByMonth(dt, null);
		assertEquals(1, events.size());
	}
}