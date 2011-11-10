import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.management.timer.Timer;

import models.Calendar;
import models.Email;
import models.Event;
import models.SingleEvent;
import models.User;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import play.jobs.*;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.joda.time.DateTime;

import play.libs.Mail;

@Every("5min")
public class EMailClass extends Job {

	public void doJob() {

	

		List<User> users = User.findAll();
		for (User user : users) {
			List<Calendar> calendars = Calendar.find("owner", user).fetch();
			for (Calendar calendar : calendars) {
				List<SingleEvent> events = calendar.eventsRemind(user);
				for (Event event : events) {
					Email newmail = new Email();
					newmail.setFrom("kumar.simpal.sharma@gmail.com");
					newmail.setTo(user.email);
					newmail.setCc(user.email);
					newmail.setBcc(user.email);
					newmail.setSubject(event.name);
					newmail.setText("Reminder mail" + event.description);
					sendMail(newmail.getFrom(), "Ticino123", "smtp.gmail.com",
							"465", "true", "true", true,
							"javax.net.ssl.SSLSocketFactory", "false",
							newmail.getTo(), newmail.getCc(), newmail.getBcc(),
							newmail.getSubject(), newmail.getText());
				}
			}
		}
		/*
		 * Email newmail = new Email();
		 * newmail.setFrom("kumar.simpal.sharma@gmail.com");
		 * newmail.setTo("simpal.kumar@yahoo.com");
		 * newmail.setCc("simpal.kumar@gmail.com");
		 * newmail.setBcc("simpal.kumar@gmail.com");
		 * newmail.setSubject("trial mail");
		 * newmail.setText("just a trial mail");
		 * sendMail(newmail.getFrom(),"Ticino123","smtp.gmail.com","465","true",
		 * "true"
		 * ,true,"javax.net.ssl.SSLSocketFactory","false",newmail.getTo(),
		 * newmail .getCc(),newmail.getBcc(),
		 * newmail.getSubject(),newmail.getText());
		 */

	}

	public synchronized static boolean sendMail(String userName,
			String passWord, String host, String port, String starttls,
			String auth, boolean debug, String socketFactoryClass,
			String fallback, String[] to, String[] cc, String[] bcc,
			String subject, String text) {
		Properties props = new Properties();

		props.put("mail.smtp.user", userName);
		props.put("mail.smtp.host", host);
		if (!"".equals(port))
			props.put("mail.smtp.port", port);
		if (!"".equals(starttls))
			props.put("mail.smtp.starttls.enable", starttls);
		props.put("mail.smtp.auth", auth);
		if (debug) {
			props.put("mail.smtp.debug", "true");
		} else {
			props.put("mail.smtp.debug", "false");
		}
		if (!"".equals(port))
			props.put("mail.smtp.socketFactory.port", port);
		if (!"".equals(socketFactoryClass))
			props.put("mail.smtp.socketFactory.class", socketFactoryClass);
		if (!"".equals(fallback))
			props.put("mail.smtp.socketFactory.fallback", fallback);

		try {
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(debug);
			MimeMessage msg = new MimeMessage(session);
			msg.setText(text);
			msg.setSubject(subject);
			// msg.setFrom(new InternetAddress("p_sambasivarao@sutyam.com"));
			msg.setFrom(new InternetAddress("admin@calpal.com"));
			for (int i = 0; i < to.length; i++) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						to[i]));
			}
			for (int i = 0; i < cc.length; i++) {
				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(
						cc[i]));
			}
			for (int i = 0; i < bcc.length; i++) {
				msg.addRecipient(Message.RecipientType.BCC,
						new InternetAddress(bcc[i]));
			}
			msg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(host, userName, passWord);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception mex) {
			mex.printStackTrace();
			return false;
		}
	}

}
