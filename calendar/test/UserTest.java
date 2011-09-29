import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.*;

import org.junit.Test;
import org.junit.Before;

import play.test.UnitTest;

public class UserTest extends UnitTest{
	Calendar cal;
	User testA, testB;
	Event e1, e2, e3, e4;
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	DateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public void setup() throws ParseException, InvalidEventException {
		testA = new User("A");
		testB = new User("B");
		cal = new Calendar("Test Calendar", testA);
		
		Date start = dateFormat.parse("11.01.1990 11:00");
		Date end = dateFormat.parse("11.01.1990 16:00");
		e1 = new Event("Test Event 1", start, end, false);
		
		start = dateFormat.parse("11.01.1990 12:00");
		end = dateFormat.parse("11.01.1990 15:00");
		e2 = new Event("Test Event 2", start, end, true);
		
		start = dateFormat.parse("11.01.1990 08:00");
		end = dateFormat.parse("11.01.1990 16:00");
		e3 = new Event("Test Event 3", start, end, false);
		
		start = dateFormat.parse("12.01.1990 08:00");
		end = dateFormat.parse("12.01.1990 16:00");
		e4 = new Event("Test Event 4", start, end, false);
	}
	
	@Test
	public void createUser() {
		testA = new User("testA");
		assertEquals(testA.getName(), "testA");
	}
	
	@Test
	public void createCalendarFromUser() {
		cal = testA.createCalendar("testA's Calendar");
		assertEquals(cal.getOwner(), testA);
		assertEquals(cal.getName(), "testA's Calendar");
	}
	
	@Test
	public void createEventForUser() throws ParseException, InvalidEventException {
		Date start = dateFormat.parse("11.01.1990 11:00");
		Date end = dateFormat.parse("11.01.1990 11:11");
		testA.createEvent(cal, "TestEvent", start, end, false);
		
		assertFalse(cal.getEvents().isEmpty());
	}
	
	@Test
	public void obtainListForDate() throws ParseException {
		Date specificDate = dayFormat.parse("11.01.1990");
		cal.addEvent(e1);
		cal.addEvent(e2);
		cal.addEvent(e3);
		cal.addEvent(e4);
		
		List<Event> list = testA.getList(cal, specificDate);
		assertTrue(list.contains(e1));
		assertTrue(list.contains(e2));
		assertTrue(list.contains(e3));
		assertFalse(list.contains(e4));
	}
	
	@Test
	public void createIteratorForDate() throws ParseException {
		Date specificDate = dayFormat.parse("11.01.1990");
		cal.addEvent(e1);
		cal.addEvent(e2);
		cal.addEvent(e3);
		cal.addEvent(e4);
		
		Iterator<Event> it = testA.getIterator(cal, specificDate);
		
		assertEquals(it.next(), e3);
		assertEquals(it.next(), e1);
		assertEquals(it.next(), e2);
		assertEquals(it.next(), e4);
	}
}
