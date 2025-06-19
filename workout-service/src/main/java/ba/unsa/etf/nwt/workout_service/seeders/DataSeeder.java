package ba.unsa.etf.nwt.workout_service.seeders;

import ba.unsa.etf.nwt.workout_service.domain.*;
import ba.unsa.etf.nwt.workout_service.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMetadataRepository exerciseMetadataRepository;
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final ExerciseDetailsRepository exerciseDetailsRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;

    public DataSeeder(final UserRepository userRepository, final WorkoutRepository workoutRepository, final ExerciseRepository exerciseRepository,
                      final ExerciseMetadataRepository exerciseMetadataRepository, final WorkoutTemplateRepository workoutTemplateRepository,
                      final ExerciseDetailsRepository exerciseDetailsRepository, final ExerciseTemplateRepository exerciseTemplateRepository){
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseMetadataRepository = exerciseMetadataRepository;
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.exerciseDetailsRepository = exerciseDetailsRepository;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // db already seeded
        }

        User user1 = new User();
        User user2 = new User();
        userRepository.saveAll(Arrays.asList(user1, user2));

        ExerciseDetails benchPressDetails = new ExerciseDetails("Bench Press", "Chest workout using a barbell", "Chest", "Barbell", "Intermediate");
        ExerciseDetails latPulldownDetails = new ExerciseDetails("Lat Pulldown", "Back exercise using a machine", "Back", "Cable Machine", "Beginner");
        ExerciseDetails legPressDetails = new ExerciseDetails("Leg Press", "Leg strengthening using a machine", "Legs", "Machine", "Advanced");
        exerciseDetailsRepository.saveAll(Arrays.asList(benchPressDetails, latPulldownDetails, legPressDetails));

        WorkoutTemplate template1 = new WorkoutTemplate("Full Body Workout", "A full-body workout template", user1, null);
        WorkoutTemplate template2 = new WorkoutTemplate("Leg Day", "Focused on lower body", user2, null);
        workoutTemplateRepository.saveAll(Arrays.asList(template1, template2));

        ExerciseTemplate templateExercise1 = new ExerciseTemplate(benchPressDetails, template1);
        ExerciseTemplate templateExercise2 = new ExerciseTemplate(latPulldownDetails, template1);
        ExerciseTemplate templateExercise3 = new ExerciseTemplate(legPressDetails, template2);
        exerciseTemplateRepository.saveAll(Arrays.asList(templateExercise1, templateExercise2, templateExercise3));

        template1.setExerciseTemplates(Arrays.asList(templateExercise1, templateExercise2));
        template2.setExerciseTemplates(List.of(templateExercise3));
        workoutTemplateRepository.saveAll(Arrays.asList(template1, template2));

        Workout workout1 = new Workout("Full Body Workout", Instant.now(), user1, null);
        Workout workout2 = new Workout("Leg Day", Instant.now(), user2, null);
        workoutRepository.saveAll(Arrays.asList(workout1, workout2));

        Exercise exercise1 = new Exercise(50, 10, 3, workout1, benchPressDetails);
        Exercise exercise2 = new Exercise(25, 12, 4, workout1, latPulldownDetails);
        Exercise exercise3 = new Exercise(70, 8, 4, workout2, legPressDetails);
        exerciseRepository.saveAll(Arrays.asList(exercise1, exercise2, exercise3));

        ExerciseMetadata metadata1 = new ExerciseMetadata(exercise1, "benchpress.mp4", "benchpress.jpg", "Keep your back flat on the bench.");
        exerciseMetadataRepository.saveAll(List.of(metadata1));

        workout1.setExercises(Arrays.asList(exercise1, exercise2));
        workout2.setExercises(List.of(exercise3));
        workoutRepository.saveAll(Arrays.asList(workout1, workout2));

    }
}

