package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.workout_service.services.WorkoutTemplateService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkoutTemplateController {
    private final WorkoutTemplateService workoutTemplateService;

    public WorkoutTemplateController(final WorkoutTemplateService workoutTemplateService){
        this.workoutTemplateService = workoutTemplateService;
    }
}
