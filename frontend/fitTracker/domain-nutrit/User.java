package ba.unsa.etf.nwt.nutrition_service.domain;

import ba.unsa.etf.nwt.common.jpa.uuid_generator.AutoGenerateUUID;
import ba.unsa.etf.nwt.common.jpa.uuid_generator.UUIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "user_account")
@EntityListeners(UUIDGenerator.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AutoGenerateUUID
    @Column(unique = true, nullable = false, updatable = false)
    @JsonIgnore
    private UUID uuid;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Meal> meals;

    public User() {}

    public User(List<Meal> meals) {
        this.meals = meals;
    }
}
