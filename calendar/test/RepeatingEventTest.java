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
		assertEquals(1, EventSeries.count());
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
		assertEquals(2, EventSeries.count());
		
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
		EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		
		List<SingleEvent> list = budCalendar.events(budCalendar.owner, new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011));
		
		assertEquals(1, list.size());
		assertEquals("Weekly Meeting", list.get(0).name);
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
		assertEquals(RepeatingType.WEEKLY, dummy.type);
	}
}