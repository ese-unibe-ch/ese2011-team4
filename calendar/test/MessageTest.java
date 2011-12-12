import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class MessageTest extends UnitTest{
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
		// Create Message Boxes
	    List<User> users = User.all().fetch();
	    for(User u : users) {
	    	if(u.messageBox == null) {
	    		u.messageBox = new MessageBox(u).save();
	    		u.save();
	    		
	    		Query query = JPA.em().createQuery("SELECT msg FROM Message msg " +
	    				"WHERE msg.recipient = ?1");
	    		query.setParameter(1, u);
	    		List<Message> messages = query.getResultList();
	    		for(Message msg: messages) {
	    			msg.inbox = u.messageBox;
	    			u.messageBox.inbox.add(msg);
	    			msg.save();
	    			u.messageBox.save();
	    		}
	    	}
	    }
        // YAML can't load enum
        for(Event e : SingleEvent.all().<SingleEvent>fetch()) {
        	e.type = RepeatingType.NONE;
        	e.save();
        }
        
        EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		event.save();
		assertEquals(3, User.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, User.count());
	}
	
	@Test
	public void create() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		// Create a new Message
		Message message = new Message(jack, bud);
		
		assertTrue(message.validateAndSave());
		assertEquals(jack, message.sender);
		assertEquals(bud, message.recipient);
	}
	
	@Test
	public void send() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		assertEquals(0, bud.messageBox.countUnread());
		Message message = new Message(jack, bud);
		Message message2 = new Message(null, bud);
		Message message3 = new Message(jack, null);
		Message message4 = new Message(null, null);
		try {
			message.send();
		} catch (Exception e) {
			assertTrue(false); // always false. To show that an exception occurred which should not happen.
		}
		try {
			message2.send();
		} catch (Exception e) {
			assertNull(message2.sender);
			assertEquals(bud, message2.recipient);
		}
		try {
			message3.send();
		} catch (Exception e) {
			assertEquals(jack, message3.sender);
			assertNull(message3.recipient);
		}
		try {
			message4.send();
		} catch (Exception e) {
			assertNull(message4.sender);
			assertNull(message4.recipient);
		}
		assertEquals(1, bud.messageBox.countUnread());
	}
	
	@Test
	public void saveAsDraft() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		assertEquals(0, jack.messageBox.drafts.size());
		Message message = new Message(jack, bud);
		message.saveAsDraft(jack.messageBox);
		assertEquals(1, jack.messageBox.drafts.size());
		assertTrue(jack.messageBox.drafts.contains(message));
	}
	
	@Test
	public void read() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		assertEquals(0, bud.messageBox.countUnread());
		Message message = new Message(jack, bud);
		try {
			message.send();
		} catch (Exception e) {
			assertTrue(false); // always false. To show that an exception occurred which should not happen.
		}
		assertEquals(1, bud.messageBox.countUnread());
		message.read();
		assertEquals(0, bud.messageBox.countUnread());
	}
}