package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.Query;

import org.joda.time.DateTime;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;


/**
 * The User class represents an end-user who uses the calendar application.
 * <p>
 * A User has a user account and is identified by it's email address and password. Furthermore
 * every user has a full name, zero or more calendars and a favorite list. A user can also be 
 * administrator of the calendar application.
 * <p>
 * The class User includes methods for
 * <ul>
 * <li>connecting this user to the calendar application</li>
 * <li>verifying if a certain user is in this user's favorite list</li>
 * <li>adding a certain user to this user's favorite list</li>
 * <li>removing a certain user from this user's favorite list</li>
 * <li>deleting this user</li>
 * </ul>
 * 
 * @since Iteration-1
 * @see Calendar
 */
@Entity
public class User extends Model {
	
	/**
	 * This user's email address.
	 */
	@Email
	@CheckWith(uniqueMailCheck.class)
	@Required
	@Column(unique=true)
	public String email;
	
	/**
	 * This users's password.
	 */
	@Required
	public String password;
	
	/**
	 * This user's full name, consisting of his first name and last name.
	 */
	@Required
	public String fullname;
	
	/**
	 * This user's calendars.
	 */
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<Calendar> calendars;
	
	/**
	 * This user's favorite users.
	 */
	@ManyToMany
	public List<User> favorites;
	
	/**
	 * <code>true</code> if this user is the administrator, otherwise <code>false</code>
	 */
	public boolean isAdmin;
	
	/**
	 * User's constructor. The default behaviour is:
	 * <ul>
	 * <li>User has an email address</li>
	 * <li>User has a password</li>
	 * <li>User has a full name, consisting of his first name and last name</li>
	 * <li>User has zero or more favorite users</li>
	 * </ul>
	 * 
	 * @param email		this user's email address
	 * @param password	this user's password
	 * @param fullname	this user's full name
	 */
	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
		this.favorites = new LinkedList<User>();
	}
	
	/**
	 * Returns this user.
	 * <p>
	 * This is a helper method to log in this user to the application.
	 * 
	 * @param email		email address of the user to log in
	 * @param password	password of the user to log in
	 * @return this user if he exists
	 * @since Iteration-1
	 */
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	
	/**
	 * Returns <code>true</code> if the argument user is in this user's favorite list, otherwise
	 * <code>false</code>
	 * 
	 * @param user		user to check for favorite user
	 * @return <code>true</code> if argument user is a favorite user of this user, otherwise
	 * <code>false</code>
	 * @since Iteration-2 
	 */
	public boolean isFavorite(User user){
		return favorites.contains(user);
	}
	
	
	/**
	 * Adds the argument user to this user's favorite list.
	 * 
	 * @param user	user to add to this user's favorite list
	 * @since Iteration-2
	 */
	public void addFavorite(User user) {
		assert !favorites.contains(user);
		favorites.add(user);
	}
	
	
	/**
	 * Removes the argument user from this user's favorite list.
	 * 
	 * @param user	user to remove from this user's favorite list.
	 * @since Iteration-2
	 */
	public void removeFavorite(User user) {
		assert favorites.contains(user);
		favorites.remove(user);
	}
	
	
	/**
	 * This method first deletes all calendars of this user and then deletes
	 * this user itself
	 * 
	 * @since Iteration-1
	 * @see Calendar
	 */
	@Override
	public User delete() {
		for(Calendar c : calendars)
			c.delete();
		
		return super.delete();
	}
	
	/**
	 * Returns this user's full name.
	 * 
	 * @since Iteration-1
	 */
	@Override
	public String toString() {
		return fullname;
	}
	
	static class uniqueMailCheck extends Check {
		public boolean isSatisfied(Object user_, Object mail_) {
			String mail = (String) mail_;
			setMessage("validation.uniqueMailCheck");
			return User.find("byEmail", mail).fetch().size() == 0;
		}
	}
}