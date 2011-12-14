import java.util.List;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class RepeatingEventTest extends UnitTest {
	
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
		
		event = EventSeries.find("byName", "Breakfast at Tiffany's").first();
		event.type = RepeatingType.DAILY;
		event.save();
		
		assertEquals(2, EventSeries.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, EventSeries.count());
	}

	@Test
	public void create() {
		// Get a calendar
		Calendar jacksCalendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Create an event
		EventSeries event = new EventSeries(
				jacksCalendar, 
				"Test 1",
				new DateTime().withDayOfYear(17).withYear(2011),
				new DateTime().withDayOfYear(18).withYear(2011),
				RepeatingType.DAILY);
		
		assertTrue(event.validateAndSave());
		assertEquals(3, EventSeries.count());
		
		// Retrieve data
		EventSeries e = EventSeries.findById(event.id);
		assertEquals(event, e);
		assertEquals("Test 1", e.name);
		assertEquals(false, e.isPrivate);
	}
	
	@Test
	public void testWeeklyEvent() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		
		List<SingleEvent> list = budCalendar.events(budCalendar.owner, new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011));
		
		assertEquals(1, list.size());
		assertEquals("Weekly Meeting", list.get(0).name);
	}
	
	@Test
	public void testDailyEvent() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		
		// Does not repeating, as it has an interval of 2
		List<SingleEvent> list = budCalendar.events(budCalendar.owner, new DateTime().withDayOfMonth(12).withMonthOfYear(11).withYear(2011));
		
		assertEquals(0, list.size());		
		
		// Repeats every second day
		list = budCalendar.events(budCalendar.owner, new DateTime().withDayOfMonth(13).withMonthOfYear(11).withYear(2011));
		
		assertEquals(1, list.size());
		assertEquals("Breakfast at Tiffany's", list.get(0).name);
	}
	
	@Test
	public void creationOfDummyEvent() {
		// Get a event
		EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		
		// Create the dummy
		Event dummy = event.createDummyEvent(new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011));
		
		assertEquals(event.name, dummy.name);
		assertEquals(event.comments, dummy.comments);
		assertEquals(event.startDate.withDayOfMonth(7).withMonthOfYear(11).withYear(2011), dummy.startDate);
		assertEquals(RepeatingType.WEEKLY, dummy.type);
	}
	
	@Test
	public void creationOfDummyBirthdayEvent() {
		// Get a calendar
		Calendar jacksCalendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get a user
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Create an event
        DateTime start = bud.birthday.withTime(0, 0, 0, 0);
        DateTime end = start.plusDays(1);
        EventSeries birthday = new EventSeries(
        		jacksCalendar,
        		"Brithday-Alert!",
        		start,
        		end,
        		RepeatingType.YEARLY);
		
        birthday.validateAndSave();
		
		// Create the dummy
		BirthdayEvent dummy = birthday.createDummyBirthdayEvent(start, bud);
		
		assertEquals(birthday.name, dummy.name);
		assertEquals(start, dummy.startDate);
		assertEquals(end, dummy.endDate);
		assertEquals(RepeatingType.YEARLY, dummy.type);
		assertEquals(bud, dummy.user);
	}
	
	@Test
	public void mutateEvent() {
		// Get a event
		EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		Calendar calendar = event.origin;
		
		DateTime dt = new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011);		
		
		event.mutate(dt);
		List<SingleEvent> list = calendar.events(calendar.owner, dt);
		assertEquals(0, list.size());
	}
	
	@Test
	public void isThisDayandLocation() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		// Create a location
		Location loc2 = new Location();
		loc2.street = "Teststrasse";
		loc2.num = "123";
		loc2.city = "Test";
		loc2.country = "Test";
		loc2.pincode = "12345";
		
		DateTime start = new DateTime();
		DateTime end = start.plusHours(2);
		
		// Create an event
		EventSeries event = new EventSeries(budCalendar, "Test1", start, end, RepeatingType.MONTHLY);
		event.location = loc;
		EventSeries event2 = new EventSeries(budCalendar, "Test2", start, end, RepeatingType.YEARLY);
		event2.location = loc;
		
		assertTrue(event.isThisDayandLocation(start, loc));
		assertFalse(event.isThisDayandLocation(start.plusDays(2), loc));
		assertFalse(event.isThisDayandLocation(start, loc2));
		assertFalse(event.isThisDayandLocation(start.plusDays(2), loc2));
		assertFalse(event2.isThisDayandLocation(start.plusDays(2), loc));
	}
	
	@Test
	public void editSingleEvent() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		
		DateTime start = new DateTime();
		DateTime end = start.plusHours(2);
		
		// Create an event
		EventSeries serie = new EventSeries(budCalendar, "Test", start, end, RepeatingType.YEARLY);
		EventSeries serie2 = new EventSeries(budCalendar, "", start, end, RepeatingType.YEARLY);
		
		SingleEvent singleEvent = serie.editSingleEvent(start.plusYears(2));
		SingleEvent singleEvent2 = serie2.editSingleEvent(start.plusYears(2));
		
		assertNotNull(singleEvent);
		assertEquals(serie.origin, singleEvent.origin);
		assertEquals(serie.name, singleEvent.name);
		assertEquals(start.plusYears(2), singleEvent.startDate);
		assertEquals(start.plusYears(2).plusHours(1), singleEvent.endDate);
		assertEquals(RepeatingType.NONE, singleEvent.type);
		assertNull(singleEvent2);
	}
	
	@Test
	public void getPeroidStart() {
		// Get a calendar
		Calendar budCalendar = Calendar.find("byName", "Buds Schedule").first();
		
		DateTime start = new DateTime();
		DateTime end = start.plusHours(2);
		
		// Create an event
		EventSeries serie = new EventSeries(budCalendar, "Test", start, end, RepeatingType.WEEKLY);
		serie.setPeriodStart(start);
		
		assertEquals(start, serie.getPeriodStart());
	}
}