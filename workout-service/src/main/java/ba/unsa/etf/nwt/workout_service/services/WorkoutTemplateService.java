package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.repositories.WorkoutTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkoutTemplateService {
    private final WorkoutTemplateRepository workoutTemplateRepository;

    public WorkoutTemplateService(final WorkoutTemplateRepository workoutTemplateRepository){
        this.workoutTemplateRepository = workoutTemplateRepository;
    }
}
