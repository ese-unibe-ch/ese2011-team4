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
	
	@Test
	public void availableJoins() {
		// Get some users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get some events
		Event e3 = Event.find("byName", "Work").first();
		Event e0 = Event.find("byName", "Collections").first();
		
		assertEquals(2, e3.availableJoins(jack).size());
		assertEquals(0, e3.availableJoins(bud).size());
		
		// Private event can't join
		assertEquals(0, e0.availableJoins(jack).size());
	}
	
	@Test
	public void joinCalendar() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		assertEquals(1, budCalendar.events.size());
		
		// Get a event
		Event event = Event.find("byName", "Cinema").first();
		assertFalse(event.calendars.contains(budCalendar));
		
		// Join the calendar
		event.joinCalendar(budCalendar);
		assertEquals(2, budCalendar.events.size());
		assertTrue(event.calendars.contains(budCalendar));
				
		// Make sure there weren't created any objects
		assertEquals(7, Event.count());
		assertEquals(4, Calendar.count());
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
}
