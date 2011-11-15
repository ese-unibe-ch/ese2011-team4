package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;

import org.hibernate.annotations.Type;
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
 * A User has a user profile in which he optionally can add information about himself. The user
 * profile contains following informations:
 * <ul>
 * <li>This user's full name</li>
 * <li>This user's nickname</li>
 * <li>This user's gender</li>
 * <li>This user's birthday</li>
 * <li>This usre's address</li>
 * <li>This user's description.</li>
 * </ul>
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
	 * This user's nickname.
	 */
    public String nickname;
	
    /**
     * This user's gender.
     */
	public String gender;
	
	/**
	 * If <code>true</code> then this user's gender is shown in his user profile, otherwise
	 * not.
	 */
	public boolean visiblegender;
	
	/**
	 * This user's birthday.
	 */
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime birthday;
	
	/**
	 * If <code>true</code> then this user's birthday is shown in his user profile, otherwise
	 * not.
	 */
	public boolean visiblebirthday;
	
	/**
	 * This user's address.
	 */
	@OneToOne
	public Location address;
	
	/**
	 * If <code>true</code> then this user's address is shown in his user profile, otherwise
	 * not.
	 */
	public boolean visibleaddress;
	
	/**
	 * This user's telephone number.
	 */
	public String telephone;
	
	/**
	 * If <code>true</code> then this user's telephone number is shown in his user profile, otherwise
	 * not.
	 */
	public boolean visibletelephone;
	
	/**
	 * Text which describes this user.
	 */
	@Lob
	public String descriptionUser;
	
	
	/**
	 * User's constructor. The default behavior is:
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
	 * This method first deletes all calendars of this user and then deletes
	 * this user itself
	 * 
	 * @since Iteration-1
	 * @see Calendar
	 */
	@Override
	public User delete() {
		// First delete calendars
		for(Calendar c : calendars)
			c.delete();
				
		merge();
		return super.delete();
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
	 * Returns this user's full name.
	 * 
	 * @since Iteration-1
	 */
	@Override
	public String toString() {
		return fullname;
	}
	
	/**
	 * Returns this user's gender.
	 * 
	 * @return <code>String gender</code>: This user's gender
	 * @since Iteration-5
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Returns <code>true</code> if this user's gender is visible, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>boolean</code>: <code>true</code> if this user's gender is visible, otherwise
	 * false
	 * @since Iteration-5
	 */
	public boolean getGenderVisibility() {
		return visiblegender;
	}

	/**
	 * Sets this user's gender to the argument gender.
	 * 
	 * @param gen : This user's gender
	 * @since Iteration-5
	 */
	public void setGender(String gen){
		this.gender=gen;
	}
	
	/**
	 * Sets this user's gender visibility to the argument visibility.
	 * 
	 * @param isVisible : This user's gender visibility
	 * @since Iteration-5
	 */
	public void setGenderVisibility(boolean isVisible) {
		this.visiblegender = isVisible;
	}
	
	/**
	 * Returns this user's birthday.
	 * 
	 * @return <code>DateTime birthday</code>: This user's birthday
	 * @since Iteration-5
	 */
	public DateTime getBirthday() {
		return birthday;
	}

	/**
	 * Returns this user's birthday visibility.
	 * 
	 * @return <code>boolean</code>: <code>true</code> if this user's birthday is visible, otherwise
	 * <code>false</code>
	 * @since Iteration-5
	 */
	public boolean getBirthdayVisibility() {
		return visiblebirthday;
	}

	/**
	 * Sets this user's birthday to the argument birthday date.
	 * 
	 * @param birthday : This user's birthday date
	 * @since Iteration-5
	 */
	public void setBirthday(DateTime birthday){
		this.birthday=birthday;
	}
	
	/**
	 * Sets this user's birthday visibility to the argument visibility.
	 * 
	 * @param isVisible : This user's birthday visibility
	 * @since Iteration-5
	 */
	public void setBirthdayVisibility(boolean isVisible) {
		this.visiblebirthday = isVisible;
	}
	
	/**
	 * Returns this user's address.
	 * 
	 * @return <code>Location address</code>: This user's address
	 * @since Iteration-5
	 */
	public Location getAddress() {
		return address;
	}

	/**
	 * Returns this user's address visibility.
	 * 
	 * @return <code>boolean</code>: <code>true</code> if this user's address is visible, 
	 * otherwise <code>false</code>
	 * @since Iteration-5
	 */
	public boolean getAddressVisibility() {
		return visibleaddress;
	}

	/**
	 * Sets this user's address to the argument address.
	 * 
	 * @param loc : This user's address
	 * @since Iteration-5
	 */
	public void setAddress(Location loc){
		this.address=loc;
	}
	
	/**
	 * Sets this user's address visibility to the argument visibility
	 * 
	 * @param isVisible : This user's address visibility
	 * @since Iteration-5
	 */
	public void setAddressVisibility(boolean isVisible) {
		this.visibleaddress = isVisible;
	}

	/**
	 * Returns this user's telephone number.
	 * 
	 * @return <code>String telephone</code>: This user's telephone number
	 * @since Iteration-5
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * Returns this user's telephone number visibility.
	 * 
	 * @return <code>boolean</code>: <code>true</code> if this user's telephone number is visible,
	 * otherwise <code>false</code>
	 * @since Iteration-5
	 */
	public boolean getTelephoneVisibility() {
		return visibletelephone;
	}

	/**
	 * Sets this user's telephone number to the argument telephone number.
	 * 
	 * @param tele : This user's telephone number
	 * @since Iteration-5
	 */
	public void setTelephone(String tele){
		this.telephone=tele;
	}
	
	/**
	 * Sets this user's telephone number visibility to the argument visibility.
	 * 
	 * @param isVisible : This user's telephone visibility
	 * @since Iteration-5
	 */
	public void setTelephoneVisibility(boolean isVisible) {
		this.visibletelephone = isVisible;
	}
	
	/**
	 * Returns this user's nickname.
	 * 
	 * @return <code>String nickname</code>: This user's nickname
	 * @since Iteration-5
	 */
	public String getNickName() {
		return nickname;
	}

	/**
	 * Sets this user's nickname to the argument nickname.
	 * 
	 * @param nick : This user's nickname
	 * @since Iteration-5
	 */
	public void setNickName(String nick){
		this.nickname=nick;
	}
	
	/**
	 * Returns this user's description.
	 * 
	 * @return <code>String descriptionUser</code>: This user's description
	 * @since Iteration-5
	 */
	public String getDescription() {
		return descriptionUser;
	}

	/**
	 * Sets this user's description to the argument description.
	 * 
	 * @param desc : This user's description
	 * @since Iteration-5
	 */
	public void setDescripton(String desc){
		this.descriptionUser=desc;
	}
	
	static class uniqueMailCheck extends Check {
		public boolean isSatisfied(Object user_, Object mail_) {
			String mail = (String) mail_;
			setMessage("validation.uniqueMailCheck");
			return (User.find("byEmail", mail).fetch().size() == 0 || (User.find("byEmail", mail).fetch().size() == 1 && User.find("byEmail", mail).first().equals(user_)));
		}
	}
}