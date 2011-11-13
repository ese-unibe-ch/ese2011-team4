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

public class SingleEventTest extends UnitTest {
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	
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
		assertEquals(7, SingleEvent.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, SingleEvent.count());
	}
	
	@Test
	public void create() {
		// Get a calendar
		Calendar calendar = Calendar.all().first();
		
		// Create an event
		SingleEvent event = new SingleEvent(	calendar, 
												"Test 1", 
												format.parseDateTime("20.10.2011 10:00"), 
												format.parseDateTime("20.10.2011 12:00"));
		
		assertTrue(event.validateAndSave());
		assertEquals(8, SingleEvent.count());
		
		// Retrieve data
		SingleEvent e = SingleEvent.findById(event.id);
		assertEquals(event, e);
		assertEquals("Test 1", e.name);
		assertEquals(format.parseDateTime("20.10.2011 10:00"), e.startDate);
		assertEquals(format.parseDateTime("20.10.2011 12:00"), e.endDate);
		assertEquals(false, e.isPrivate);
	}
	
	@Test
	public void edit() {		
		// Get an event and change some information
		SingleEvent event = SingleEvent.all().first();
		event.name = "Test 2";
		DateTime start = event.startDate;
		DateTime end = event.endDate;
		boolean isPrivate = event.isPrivate;

		assertTrue(event.validateAndSave());
		assertEquals(7, SingleEvent.count());
		
		// Retrieve data
		SingleEvent e = SingleEvent.findById(event.id);
		assertEquals(event, e);
		assertEquals("Test 2", e.name);
		assertEquals(start, e.startDate);
		assertEquals(end, e.endDate);
		assertEquals(isPrivate, e.isPrivate);
	}
	
	@Test
	public void remove() {
		// Get an event
		SingleEvent event = SingleEvent.find("byName", "Meet Lynn Bracken").first();
		Long id = event.id;
		
		// Delete it
		event.delete();
		
		// Try to find it
		assertNull(SingleEvent.findById(id));
		assertEquals(6, SingleEvent.count());
		
		// Count comments
		assertEquals(1, Comment.count());
	}
	
	@Test
	public void visibility() {
		// Get user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// Get visible event
		SingleEvent visibleEvent = SingleEvent.find("byName", "Work").first();
		assertTrue(visibleEvent.isVisible(jack));
		
		// Get invisible event
		SingleEvent invisibleEvent = SingleEvent.find("byName", "Meeting with the mayor").first();
		assertFalse(invisibleEvent.isVisible(jack));
		
		// Get own private event
		SingleEvent ownEvent = SingleEvent.find("byName", "Collections").first();
		assertTrue(ownEvent.isVisible(jack));
	}
	
	@Test
	public void availableJoins() {
		// Get some users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get some events
		SingleEvent e3 = SingleEvent.find("byName", "Work").first();
		SingleEvent e0 = SingleEvent.find("byName", "Collections").first();
		
		assertEquals(1, e3.availableJoins(jack).size());
		assertEquals(0, e3.availableJoins(bud).size());
		
		// Private event can't join
		assertEquals(0, e0.availableJoins(jack).size());
	}
	
	@Test
	public void joinCalendar() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		assertEquals(3, budCalendar.events.size());
		
		// Get a event
		SingleEvent event = SingleEvent.find("byName", "Observation").first();
		assertFalse(event.calendars.contains(budCalendar));
		
		// Join the calendar
		event.joinCalendar(budCalendar);
		assertEquals(4, budCalendar.events.size());
		assertTrue(event.calendars.contains(budCalendar));
				
		// Make sure there weren't created any objects
		assertEquals(7, SingleEvent.count());
		assertEquals(4, Calendar.count());
	}
	
	@Test
	public void showParticipants() {
		// Get a event with no participants
		Event event = Event.find("byName", "Meeting with the mayor").first();
		assertEquals(1, event.calendars.size());
		assertEquals(0, event.participants().size());
		
		// Get a event with 1 participant
		event = Event.find("byName", "Work").first();
		assertEquals(2, event.calendars.size());
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		assertEquals(1, event.participants().size());
		assertTrue(event.participants().contains(jack));
	}
	
	@Test
	public void validDateCheck() {
		// Get a calendar
		Calendar calendar = Calendar.all().first();
		
		// Create an event
		SingleEvent event = new SingleEvent(	calendar, 
												"Test 3", 
												format.parseDateTime("20.10.2011 09:00"),
												format.parseDateTime("20.10.2011 08:00"));
		
		// Try to save it
		assertFalse(event.validateAndSave());
		assertEquals(7, SingleEvent.count());
	}
	
	@Test
	public void isThisDay() {
		// Get an event
		SingleEvent event = SingleEvent.find("byName", "Collections").first();
		
		assertTrue(event.isThisDay(new DateTime().withDayOfMonth(5).withMonthOfYear(10).withYear(2011)));
	}
	
	@Test
	public void isThisDayandLocation() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		DateTime day = new DateTime();
		
		// Get an event
		SingleEvent event = SingleEvent.find("byName", "Collections").first();
		event.startDate = day;
		event.location = loc;
		
		// Create a location
		Location loc2 = new Location();
		loc2.street = "Teststrasse";
		loc2.num = "123";
		loc2.city = "Test";
		loc2.country = "Test";
		loc2.pincode = "12345";
		
		assertTrue(event.isThisDayandLocation(day, loc));
		assertFalse(event.isThisDayandLocation(day.plusDays(2), loc));
		assertFalse(event.isThisDayandLocation(day, loc2));
		assertFalse(event.isThisDayandLocation(day.plusDays(2), loc2));
	}
}