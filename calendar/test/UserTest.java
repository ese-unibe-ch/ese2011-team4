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

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest{
	Calendar cal;
	User testA, testB;
	Event e1, e2, e3, e4;
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	DateFormat dayFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void createAndRetrieveUser() {
		new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		assertNotNull(jack);
		assertEquals("Jack Vincennes", jack.fullname);
	}
	
	@Test
	public void tryConnectAsUser() {
		new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		assertNotNull(User.connect("jack.vincennes@lapd.com", "secret"));
		assertNull(User.connect("jack.vincennes@lapd.com", "notsosecret"));
		assertNull(User.connect("bud.white@lapd.com", "secret"));
	}
	
	@Test
	public void createCalendar() {
		User jack = new User("jack.vincennes@lapd.com", "secret", "Jack Vincennes").save();
		jack.createCalendar("Jacks Agenda");
		
		assertEquals(1, Calendar.count());
		
		List<Calendar> jacksCalendars = Calendar.find("byOwner", jack).fetch();
		
		assertEquals(1, jacksCalendars.size());
		Calendar firstCalendar = jacksCalendars.get(0);
	    assertNotNull(firstCalendar);
	    assertEquals(jack, firstCalendar.owner);
	    assertEquals("Jacks Agenda", firstCalendar.name);
	}
}
