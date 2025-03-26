package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(final ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }
}
