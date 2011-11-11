package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import models.User.uniqueMailCheck;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;
@Entity
public class UserProfile  extends Model {
	
	public String useremail;
	
	public String name;
	
	public String gender;
	
	public DateTime birthday;
	
	public Location address;
	
	public UserProfile(String email,String fullname,String gen, DateTime birth,Location addr) {
		this.useremail=email;
		this.name = fullname;
		this.gender = gen;
		this.birthday = birth;
		this.address =addr;
	}
	
	public void EditProfile(UserProfile userprof){
		
			UserProfile uprof=find("byEmail", userprof.useremail).first();
		    uprof.gender=userprof.gender;
		    uprof.birthday=userprof.birthday;
		    uprof.address= userprof.address;
		    
		    
	}
	@Override
	public String toString() {
		return useremail;
	}
	public static UserProfile find(String email) {
		Query query = JPA.em().createQuery("SELECT e FROM UserProfile e "+
				"WHERE UPPER(e.useremail) = UPPER(?1)");
		query.setParameter(1, email.trim());
		query.setMaxResults(1);
		return (query.getResultList().size() > 0)?(UserProfile) query.getResultList().get(0):null;
	}
	
	
}
