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


public class LocationTest extends UnitTest {
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
		assertEquals(1, Location.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, SingleEvent.count());
	}
	
	@Test
	public void eventWithLocation() {
		// Get an event
		SingleEvent e1 = SingleEvent.find("byName", "Meet Lynn Bracken").first();
		
		// Test if it has an event
		assertEquals("Bernstrasse", e1.location.street);
	}
	
	@Test
	public void addLocation() {
		// Get an Event
		SingleEvent e0 = SingleEvent.find("byName", "Collections").first();
		
		// Create a new Location
		Location location = new Location();
		location.street = "Worbstrasse";
		location.num = "14";
		location.city = "Gümligen";
		location.pincode = "3000";
		location.country = "Switzerland";
		
		// Save it
		assertTrue(location.validateAndSave());
		
		// Add it to the event
		assertNull(e0.location);
		e0.location = location;
		
		// Save it
		e0.validateAndSave();
		for(play.data.validation.Error e : Validation.errors())
			Logger.info(e.message());
		
		assertTrue(e0.validateAndSave());
		
		// Retrieve data
		location = Location.findById(location.id);
		assertEquals("Worbstrasse", location.street);
		assertEquals("14", location.num);
		assertEquals("Gümligen", location.city);
		assertEquals("3000", location.pincode);
		assertEquals("Switzerland", location.country);
	}
	
	@Test
	public void getAllEvents() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get event
		SingleEvent event = Event.find("byName", "Meet Lynn Bracken").first();
		
		assertEquals(1, loc.getAllEvents().size());
		assertTrue(loc.getAllEvents().contains(event));
	}
	
	@Test
	public void numberOfAllEvents() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		assertEquals(1, loc.numberOfAllEvents());
	}
	
	@Test
	public void getVisibleEvents() {
		// Get User
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get event
		SingleEvent event = Event.find("byName", "Meet Lynn Bracken").first();
		
		// Create event
		SingleEvent event2 = new SingleEvent(calendar, "Test", new DateTime(), new DateTime().plusHours(2));
		event2.isPrivate = true;
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		
		assertEquals(2, loc.getVisibleEvents(jack).size());
		assertTrue(loc.getVisibleEvents(jack).contains(event));
		assertTrue(loc.getVisibleEvents(jack).contains(event2));
		assertEquals(1, loc.getVisibleEvents(bud).size());
		assertTrue(loc.getVisibleEvents(bud).contains(event));
		assertFalse(loc.getVisibleEvents(bud).contains(event2));
	}
	
	@Test
	public void numberOfVisibleEvents() {
		// Get User
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Create event
		SingleEvent event2 = new SingleEvent(calendar, "Test", new DateTime(), new DateTime().plusHours(2));
		event2.isPrivate = true;
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		
		assertEquals(2, loc.numberOfVisibleEvents(jack));
		assertEquals(1, loc.numberOfVisibleEvents(bud));
	}
	
	@Test
	public void getVisibleEventsByDay() {
		// Get User
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get event
		SingleEvent event = Event.find("byName", "Meet Lynn Bracken").first();
		
		DateTime day = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011);
		
		// Create event
		SingleEvent event2 = new SingleEvent(calendar, "Test", day, day.plusHours(2));
		event2.isPrivate = true;
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		SingleEvent event3 = new SingleEvent(calendar, "Test2", day.plusDays(1), day.plusDays(1).plusHours(2));
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		
		assertEquals(2, loc.getVisibleEventsByDay(day, jack).size());
		assertTrue(loc.getVisibleEventsByDay(day, jack).contains(event));
		assertTrue(loc.getVisibleEventsByDay(day, jack).contains(event2));
		assertFalse(loc.getVisibleEventsByDay(day, jack).contains(event3));
		assertEquals(1, loc.getVisibleEventsByDay(day, bud).size());
		assertTrue(loc.getVisibleEventsByDay(day, bud).contains(event));
		assertFalse(loc.getVisibleEventsByDay(day, bud).contains(event2));
		assertFalse(loc.getVisibleEventsByDay(day, bud).contains(event3));
	}
	
	@Test
	public void getVisibleEventsByDayAndTime() {
		// Get User
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get event
		SingleEvent event = Event.find("byName", "Meet Lynn Bracken").first();
		
		DateTime start = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(13).withMinuteOfHour(10);
		DateTime end = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(14).withMinuteOfHour(20);
		
		// Create event
		SingleEvent event2 = new SingleEvent(calendar, "Test", start, end);
		event2.isPrivate = true;
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		SingleEvent event3 = new SingleEvent(calendar, "Test2", start.plusHours(3), end.plusHours(3));
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		
		assertEquals(2, loc.getVisibleEventsByDayAndTime(start, end, jack).size());
		assertTrue(loc.getVisibleEventsByDayAndTime(start, end, jack).contains(event));
		assertTrue(loc.getVisibleEventsByDayAndTime(start, end, jack).contains(event2));
		assertFalse(loc.getVisibleEventsByDayAndTime(start, end, jack).contains(event3));
		assertEquals(1, loc.getVisibleEventsByDayAndTime(start, end, bud).size());
		assertTrue(loc.getVisibleEventsByDayAndTime(start, end, bud).contains(event));
		assertFalse(loc.getVisibleEventsByDayAndTime(start, end, bud).contains(event2));
		assertFalse(loc.getVisibleEventsByDayAndTime(start, end, bud).contains(event3));
	}
	
	
	@Test
	public void numberOfAllEventsByDay() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		DateTime day = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011);
		
		// Create event
		SingleEvent event = new SingleEvent(calendar, "Test", day.plusDays(1), day.plusDays(1).plusHours(2));
		event.location = loc;
		assertTrue(event.validateAndSave());
		
		assertEquals(1, loc.numberOfAllEventsByDay(day));
	}
	
	@Test
	public void numberOfAllEventsByDayAndTime() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		DateTime start1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(13).withMinuteOfHour(10);
		DateTime end1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(14).withMinuteOfHour(20);
		
		DateTime start2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(18).withMinuteOfHour(30);
		DateTime end2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(19).withMinuteOfHour(30);
		
		// Create event
		EventSeries event = new EventSeries(	calendar, 
				"Repeating1", 
				start2, 
				end2,
				RepeatingType.WEEKLY);
		event.location = loc;
		
		assertTrue(event.validateAndSave());
		
		assertEquals(1, loc.numberOfAllEventsByDayAndTime(start1, end1));
		assertEquals(2, loc.numberOfAllEventsByDayAndTime(start1, end2));
		assertEquals(1, loc.numberOfAllEventsByDayAndTime(start2, end2));
	}
	
	@Test
	public void getVisibleUpcomingEvents() {
		// Get User
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		int numJack = loc.getVisibleUpcomingEvents(jack).size();
		int numBud = loc.getVisibleUpcomingEvents(bud).size();
		DateTime day = new DateTime();
		
		// Create event
		SingleEvent event = new SingleEvent(calendar, "Test", day, day.plusHours(2));
		event.isPrivate = true;
		event.location = loc;
		assertTrue(event.validateAndSave());
		SingleEvent event2 = new SingleEvent(calendar, "Test2", day.plusDays(1), day.plusDays(1).plusHours(2));
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		SingleEvent event3 = new SingleEvent(calendar, "Test3", day.minusHours(3), day.minusHours(2));
		event3.location = loc;
		assertTrue(event3.validateAndSave());
		
		assertEquals(numJack+2, loc.getVisibleUpcomingEvents(jack).size());
		assertTrue(loc.getVisibleUpcomingEvents(jack).contains(event));
		assertTrue(loc.getVisibleUpcomingEvents(jack).contains(event2));
		assertFalse(loc.getVisibleUpcomingEvents(jack).contains(event3));
		
		assertEquals(numBud+1, loc.getVisibleUpcomingEvents(bud).size());
		assertFalse(loc.getVisibleUpcomingEvents(bud).contains(event));
		assertTrue(loc.getVisibleUpcomingEvents(bud).contains(event2));
		assertFalse(loc.getVisibleUpcomingEvents(bud).contains(event3));
	}
	
	@Test
	public void numberOfAllUpcomingEvents() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		DateTime day = new DateTime();
		
		// Create event
		SingleEvent event = new SingleEvent(calendar, "Test", day, day.plusHours(2));
		event.location = loc;
		assertTrue(event.validateAndSave());
		SingleEvent event2 = new SingleEvent(calendar, "Test2", day.plusDays(1), day.plusDays(1).plusHours(2));
		event2.location = loc;
		assertTrue(event2.validateAndSave());
		SingleEvent event3 = new SingleEvent(calendar, "Test3", day.minusHours(3), day.minusHours(2));
		event3.location = loc;
		assertTrue(event3.validateAndSave());
		
		assertEquals(2, loc.numberOfAllUpcomingEvents());
	}
}