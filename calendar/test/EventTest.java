import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.*;

import org.junit.Before;
import org.junit.Test;

import play.test.FunctionalTest;

public class EventTest extends FunctionalTest {
	Calendar cal;
	User testA, testB;
	Event e1, e2, e3;
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
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
	}
	
	@Test
	public void createEvent() throws ParseException {
		assertEquals(e1.getName(), "Test Event 1");
		assertEquals(e1.getStartDate(), dateFormat.parse("11.01.1990 11:00"));
		assertEquals(e1.getEndDate(), dateFormat.parse("11.01.1990 16:00"));
		assertFalse(e1.getPrivacy());
	}
	
	@Test(expected=EndDateBeforeStartDateException.class)
	public void createInvalidEvent() throws ParseException, EndDateBeforeStartDateException {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date start = dateFormat.parse("11.01.1990 17:00");
		Date end = dateFormat.parse("11.01.1990 16:00");
		e1 = new Event("Invalid Event", start, end, false);
	}
	
	@Test
	public void compare() {	
		assertTrue(e1.compareTo(e2) < 0);
		assertTrue(e1.compareTo(e3) > 0);
		assertTrue(e2.compareTo(e3) > 0);
	}
}
