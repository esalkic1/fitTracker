package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.services.ExerciseMetadataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-metadata")
public class ExerciseMetadataController {
    private final ExerciseMetadataService exerciseMetadataService;

    public ExerciseMetadataController(ExerciseMetadataService exerciseMetadataService){
        this.exerciseMetadataService = exerciseMetadataService;
    }

    @PostMapping
    public ExerciseMetadata createExerciseMetadata(@RequestBody ExerciseMetadata exerciseMetadata) {
        return exerciseMetadataService.createExerciseMetadata(exerciseMetadata);
    }

    @GetMapping("/{id}")
    public ExerciseMetadata getExerciseMetadata(@PathVariable("id") String id) {
        return exerciseMetadataService.getExerciseMetadataById(id);
    }

    @PutMapping("/{id}")
    public ExerciseMetadata updateExerciseMetadata(@PathVariable("id") String id,
                                                   @RequestBody ExerciseMetadata exerciseMetadata) {
        return exerciseMetadataService.updateExerciseMetadata(id, exerciseMetadata);
    }

    @DeleteMapping("/{id}")
    public void deleteExerciseMetadata(@PathVariable("id") String id) {
        exerciseMetadataService.deleteExerciseMetadata(id);
    }

    @GetMapping
    public List<ExerciseMetadata> getAllExerciseMetadata() {
        return exerciseMetadataService.getAllExerciseMetadata();
    }
}
