package ba.unsa.etf.nwt.workout_service;

import ba.unsa.etf.nwt.workout_service.domain.*;
import ba.unsa.etf.nwt.workout_service.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMetadataRepository exerciseMetadataRepository;

    @Autowired
    private WorkoutTemplateRepository workoutTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create exercise metadata
        ExerciseMetadata metadata1 = new ExerciseMetadata("Chest", "Barbell", "Intermediate");
        ExerciseMetadata metadata2 = new ExerciseMetadata("Back", "Dumbbell", "Beginner");
        ExerciseMetadata metadata3 = new ExerciseMetadata("Legs", "Machine", "Advanced");

        exerciseMetadataRepository.saveAll(Arrays.asList(metadata1, metadata2, metadata3));

        User user1 = new User(null, null);
        User user2 = new User(null, null);

        userRepository.saveAll(Arrays.asList(user1, user2));

        WorkoutTemplate template1 = new WorkoutTemplate( "Full Body Workout", "A full body workout template", user1);
        WorkoutTemplate template2 = new WorkoutTemplate("Leg Day", "Focused on lower body", user2);

        workoutTemplateRepository.saveAll(Arrays.asList(template1, template2));

        Workout workout1 = new Workout("Workout 1", LocalDateTime.now(), user1, null);
        Workout workout2 = new Workout("Workout 2", LocalDateTime.now(), user2, null);

        workoutRepository.saveAll(Arrays.asList(workout1, workout2));

        Exercise exercise1 = new Exercise("Bench Press",50, 10, 3, workout1, metadata1);
        Exercise exercise2 = new Exercise("Lat Pulldown", 25, 12, 4, workout1, metadata2);

        Exercise exercise3 = new Exercise("Leg Press", 70, 8, 4, workout2, metadata3);

        exerciseRepository.saveAll(Arrays.asList(exercise1, exercise2, exercise3));

        workout1.setExercises(Arrays.asList(exercise1, exercise2));
        workout2.setExercises(List.of(exercise3));

        workoutRepository.saveAll(Arrays.asList(workout1, workout2));
    }
}
