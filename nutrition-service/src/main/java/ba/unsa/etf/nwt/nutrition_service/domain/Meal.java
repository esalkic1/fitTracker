package ba.unsa.etf.nwt.nutrition_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@EntityListeners(UUIDGenerator.class)
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    @JsonIgnore
    private UUID uuid;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Food> foods;

    private Instant date;

    public Meal() {}

    public Meal(String name, User user, List<Food> foods, Instant date) {
        this.name = name;
        this.user = user;
        this.foods = foods;
        this.date = date;
    }
}
