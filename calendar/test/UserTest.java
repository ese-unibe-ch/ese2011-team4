

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import models.*;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest{
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
		assertEquals(3, User.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, User.count());
	}
	
	@Test
	public void create() {
		// Create a new user
		User lynn = new User("lynn.bracken@gmail.com", "secret", "Lynn Bracken");
		
		assertTrue(lynn.validateAndSave());
		assertEquals(4, User.count());
	}
	
	@Test
	public void delete() {
		// Find a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		Long id = jack.id;
		
		// Delete it
		jack.delete();
		
		// Try to find it
		assertNull(User.findById(id));
		assertEquals(2, User.count());
		
		// Count objects
		assertEquals(2, Calendar.count());
		assertEquals(3, Event.count());
	}
	
	@Test
	public void validateUniqueId() {
		// Create a new User with false email
		User mail = new User("lynn.asdhaks.com", "secret", "Test User 1");
		
		assertFalse(mail.validateAndSave());
		assertEquals(3, User.count());
		
		// Create a user with a non unique mail
		User jack = new User("jack.vincennes@lapd.com", "secret", "Test User 2");
		
		assertFalse(jack.validateAndSave());
		assertEquals(3, User.count());
	}
	
	@Test
	public void connect() {
	    assertNotNull(User.connect("jack.vincennes@lapd.com", "secret"));
	    assertNotNull(User.connect("bud.white@lapd.com", "secret"));
	    assertNull(User.connect("bud.white@lapd.com", "notsosecret"));
	    assertNull(User.connect("sid.hudgens@hollywood.com", "secret"));
	}
}
