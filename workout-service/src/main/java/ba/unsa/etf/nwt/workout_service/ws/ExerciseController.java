package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.workout_service.services.ExerciseService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(final ExerciseService exerciseService){
        this.exerciseService = exerciseService;
    }
}
