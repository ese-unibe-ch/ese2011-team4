package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Favorite extends Model{

	public Long favoriteId, followerId;
	public String fullname;
	
	public Favorite(Long faId, Long foId, String fullname){
		favoriteId = faId;
		followerId = foId;
		this.fullname = fullname;
	}
}