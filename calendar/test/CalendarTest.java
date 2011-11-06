
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	
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
		assertEquals(4, Calendar.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, Calendar.count());
	}
	
	@Test
	public void create() {
		// Get user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// New calendar
		Calendar calendar = new Calendar(jack, "Test Calendar 1");
		
		// Validate and save
		calendar.validateAndSave();
		
		assertTrue(calendar.validateAndSave());
		assertEquals(5, Calendar.count());
		
		// Retrieve data
		Calendar cal = Calendar.findById(calendar.id);
		assertEquals(jack, cal.owner);
		assertEquals("Test Calendar 1", cal.name);
		assertNotNull(cal.events);
		assertTrue(cal.events.isEmpty());
	}
	
	@Test
	public void delete() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		Long id = calendar.id;
		
		// Count events
		assertEquals(8, Event.count());
		
		// Delete it
		calendar.delete();
		
		// Try to find it
		assertNull(Calendar.findById(id));
		assertEquals(3, Calendar.count());
		
		// Count events
		assertEquals(5, Event.count());
		
		// Count comments
		assertEquals(0, Comment.count());
	}
	
	@Test
	public void eventsPerDay() {
		// Get some calendars
		Calendar jacks = Calendar.find("byName", "Jacks Agenda").first();
		Calendar eds = Calendar.find("byName", "Ed's Future Calandar").first();
		
		// Get two users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		assertEquals(2, jacks.events(jack, new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011)).size());
		assertEquals(1, jacks.events(bud, new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011)).size());
		
		// Event over 3 days
		assertEquals(1, eds.events(jack, new DateTime().withDayOfMonth(4).withMonthOfYear(11).withYear(2011)).size());
		assertEquals(1, eds.events(jack, new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011)).size());
		assertEquals(1, eds.events(jack, new DateTime().withDayOfMonth(6).withMonthOfYear(11).withYear(2011)).size());
		assertEquals(1, eds.events(jack, new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011)).size());
		
		assertEquals(0, jacks.events(jack, new DateTime().withDayOfMonth(6).withMonthOfYear(11).withYear(2011)).size());
		assertEquals(0, jacks.events(jack, new DateTime().withDayOfMonth(4).withMonthOfYear(11).withYear(2011)).size());
	}
	
	@Ignore("relies on system date")
	@Test
	public void visibleEvents() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
				
		// Get two users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Test method
		assertEquals(4, calendar.visibleEvents(jack).size());
		assertEquals(2, calendar.visibleEvents(bud).size());
	}
}