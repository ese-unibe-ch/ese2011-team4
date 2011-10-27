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

@Entity
public class User extends Model {
	@Email
	@CheckWith(uniqueMailCheck.class)
	@Required
	@Column(unique=true)
	public String email;
	
	@Required
	public String password;
	
	@Required
	public String fullname;
	
	@OneToMany(mappedBy="owner")
	public List<Calendar> calendars;
	
	@ManyToMany
	public List<User> favorites;
	
	public boolean isAdmin;

	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
		this.favorites = new LinkedList<User>();
	}
	
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	public boolean isFavorite(User user){
		return favorites.contains(user);
	}
	
	public void addFavorite(User user) {
		assert !favorites.contains(user);
		favorites.add(user);
	}
	
	public void removeFavorite(User user) {
		assert favorites.contains(user);
		favorites.remove(user);
	}
	
	@Override
	public User delete() {
		for(Calendar c : calendars)
			c.delete();
		
		return super.delete();
	}
	
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