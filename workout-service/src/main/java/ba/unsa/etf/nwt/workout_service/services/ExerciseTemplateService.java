package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseTemplate;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseTemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseTemplateService {
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final ExerciseDetailsService exerciseDetailsService;
    private final WorkoutTemplateService workoutTemplateService;
    private final ModelMapper modelMapper;

    public ExerciseTemplateService(
            final ExerciseTemplateRepository exerciseTemplateRepository,
            final ExerciseDetailsService exerciseDetailsService,
            final WorkoutTemplateService workoutTemplateService,
            final ModelMapper modelMapper) {
        this.exerciseTemplateRepository = exerciseTemplateRepository;
        this.exerciseDetailsService = exerciseDetailsService;
        this.workoutTemplateService = workoutTemplateService;
        this.modelMapper = modelMapper;
    }

    public List<ExerciseTemplate> getAllExerciseTemplates() {
        return exerciseTemplateRepository.findAll();
    }

    public ExerciseTemplate getExerciseTemplateById(Long id) throws ExerciseTemplateServiceException {
        return exerciseTemplateRepository.findById(id)
                .orElseThrow(() -> new ExerciseTemplateServiceException("Could not find exercise template with id: " + id, ErrorType.ENTITY_NOT_FOUND));
    }

    public ExerciseTemplateDTO createExerciseTemplate(ExerciseTemplateDTO exerciseTemplateDTO) throws ExerciseTemplateServiceException {
        try {
            ExerciseDetails exerciseDetails = exerciseDetailsService.getExerciseDetailsById(exerciseTemplateDTO.getExerciseDetailsId());

            WorkoutTemplate workoutTemplate = workoutTemplateService.getWorkoutTemplateById(exerciseTemplateDTO.getWorkoutTemplateId());

            ExerciseTemplate exerciseTemplate = new ExerciseTemplate();
            exerciseTemplate.setExerciseDetails(exerciseDetails);
            exerciseTemplate.setWorkoutTemplate(workoutTemplate);

            ExerciseTemplate savedTemplate = exerciseTemplateRepository.save(exerciseTemplate);

            ExerciseTemplateDTO result = modelMapper.map(savedTemplate, ExerciseTemplateDTO.class);
            result.setExerciseDetailsId(exerciseDetails.getId());
            result.setWorkoutTemplateId(workoutTemplate.getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseTemplateServiceException("Failed to create exercise template: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public ExerciseTemplateDTO updateExerciseTemplate(Long exerciseTemplateId, ExerciseTemplateDTO exerciseTemplateDTO) throws ExerciseTemplateServiceException {
        ExerciseTemplate existingTemplate = exerciseTemplateRepository.findById(exerciseTemplateId)
                .orElseThrow(() -> new ExerciseTemplateServiceException(
                        "Could not find exercise template with id: " + exerciseTemplateId,
                        ErrorType.ENTITY_NOT_FOUND));

        try {
            if (exerciseTemplateDTO.getExerciseDetailsId() != null) {
                ExerciseDetails exerciseDetails = exerciseDetailsService.getExerciseDetailsById(exerciseTemplateDTO.getExerciseDetailsId());
                existingTemplate.setExerciseDetails(exerciseDetails);
            }

            if (exerciseTemplateDTO.getWorkoutTemplateId() != null) {
                WorkoutTemplate workoutTemplate = workoutTemplateService.getWorkoutTemplateById(exerciseTemplateDTO.getWorkoutTemplateId());
                existingTemplate.setWorkoutTemplate(workoutTemplate);
            }

            ExerciseTemplate updatedTemplate = exerciseTemplateRepository.save(existingTemplate);

            ExerciseTemplateDTO result = modelMapper.map(updatedTemplate, ExerciseTemplateDTO.class);
            result.setExerciseDetailsId(updatedTemplate.getExerciseDetails().getId());
            result.setWorkoutTemplateId(updatedTemplate.getWorkoutTemplate().getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseTemplateServiceException("Failed to update exercise template: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteExerciseTemplate(Long id) throws ExerciseTemplateServiceException {
        ExerciseTemplate exerciseTemplate = exerciseTemplateRepository.findById(id)
                .orElseThrow(() -> new ExerciseTemplateServiceException("Could not find exercise template with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        exerciseTemplateRepository.delete(exerciseTemplate);
    }
}