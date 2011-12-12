import java.util.List;

import javax.persistence.Query;

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
import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class EventTest extends UnitTest {
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
		assertEquals(8, SingleEvent.count());
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
		
		DateTime start = format.parseDateTime("20.10.2011 10:00");
		DateTime end = format.parseDateTime("20.10.2011 12:00");
		
		// Create events
		Event event1 = Event.createEvent(	calendar, 
											"Test 1", 
											start, 
											end, 
											RepeatingType.NONE,
											end,
											0);
		
		assertTrue(event1.validateAndSave());
		
		Event event2 = Event.createEvent(	calendar, 
											"Test 2", 
											start, 
											end, 
											RepeatingType.WEEKLY,
											end.plusWeeks(2),
											2);
		
		assertTrue(event2.validateAndSave());
		
		// Retrieve data
		SingleEvent e1 = SingleEvent.findById(event1.id);
		assertEquals(event1, e1);
		assertEquals("Test 1", e1.name);
		assertEquals(start, e1.startDate);
		assertEquals(end, e1.endDate);
		assertEquals(false, e1.isPrivate);
		
		EventSeries e2 = EventSeries.findById(event2.id);
		assertEquals(event2, e2);
		assertEquals("Test 2", e2.name);
		assertEquals(start, e2.startDate);
		assertEquals(end, e2.endDate);
		assertEquals(false, e2.isPrivate);
		assertEquals(RepeatingType.WEEKLY, e2.type);
		assertEquals(end.plusWeeks(2), e2.getPeriodEnd());
		assertEquals(2, e2.getRepeatingInterval());
	}
	
	@Test
	public void convertFromSeries() {
		// Get a calendar
		Calendar eds = Calendar.find("byName", "Ed's Future Calandar").first();
		
		// Get a user
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get an event
		EventSeries serie = EventSeries.find("byName", "Weekly Meeting").first();
		serie.addComment(bud, "test");
		serie.calendars.add(eds);
		
		SingleEvent singleEvent = Event.convertFromSeries(serie);

		assertEquals(serie.origin, singleEvent.origin);
		assertEquals(serie.name, singleEvent.name);
		assertEquals(serie.startDate, singleEvent.startDate);
		assertEquals(serie.endDate, singleEvent.endDate);
		assertEquals(serie.description, singleEvent.description);
		assertEquals(serie.isPrivate, singleEvent.isPrivate);
		assertEquals(serie.location, singleEvent.location);
		assertEquals(serie.comments.size(), singleEvent.comments.size());
		for(int i=0; i<serie.comments.size(); i++) {
			assertEquals(serie.comments.get(i).author, singleEvent.comments.get(i).author);
			assertEquals(serie.comments.get(i).content, singleEvent.comments.get(i).content);
			assertEquals(serie.comments.get(i).postedAt, singleEvent.comments.get(i).postedAt);
		}
		assertEquals(serie.calendars.size(), singleEvent.calendars.size());
		assertArrayEquals(serie.calendars.toArray(), singleEvent.calendars.toArray());
	}
	
	@Test
	public void convertFromSingleEvent() {
		// Get a calendar
		Calendar eds = Calendar.find("byName", "Ed's Future Calandar").first();
		
		// Get a user
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Get an event
		SingleEvent singleEvent = SingleEvent.find("byName", "Meet Lynn Bracken").first();
		singleEvent.addComment(bud, "test");
		singleEvent.calendars.add(eds);
		
		Event serie = Event.convertFromSingleEvent(singleEvent, RepeatingType.WEEKLY);

		assertEquals(singleEvent.origin, serie.origin);
		assertEquals(singleEvent.name, serie.name);
		assertEquals(singleEvent.startDate, serie.startDate);
		assertEquals(singleEvent.endDate, serie.endDate);
		assertEquals(singleEvent.description, serie.description);
		assertEquals(singleEvent.isPrivate, serie.isPrivate);
		assertEquals(singleEvent.location, serie.location);
		assertEquals(singleEvent.comments.size(), serie.comments.size());
		for(int i=0; i<singleEvent.comments.size(); i++) {
			assertEquals(singleEvent.comments.get(i).author, serie.comments.get(i).author);
			assertEquals(singleEvent.comments.get(i).content, serie.comments.get(i).content);
			assertEquals(singleEvent.comments.get(i).postedAt, serie.comments.get(i).postedAt);
		}
		assertEquals(singleEvent.calendars.size(), serie.calendars.size());
		assertArrayEquals(singleEvent.calendars.toArray(), serie.calendars.toArray());
		assertEquals(RepeatingType.WEEKLY, serie.type);
	}
	
	@Test
	public void compareTo() {
		// Get some calendars
		Calendar jacks = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get an event
		SingleEvent singleEvent = SingleEvent.find("byName", "Meet Lynn Bracken").first();
		
		// Create events
		SingleEvent event1 = new SingleEvent(jacks, "Test1", singleEvent.startDate, singleEvent.endDate);
		SingleEvent event2 = new SingleEvent(jacks, "Test2", singleEvent.startDate.plusHours(1), singleEvent.endDate);
		SingleEvent event3 = new SingleEvent(jacks, "Test3", singleEvent.startDate.minusHours(1), singleEvent.endDate);
		
		assertEquals(0, singleEvent.compareTo(event1));
		assertEquals(-1, singleEvent.compareTo(event2));
		assertEquals(1, singleEvent.compareTo(event3));
	}
	
	@Ignore("Not working yet")
	@Test
	public void invitations() {
		// Get a calendar
		Calendar jacksCalendar = Calendar.find("byName", "Jacks Agenda").first();
		
		// Get a user
		User bud = User.find("byEmail", "bud.white@lapd.com").first();

		DateTime start = format.parseDateTime("20.10.2011 10:00");
		DateTime end = format.parseDateTime("20.10.2011 12:00");
		
		// Create a event
		Event e = Event.createEvent(jacksCalendar, "Private party", start, end, RepeatingType.NONE, null, 0);
		e.isPrivate = true;
		assertTrue(e.validateAndSave());
		
		assertFalse(e.isVisible(bud));
		assertEquals(0, e.availableJoins(bud).size());
		
		// Add bud to the invitation list
		e.invitations.add(bud);
		assertTrue(e.validateAndSave());
		
		assertTrue(e.isVisible(bud));
		assertEquals(1, e.availableJoins(bud).size());
	}
}