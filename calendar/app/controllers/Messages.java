package controllers;

import models.*;

import play.Logger;
import play.data.validation.Validation;
import play.mvc.Controller;

public class Messages extends Controller {
	public static void messageBox() {
		User connectedUser = Users.getConnectedUser();
		MessageBox msgBox = connectedUser.messageBox;
			
		render(connectedUser, msgBox);
	}
	
	public static void message(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		if(msgBox.drafts.contains(message)) {
			writeMessage(msgId);
		} else {
			message.read();
			render(message);
		}
	}
	
	public static void writeMessage(Long msgId) {
		Message message = Message.findById(msgId);
		assert message.sender != null && message.recipient != null;
		
		render(message);
	}
	
	public static void reply(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message original = Message.findById(msgId);
		
		StringBuffer newSubject = new StringBuffer(original.subject);
		newSubject.insert(0, "Re: ");
		
		StringBuffer newContent = new StringBuffer(original.content);
		newContent.insert(0, "\n\n---\nOriginal message: \n");
		
		Message message = new Message(original.recipient, original.sender);
		message.subject = newSubject.toString();
		message.content = newContent.toString();
		message.saveAsDraft(msgBox);
		
		writeMessage(message.id);
	}
	
	public static void sendMessage(	Long msgId,
									String subject,
									String content)
	{
		Message message = Message.findById(msgId);
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
	}
	
	public static void saveDraft(	Long msgId,
									String subject,
									String content)
	{
		Message message = Message.findById(msgId);
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
	}
	
	public static void deleteDraft(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		
		msgBox.drafts.remove(message);
		msgBox.save();
		message.delete();
		messageBox();
	}
	
	public static void delete(Long msgId) {
		MessageBox msgBox = Users.getConnectedUser().messageBox;
		Message message = Message.findById(msgId);
		msgBox.inbox.remove(message);
		msgBox.drafts.remove(message);
		msgBox.save();
		message.delete();
		messageBox();
	}
}