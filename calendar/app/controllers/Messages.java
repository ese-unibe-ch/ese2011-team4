package controllers;

import models.*;

import play.Logger;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Messages extends Controller {
	public static void messageBox() {
		User connectedUser = Users.getConnectedUser();
		MessageBox msgBox = connectedUser.messageBox;
			
		render(connectedUser, msgBox);
	}
	
	public static void message(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		
		if(Security.check(message)) {
			if(msgBox.drafts.contains(message)) {
				writeMessage(msgId);
			} else {
				message.read();
				render(message);
			}
		} else
			forbidden("Not your message!");
	}
	
	public static void handleMessage(	Long msgId,
										String subject,
										String content) {
		if(params.get("sendMessage")!=null)
			sendMessage(msgId, subject, content);
		else if(params.get("saveDraft")!=null)
			saveDraft(msgId, subject, content);
		else
			deleteDraft(msgId);
	}
	
	public static void writeMessage(Long msgId) {
		Message message = Message.findById(msgId);
		assert message.sender != null && message.recipient != null;
		
		if(Security.check(message)) {
			render(message);
		} else
			forbidden("Not your message!");
	}
	
	public static void reply(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message original = Message.findById(msgId);
		
		if(Security.check(original)) {
			StringBuffer newSubject = new StringBuffer(original.subject);
			newSubject.insert(0, "Re: ");
			
			StringBuffer newContent = new StringBuffer(original.content);
			newContent.insert(0, "\n\n---\nOriginal message: \n");
			
			Message message = new Message(original.recipient, original.sender);
			message.subject = newSubject.toString();
			message.content = newContent.toString();
			message.saveAsDraft(msgBox);
			
			writeMessage(message.id);
		} else
			forbidden("Not your message!");
	}
	
	public static void sendMessage(	Long msgId,
									String subject,
									String content)
	{
		Message message = Message.findById(msgId);
		if(Security.check(message)) {
			message.subject = subject;
			message.content = content;
			
			MessageBox msgBox = Users.getConnectedUser().messageBox;
			msgBox.drafts.remove(message);
			msgBox.save();
			
			try {
				message.send();
				Logger.info("Send message \""+message.subject+"\" to "+message.recipient+".");
				flash.success("Your message was sent to %s.", message.recipient);
				messageBox();
			} catch (Exception e) {
				validation.addError("msg.recipient", "Missing some data");
				params.flash();
		    	validation.keep();
		    	writeMessage(message.id);
			}
		} else
			forbidden("Not your message!");
	}
	
	public static void saveDraft(	Long msgId,
									String subject,
									String content)
	{
		Message message = Message.findById(msgId);
		if(Security.check(message)) {
			if(subject != null) {
				message.subject = subject;
			}
			if(content != null) {
				message.content = content;
			}
				
			MessageBox msgBox = Users.getConnectedUser().messageBox;
			message.saveAsDraft(msgBox);
			flash.success("Your message was saved in your drafts.");
			messageBox();
		} else
			forbidden("Not your message!");
	}
	
	public static void deleteDraft(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		if(Security.check(message)) {
			msgBox.drafts.remove(message);
			msgBox.save();
			message.delete();
			messageBox();
		} else
			forbidden("Not your message!");
	}
	
	public static void delete(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		if(Security.check(message)) {
			msgBox.inbox.remove(message);
			msgBox.drafts.remove(message);
			msgBox.save();
			message.delete();
			messageBox();
		} else
			forbidden("Not your message!");
	}
}