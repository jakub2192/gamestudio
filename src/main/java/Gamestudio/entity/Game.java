package Gamestudio.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
@Entity
public class Game {
	@Id
    private String ident;
	private String name;
	private String description;
	
	
	@Transient
	private double avgRating;
	
	public Game() {
	}
	
	public String getIdent() {
		return ident;
	}

	public Game(String ident, String name, String description) {
		super();
		this.ident = ident;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Game [ident=" + ident + ", name=" + name + ", description=" + description + "]";
	}

	public double getAvgRating() {
		return avgRating;
	}

public void setAvgRating(double avgRating) {
	this.avgRating = avgRating;
}


}
