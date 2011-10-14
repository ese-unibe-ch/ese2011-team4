import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;

public class EventTest extends UnitTest {
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
	public void create() {
		// Get a calendar
		Calendar calendar = Calendar.all().first();
		
		// Create an event
		Event event = new Event(calendar);
		event.name = "Test 1";
		event.startDate = format.parseDateTime("20.10.2011 10:00");
		event.endDate = format.parseDateTime("20.10.2011 12:00");
		event.isPrivate = false;
		
		assertTrue(event.validateAndSave());
		assertEquals(8, Event.count());
		
		// Retrieve data
		Event e = Event.findById(event.id);
		assertEquals(event, e);
		assertEquals("Test 1", e.name);
		assertEquals(format.parseDateTime("20.10.2011 10:00"), e.startDate);
		assertEquals(format.parseDateTime("20.10.2011 12:00"), e.endDate);
		assertEquals(false, e.isPrivate);
	}
	
	@Test
	public void edit() {		
		// Get an event and change some information
		Event event = Event.all().first();
		event.name = "Test 2";
		DateTime start = event.startDate;
		DateTime end = event.endDate;
		boolean isPrivate = event.isPrivate;

		assertTrue(event.validateAndSave());
		assertEquals(7, Event.count());
		
		// Retrieve data
		Event e = Event.findById(event.id);
		assertEquals(event, e);
		assertEquals("Test 2", e.name);
		assertEquals(start, e.startDate);
		assertEquals(end, e.endDate);
		assertEquals(isPrivate, e.isPrivate);
	}
	
	@Test
	public void remove() {
		// Get an event
		Event event = Event.all().first();
		Long id = event.id;
		
		// Delete it
		event.delete();
		
		// Try to find it
		assertNull(Event.findById(id));
		assertEquals(6, Event.count());
	}
	
	@Test
	public void validDateCheck() {
		// Get a calendar
		Calendar calendar = Calendar.all().first();
		
		// Create an event
		Event event = new Event(calendar);
		event.name = "Test 3";
		event.startDate = format.parseDateTime("20.10.2011 09:00");
		event.endDate = format.parseDateTime("20.10.2011 08:00");
		event.isPrivate = false;
		
		// Try to save it
		assertFalse(event.validateAndSave());
		assertEquals(7, Event.count());
	}
	
	@Test
	public void isThisDay() {
		// Get an event
		Event event = Event.find("byName", "Cinema").first();
		
		assertTrue(event.isThisDay(new DateTime().withDayOfMonth(21).withMonthOfYear(10).withYear(2011)));
	}
	
	@Test
	public void visibility() {
		// Get user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// Get visible event
		Event visibleEvent = Event.find("byName", "Work").first();
		assertTrue(visibleEvent.isVisible(jack));
		
		// Get invisible event
		Event invisibleEvent = Event.find("byName", "Meeting with the mayor").first();
		assertFalse(invisibleEvent.isVisible(jack));
		
		// Get own private event
		Event ownEvent = Event.find("byName", "Collections").first();
		assertTrue(ownEvent.isVisible(jack));
	}
}
