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
	
	@OneToMany
	public List<Favorite> favorites;
	
	public boolean isAdmin;

	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
		this.favorites = new LinkedList<Favorite>();
	}
	
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	public boolean isInFavorites(User user){
		List<Favorite> favs = 	Favorite.find("followerId", id).fetch();
		boolean found = false;
		Iterator<Favorite> it = favs.iterator();
		while(it.hasNext() && !found){
			Favorite fav = it.next();
			if(fav.favoriteId == user.id)
				found = true;
		}
		return found;
	}
	
	public List<Favorite> favorites() {
		return Favorite.find("followerId", id).fetch();
	}
	
	@Override
	public User delete() {
		for(Calendar c : calendars)
			c.delete();
		
		return super.delete();
	}
	
	static class uniqueMailCheck extends Check {
		public boolean isSatisfied(Object user_, Object mail_) {
			String mail = (String) mail_;
			setMessage("validation.uniqueMailCheck");
			return User.find("byEmail", mail).fetch().size() == 0;
		}
	}
}
