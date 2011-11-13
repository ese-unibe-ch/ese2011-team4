package models;

import java.util.ArrayList;
import java.util.List;


/**
 * The Email class represents an EMail and is a helper class for the reminder 
 * function to remind users of upcoming events.
 * <p>
 * An Email has all the necessary fields in order to generate a standard email.
 * This are:
 * <ul>
 * <li>from: From whom this mail is generated</li>
 * <li>to: Email recipient</li>
 * <li>cc: Additional recipients</li>
 * <li>bcc: Additional recipients who will not be shown to other recipients</li>
 * <li>subject: Email's subject</li>
 * <li>text: Contains the text of this email</li>
 * <li>mimeType: Defines the data format of this email.
 * </ul>
 * 
 * @since 	Iteration-4
 */
public class Email {
	
	/**
	 * Author of this email.
	 */
	private String from;
	
	/**
	 * This email's recipient.
	 */
	private String[] to;
	
	/**
	 * Additional recipients.
	 */
	private String[] cc;
	
	/**
	 * Additional recipients who are not shown to the other recipients.
	 */
	private String[] bcc;
	
	/**
	 * This email's subject.
	 */
	private String subject;
	
	/**
	 * The text which this email contains.
	 */
	private String text;
	
	/**
	 * Data format of this email.
	 */
	private String mimeType;
 
 
	/**
	 * Returns the author of the email.
	 * @return <code>String from</code>: Author of this email
	 */
	public String getFrom() {
		return from;
	}
	
	/**
	 * Sets this emil's author to <code>String from</code>.
	 * @param from	Author of this email
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	/**
	 * Returns this email's recipient.
	 * @return <code>String[]</code>: This email's recipient
	 */
	public String[] getTo() {
		return to;
	}
	
	/**
	 * Sets this email's recipient to the given recipient.
	 * @param to Recipient to whom this email is sent to 
	 */
	public void setTo(String... to) {
		this.to = to;
	}
	
	/**
	 * Returns an <code>Array</code> of all recipients to whom this email is sent to.
	 * @return <code>String[]</code>: This email's recipients
	 */
	public String[] getCc() {
		return cc;
	}
	
	/**
	 * Adds the given recipients to this email's list of recipients.
	 * @param cc List of recipients to whom this email is sent to 
	 */
	public void setCc(String... cc) {
		this.cc = cc;
	}
	
	/**
	 * Returns an <code>Array</code> of all hidden recipients to whom this email is sent to.
	 * @return <code>String[]</code>: This email's hidden recipients
	 */
	public String[] getBcc() {
		return bcc;
	}
	
	/**
	 * Adds the given hidden recipients to this email's list of hidden recipients.
	 * @param bcc List of hidden recipients to whom this email is sent to.
	 */
	public void setBcc(String... bcc) {
		this.bcc = bcc;
	}
	
	/**
	 * Returns this email's subject.
	 * @return <code>String subject</code>: This email's subject
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Sets this email's subject to the given subject.
	 * @param subject This email's subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Returns the text contained in this email.
	 * @return <code>String text</code>: Text contained in this email
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets this email's text to the given text.
	 * @param text This email's text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Returns the data format of this email.
	 * @return <code>String mimeType</code>: Data format of this email
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Sets this email's data format to the given data format.
	 * @param mimeType This email's data format
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}