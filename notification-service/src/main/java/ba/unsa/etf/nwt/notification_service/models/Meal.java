package ba.unsa.etf.nwt.notification_service.models;

import java.time.Instant;
import java.util.List;

public class Meal {
	private Long id;
	private String name;
	private List<Food> foods;
	private Instant date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Food> getFoods() {
		return foods;
	}

	public void setFoods(List<Food> foods) {
		this.foods = foods;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}
}
