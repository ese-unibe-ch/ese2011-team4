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

public class MessageBoxTest extends UnitTest{
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
		
		// Create a new MessageBox
		MessageBox messageBox = new MessageBox(jack);
		
		assertTrue(messageBox.validateAndSave());
		assertEquals(jack, messageBox.owner);
		assertNotNull(messageBox);
	}
	
	@Test
	public void countUnread_and_getMessage() {
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
	
	@Test
	public void addToDrafts() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();

		assertEquals(0, bud.messageBox.drafts.size());
		
		Message message = new Message(jack, bud);
		bud.messageBox.addToDrafts(message);
		
		assertEquals(1, bud.messageBox.drafts.size());
		assertTrue(bud.messageBox.drafts.contains(message));
	}
}