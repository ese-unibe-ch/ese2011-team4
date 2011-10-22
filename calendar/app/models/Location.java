package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Location extends Model{
	public String street;
	public String num;
	public String city;
	public String country;
	public String pincode;

	@Override
	public String toString(){
		return num + ", " + street + ", " + city + ", " + country + ", " + pincode;
	}
}