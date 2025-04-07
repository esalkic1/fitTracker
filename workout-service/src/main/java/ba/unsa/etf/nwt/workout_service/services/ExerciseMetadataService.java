package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseMetadataDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseMetadataServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseMetadataRepository;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseMetadataService {
    private final ExerciseMetadataRepository exerciseMetadataRepository;
    private final ExerciseRepository exerciseRepository;
    private final ModelMapper modelMapper;

    public ExerciseMetadataService(
            final ExerciseMetadataRepository exerciseMetadataRepository,
            final ExerciseRepository exerciseRepository,
            final ModelMapper modelMapper) {
        this.exerciseMetadataRepository = exerciseMetadataRepository;
        this.exerciseRepository = exerciseRepository;
        this.modelMapper = modelMapper;
    }

    public List<ExerciseMetadata> getAllExerciseMetadata() {
        return exerciseMetadataRepository.findAll();
    }

    public ExerciseMetadata getExerciseMetadataById(Long id) throws ExerciseMetadataServiceException {
        return exerciseMetadataRepository.findById(id)
                .orElseThrow(() -> new ExerciseMetadataServiceException("Could not find exercise metadata with id: " + id, ErrorType.ENTITY_NOT_FOUND));
    }

    public ExerciseMetadata getExerciseMetadataByExerciseId(Long exerciseId) throws ExerciseMetadataServiceException {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseMetadataServiceException("Could not find exercise with id: " + exerciseId, ErrorType.ENTITY_NOT_FOUND));

        return exerciseMetadataRepository.findByExerciseId(exerciseId)
                .orElseThrow(() -> new ExerciseMetadataServiceException("No metadata found for exercise with id: " + exerciseId, ErrorType.ENTITY_NOT_FOUND));
    }

    public ExerciseMetadataDTO createExerciseMetadata(ExerciseMetadataDTO exerciseMetadataDTO) throws ExerciseMetadataServiceException {
        try {
            Exercise exercise = exerciseRepository.findById(exerciseMetadataDTO.getExerciseId())
                    .orElseThrow(() -> new ExerciseMetadataServiceException(
                            "Could not find exercise with id: " + exerciseMetadataDTO.getExerciseId(),
                            ErrorType.ENTITY_NOT_FOUND));

            exerciseMetadataRepository.findByExerciseId(exercise.getId())
                    .ifPresent(existing -> {
                        throw new RuntimeException("Metadata already exists for this exercise. Use update instead.");
                    });

            ExerciseMetadata exerciseMetadata = modelMapper.map(exerciseMetadataDTO, ExerciseMetadata.class);
            exerciseMetadata.setExercise(exercise);

            ExerciseMetadata savedMetadata = exerciseMetadataRepository.save(exerciseMetadata);
            ExerciseMetadataDTO result = modelMapper.map(savedMetadata, ExerciseMetadataDTO.class);
            result.setExerciseId(exercise.getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseMetadataServiceException("Failed to create exercise metadata: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public ExerciseMetadataDTO updateExerciseMetadata(Long id, ExerciseMetadataDTO exerciseMetadataDTO) throws ExerciseMetadataServiceException {
        ExerciseMetadata existingMetadata = exerciseMetadataRepository.findById(id)
                .orElseThrow(() -> new ExerciseMetadataServiceException(
                        "Could not find exercise metadata with id: " + id,
                        ErrorType.ENTITY_NOT_FOUND));

        try {
            if (exerciseMetadataDTO.getExerciseId() != null &&
                    !exerciseMetadataDTO.getExerciseId().equals(existingMetadata.getExercise().getId())) {

                Exercise exercise = exerciseRepository.findById(exerciseMetadataDTO.getExerciseId())
                        .orElseThrow(() -> new ExerciseMetadataServiceException(
                                "Could not find exercise with id: " + exerciseMetadataDTO.getExerciseId(),
                                ErrorType.ENTITY_NOT_FOUND));

                exerciseMetadataRepository.findByExerciseId(exercise.getId())
                        .ifPresent(existing -> {
                            if (existing.getId() != existingMetadata.getId()) {
                                throw new RuntimeException("Metadata already exists for the target exercise.");
                            }
                        });

                existingMetadata.setExercise(exercise);
            }

            if (exerciseMetadataDTO.getAdditionalNotes() != null) {
                existingMetadata.setAdditionalNotes(exerciseMetadataDTO.getAdditionalNotes());
            }

            ExerciseMetadata updatedMetadata = exerciseMetadataRepository.save(existingMetadata);
            ExerciseMetadataDTO result = modelMapper.map(updatedMetadata, ExerciseMetadataDTO.class);
            result.setExerciseId(updatedMetadata.getExercise().getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseMetadataServiceException("Failed to update exercise metadata: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteExerciseMetadata(Long id) throws ExerciseMetadataServiceException {
        ExerciseMetadata exerciseMetadata = exerciseMetadataRepository.findById(id)
                .orElseThrow(() -> new ExerciseMetadataServiceException("Could not find exercise metadata with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        exerciseMetadataRepository.delete(exerciseMetadata);
    }

    private void validateExerciseMetadata(ExerciseMetadata exerciseMetadata) throws ExerciseMetadataServiceException {
        if (exerciseMetadata.getExercise() == null) {
            throw new ExerciseMetadataServiceException("Exercise must be provided", ErrorType.VALIDATION_FAILED);
        }

        // Verify that the referenced exercise exists
        exerciseRepository.findById(exerciseMetadata.getExercise().getId())
                .orElseThrow(() -> new ExerciseMetadataServiceException("Could not find exercise with id: " + exerciseMetadata.getExercise().getId(), ErrorType.ENTITY_NOT_FOUND));
    }
}