package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExerciseMetadataService {
    private final ExerciseMetadataRepository exerciseMetadataRepository;

    public ExerciseMetadataService(final ExerciseMetadataRepository exerciseMetadataRepository) {
        this.exerciseMetadataRepository = exerciseMetadataRepository;
    }

    // Create Operation
    public ExerciseMetadata createExerciseMetadata(ExerciseMetadata exerciseMetadata) {
        return exerciseMetadataRepository.save(exerciseMetadata);
    }

    // Read Operation by ID
    public ExerciseMetadata getExerciseMetadataById(String id) {
        Optional<ExerciseMetadata> metadata = exerciseMetadataRepository.findById(Long.valueOf(id));
        return metadata.orElse(null);  // Return null if not found, you can customize this behavior
    }

    // Update Operation
    public ExerciseMetadata updateExerciseMetadata(String id, ExerciseMetadata exerciseMetadata) {
        // TODO
//        if (exerciseMetadataRepository.findById(id)) {
//            exerciseMetadata.setId(id);  // Ensure the entity ID is set before updating
//            return exerciseMetadataRepository.save(exerciseMetadata);
//        }
        return null;  // Return null if the entity doesn't exist
    }

    // Delete Operation
    public void deleteExerciseMetadata(String id) {
        exerciseMetadataRepository.deleteById(Long.valueOf(id));
    }

    // List All Operation
    public List<ExerciseMetadata> getAllExerciseMetadata() {
        return exerciseMetadataRepository.findAll();
    }
}
