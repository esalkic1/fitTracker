package ba.unsa.etf.nwt.nutrition_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@EntityListeners(UUIDGenerator.class)
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    @JsonIgnore
    private UUID uuid;

    private String name;
    private Double calories;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    @JsonBackReference
    private Meal meal;

    public Food() {}

    public Food(String name, Double calories, Meal meal) {
        this.name = name;
        this.calories = calories;
        this.meal = meal;
    }
}
