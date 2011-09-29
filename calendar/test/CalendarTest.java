import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.*;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class CalendarTest extends UnitTest {
	Calendar cal;
	User testA, testB;
	Event e1, e2, e3, e4;
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	DateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public void setup() throws ParseException, EndDateBeforeStartDateException {
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
	public void createCalendar() {
		assertEquals(cal.getOwner(), testA);
		assertEquals(cal.getName(), "Test Calendar");
	}
	
	@Test
	public void emptyCalendar() {
		assertTrue(cal.getEvents().isEmpty());
	}
	
	@Test
	public void addEvent() {
		cal.addEvent(e1);
		
		assertFalse(cal.getEvents().isEmpty());
		assertTrue(cal.getEvents().contains(e1));
	}
	
	@Test
	public void sortingEvents() {
		cal.addEvent(e1);
		cal.addEvent(e2);
		cal.addEvent(e3);
		
		List<Event> test = cal.getEvents();
		
		for(int i = 0; i < test.size() - 1; i++)
			assertTrue(test.get(i).getStartDate().before(test.get(i+1).getStartDate()));
	}
	
	@Test
	public void visibility()  {
		assertTrue(cal.isVisible(testA, e1));
		assertTrue(cal.isVisible(testA, e2));
		assertTrue(cal.isVisible(testB, e1));
		assertFalse(cal.isVisible(testB, e2));
	}
	
	@Test
	public void iterator() throws ParseException {
		cal.addEvent(e1);
		cal.addEvent(e2);
		cal.addEvent(e3);
		cal.addEvent(e4);
		
		Iterator<Event> it = cal.getIteratorForUser(testA, dayFormat.parse("11.01.1990"));
		assertEquals(it.next(), e3);
		assertEquals(it.next(), e1);
		assertEquals(it.next(), e2);
		assertEquals(it.next(), e4);
		
		it = cal.getIteratorForUser(testB, dayFormat.parse("11.01.1990"));
		assertEquals(it.next(), e3);
		assertEquals(it.next(), e1);
		assertEquals(it.next(), e4);
		
		it = cal.getIteratorForUser(testA, dayFormat.parse("12.01.1990"));
		assertEquals(it.next(), e4);
	}
	
	@Test
	public void getList() throws ParseException {
		cal.addEvent(e1);
		cal.addEvent(e2);
		cal.addEvent(e3);
		cal.addEvent(e4);
		
		List<Event> list = cal.getListForDate(testA, dayFormat.parse("11.01.1990"));
		assertTrue(list.contains(e1));
		assertTrue(list.contains(e2));
		assertTrue(list.contains(e3));
		assertFalse(list.contains(e4));
		
		list = cal.getListForDate(testB, dayFormat.parse("11.01.1990"));
		assertTrue(list.contains(e1));
		assertFalse(list.contains(e2));
		assertTrue(list.contains(e3));
		assertFalse(list.contains(e4));
		
		list = cal.getListForDate(testA, dayFormat.parse("12.01.1990"));
		assertFalse(list.contains(e1));
		assertFalse(list.contains(e2));
		assertFalse(list.contains(e3));
		assertTrue(list.contains(e4));
	}
	
	@Test
	public void addLongEvent() throws ParseException, EndDateBeforeStartDateException {
		Date start = dateFormat.parse("11.01.1990 11:00");
		Date end = dateFormat.parse("11.02.1990 16:00");
		
		Event longEvent = new Event("Long Event", start, end, false);
		cal.addEvent(longEvent);
		
		List<Event> list = cal.getListForDate(testA, dayFormat.parse("11.01.1990"));
		assertTrue(list.contains(longEvent));
		
		list = cal.getListForDate(testA, dayFormat.parse("11.02.1990"));
		assertFalse(list.contains(longEvent));
	}
}