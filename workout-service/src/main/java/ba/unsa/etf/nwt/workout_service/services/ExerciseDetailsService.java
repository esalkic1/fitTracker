package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseDetailsServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
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

    public ExerciseDetails patchExerciseDetails(Long id, JsonPatch patch) throws ExerciseDetailsServiceException {
        ExerciseDetails existing = exerciseDetailsRepository.findById(id)
                .orElseThrow(() -> new ExerciseDetailsServiceException("Could not find exercise details with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode originalNode = objectMapper.convertValue(existing, JsonNode.class);

            JsonNode patchedNode = patch.apply(originalNode);

            ExerciseDetails patched = objectMapper.treeToValue(patchedNode, ExerciseDetails.class);

            if (patched.getName() != null) existing.setName(patched.getName());
            if (patched.getDescription() != null) existing.setDescription(patched.getDescription());
            if (patched.getMuscleGroup() != null) existing.setMuscleGroup(patched.getMuscleGroup());
            if (patched.getEquipment() != null) existing.setEquipment(patched.getEquipment());
            if (patched.getDifficultyLevel() != null) existing.setDifficultyLevel(patched.getDifficultyLevel());

            return exerciseDetailsRepository.save(existing);

        } catch (JsonPatchException | JsonProcessingException e) {
            throw new ExerciseDetailsServiceException("Failed to apply patch: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }
}
