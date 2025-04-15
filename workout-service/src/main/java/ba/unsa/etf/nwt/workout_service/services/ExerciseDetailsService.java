package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseDetailsServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ExerciseDetailsService {
    private final ExerciseDetailsRepository exerciseDetailsRepository;

    public ExerciseDetailsService(final ExerciseDetailsRepository exerciseDetailsRepository) {
        this.exerciseDetailsRepository = exerciseDetailsRepository;
    }

    public List<ExerciseDetails> getAllExerciseDetails() {
        return exerciseDetailsRepository.findAll();
    }

    public ExerciseDetails getExerciseDetailsById(Long id) throws ExerciseDetailsServiceException {
        return exerciseDetailsRepository.findById(id)
                .orElseThrow(() -> new ExerciseDetailsServiceException("Could not find exercise details with id: " + id, ErrorType.ENTITY_NOT_FOUND));
    }

    public List<ExerciseDetails> getExerciseDetailsByMuscleGroup(String muscleGroup) {
        return exerciseDetailsRepository.findByMuscleGroup(muscleGroup);
    }

    public List<ExerciseDetails> getExerciseDetailsByDifficultyLevel(String difficultyLevel) {
        return exerciseDetailsRepository.findByDifficultyLevel(difficultyLevel);
    }

    public List<ExerciseDetails> searchExerciseDetailsByName(String name) {
        return exerciseDetailsRepository.findByName(name);
    }

    public ExerciseDetails createExerciseDetails(ExerciseDetails exerciseDetails) throws ExerciseDetailsServiceException {
        try {
            return exerciseDetailsRepository.save(exerciseDetails);
        } catch (Exception e) {
            throw new ExerciseDetailsServiceException("Failed to create exercise details: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public ExerciseDetails updateExerciseDetails(Long id, ExerciseDetails updatedDetails) throws ExerciseDetailsServiceException {
        ExerciseDetails existingDetails = exerciseDetailsRepository.findById(id)
                .orElseThrow(() -> new ExerciseDetailsServiceException("Could not find exercise details with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        if (updatedDetails.getName() != null) {
            existingDetails.setName(updatedDetails.getName());
        }
        if (updatedDetails.getDescription() != null) {
            existingDetails.setDescription(updatedDetails.getDescription());
        }
        if (updatedDetails.getMuscleGroup() != null) {
            existingDetails.setMuscleGroup(updatedDetails.getMuscleGroup());
        }
        if (updatedDetails.getEquipment() != null) {
            existingDetails.setEquipment(updatedDetails.getEquipment());
        }
        if (updatedDetails.getDifficultyLevel() != null) {
            existingDetails.setDifficultyLevel(updatedDetails.getDifficultyLevel());
        }

        try {
            return exerciseDetailsRepository.save(existingDetails);
        } catch (Exception e) {
            throw new ExerciseDetailsServiceException("Failed to update exercise details: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteExerciseDetails(Long id) throws ExerciseDetailsServiceException {
        ExerciseDetails exerciseDetails = exerciseDetailsRepository.findById(id)
                .orElseThrow(() -> new ExerciseDetailsServiceException("Could not find exercise details with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        exerciseDetailsRepository.delete(exerciseDetails);
    }
}
