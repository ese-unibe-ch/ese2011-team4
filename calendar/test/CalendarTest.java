
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
		Calendar calendar = Calendar.find("byName", "Jacks Secret Agenda").first();
		Long id = calendar.id;
		
		// Count events
		assertEquals(7, SingleEvent.count());
		
		// Delete it
		calendar.delete();
		
		// Try to find it
		assertNull(Calendar.findById(id));
		assertEquals(3, Calendar.count());
		
		// Count events
		assertEquals(6, SingleEvent.count());
		
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
		
		assertEquals(2, jacks.eventsByDay(new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011), jack).size());
		assertEquals(1, jacks.eventsByDay(new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011), bud).size());
		
		// Event over 3 days
		assertEquals(1, eds.eventsByDay(new DateTime().withDayOfMonth(4).withMonthOfYear(11).withYear(2011), jack).size());
		assertEquals(1, eds.eventsByDay(new DateTime().withDayOfMonth(5).withMonthOfYear(11).withYear(2011), jack).size());
		assertEquals(1, eds.eventsByDay(new DateTime().withDayOfMonth(6).withMonthOfYear(11).withYear(2011), jack).size());
		assertEquals(1, eds.eventsByDay(new DateTime().withDayOfMonth(7).withMonthOfYear(11).withYear(2011), jack).size());
		
		assertEquals(0, jacks.eventsByDay(new DateTime().withDayOfMonth(6).withMonthOfYear(11).withYear(2011), jack).size());
		assertEquals(0, jacks.eventsByDay(new DateTime().withDayOfMonth(4).withMonthOfYear(11).withYear(2011), jack).size());
	}
	
	@Test
	public void visibleEvents() {
		// Get a calendar
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
				
		// Get two users
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Test method
		assertEquals(4, calendar.visibleEvents(jack));
		assertEquals(2, calendar.visibleEvents(bud));
	}
}