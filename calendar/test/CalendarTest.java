
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
		assertEquals(9, Event.count());
		
		// Delete it
		calendar.delete();
		
		// Try to find it
		assertNull(Calendar.findById(id));
		assertEquals(3, Calendar.count());
		
		// Count events
		assertEquals(6, Event.count());
		
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
	
	@Test
	public void visibleEventsInNext30Days() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
				
		// Get two users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		int numJack = calendar.visibleEvents(jack).size();
		int numBud = calendar.visibleEvents(bud).size();
		
		// Create events
		SingleEvent event1 = new SingleEvent(	calendar, 
												"Test1", 
												new DateTime().plusDays(6), 
												new DateTime().plusDays(7));
		
		assertTrue(event1.validateAndSave());
		
		SingleEvent event2 = new SingleEvent(	calendar, 
												"Test2", 
												new DateTime().plusDays(21), 
												new DateTime().plusDays(35));
		event2.isPrivate = true;

		assertTrue(event2.validateAndSave());

		SingleEvent event3 = new SingleEvent(	calendar, 
												"Test3", 
												new DateTime().plusDays(33), 
												new DateTime().plusDays(35));

		assertTrue(event3.validateAndSave());

		// Test method
		assertEquals(numJack+2, calendar.visibleEvents(jack).size());
		assertTrue(calendar.visibleEvents(jack).contains(event1));
		assertTrue(calendar.visibleEvents(jack).contains(event2));
		
		assertEquals(numBud+1, calendar.visibleEvents(bud).size());
		assertTrue(calendar.visibleEvents(bud).contains(event1));
	}
	
	@Test
	public void eventsForReminder() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
				
		// Get a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		int num = calendar.eventsRemind(jack).size();
		
		// Create events
		SingleEvent event1 = new SingleEvent(	calendar, 
												"ReminderTest1", 
												new DateTime().plusMinutes(8), 
												new DateTime().plusMinutes(123));
		
		assertTrue(event1.validateAndSave());
		
		SingleEvent event2 = new SingleEvent(	calendar, 
												"ReminderTest2", 
												new DateTime().plusMinutes(12), 
												new DateTime().plusMinutes(123));

		assertTrue(event2.validateAndSave());

		SingleEvent event3 = new SingleEvent(	calendar, 
												"ReminderTest3", 
												new DateTime().plusMinutes(16), 
												new DateTime().plusMinutes(123));

		assertTrue(event3.validateAndSave());

		// Test method
		assertEquals(num+1, calendar.eventsRemind(jack).size());
		assertTrue(calendar.eventsRemind(jack).contains(event2));
	}
	
	@Test
	public void eventsByLocation() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// Get a location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		assertEquals(1, calendar.events(jack, loc).size());
	}
	
	@Test
	public void eventsByDayAndLocation() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// Get a location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		assertEquals(1, calendar.events(jack, new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011), loc).size());
		loc = null;
		assertEquals(0, calendar.events(jack, new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011), loc).size());
		
	}
	
	@Test
	public void getDaysInMonth() {
		DateTime date1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(13).withMinuteOfHour(10);
		DateTime date2 = new DateTime().withDayOfMonth(8).withMonthOfYear(12).withYear(2011).withHourOfDay(5).withMinuteOfHour(42);
		DateTime date3 = new DateTime().withDayOfMonth(20).withMonthOfYear(2).withYear(2012).withHourOfDay(8).withMinuteOfHour(24);
		
		assertEquals(30, Calendar.getDaysInMonth(date1).size());
		assertEquals(date1.withDayOfMonth(1), Calendar.getDaysInMonth(date1).get(0));
		assertEquals(31, Calendar.getDaysInMonth(date2).size());
		assertTrue(Calendar.getDaysInMonth(date2).contains(date2.withDayOfMonth(31)));
		assertEquals(29, Calendar.getDaysInMonth(date3).size());
		assertEquals(date1.withDayOfMonth(29), Calendar.getDaysInMonth(date1).get(28));
	}
	
//	@Test
//	public void numberOfAllEventsInCalendarByDayAndTime() {
//		// Get a calendar
//		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
//		
//		DateTime start1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(13).withMinuteOfHour(10);
//		DateTime end1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(18).withMinuteOfHour(30);
//		
//		DateTime start2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(18).withMinuteOfHour(10);
//		DateTime end2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(19).withMinuteOfHour(30);
//		
//		assertEquals(2, calendar.numberOfAllEventsInCalendarByDayAndTime(start1, end1));
//		assertEquals(1, calendar.numberOfAllEventsInCalendarByDayAndTime(start2, end2));
//	}
//	
	@Test
	public void numberOfAllEventsInCalendarByDayAndTime() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		DateTime start1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(13).withMinuteOfHour(10);
		DateTime end1 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(18).withMinuteOfHour(30);
		
		DateTime start2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(14).withMinuteOfHour(30);
		DateTime end2 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(17).withMinuteOfHour(30);
		
		DateTime start3 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(18).withMinuteOfHour(10);
		DateTime end3 = new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011).withHourOfDay(19).withMinuteOfHour(30);
		
		// Create events
		EventSeries event1 = new EventSeries(	calendar, 
				"Repeating1", 
				start2, 
				end2,
				RepeatingType.WEEKLY);
		
		assertTrue(event1.validateAndSave());
		
		assertEquals(3, calendar.numberOfAllEventsInCalendarByDayAndTime(start1, end1));
		assertEquals(2, calendar.numberOfAllEventsInCalendarByDayAndTime(start2, end2));
		assertEquals(1, calendar.numberOfAllEventsInCalendarByDayAndTime(start3, end3));
		assertEquals(0, calendar.numberOfAllEventsInCalendarByDayAndTime(start1.withHourOfDay(1), start1.withHourOfDay(2)));
	}

	@Test
	public void birthdays() {
		// Get users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Add a favorite
		jack.addFavorite(bud);
		
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		
		DateTime day = new DateTime().withMonthOfYear(bud.birthday.getMonthOfYear()).withDayOfMonth(bud.birthday.getDayOfMonth());
		List<BirthdayEvent> birthdays1 = calendar.birthdays(jack, day);
		List<BirthdayEvent> birthdays2 = calendar.birthdays(jack, day.plusYears(1));
		List<BirthdayEvent> birthdays3 = calendar.birthdays(jack, day.plusDays(1));
		List<BirthdayEvent> birthdays4 = calendar.birthdays(jack, day.minusDays(1));
		
		assertEquals(1, birthdays1.size());
		assertEquals(bud.birthday.withTime(0, 0, 0, 0), birthdays1.get(0).startDate);
		assertEquals(bud.birthday.withTime(0, 0, 0, 0).plusDays(1), birthdays1.get(0).endDate);
		
		assertEquals(1, birthdays2.size());
		assertEquals(bud.birthday.withTime(0, 0, 0, 0), birthdays2.get(0).startDate);
		assertEquals(bud.birthday.withTime(0, 0, 0, 0).plusDays(1), birthdays2.get(0).endDate);
		
		assertEquals(0, birthdays3.size());
		assertEquals(0, birthdays4.size());
	}
}