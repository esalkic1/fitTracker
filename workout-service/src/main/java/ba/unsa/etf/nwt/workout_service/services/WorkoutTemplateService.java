package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.*;
import ba.unsa.etf.nwt.workout_service.dto.*;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseTemplateRepository;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutTemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutTemplateService {
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ExerciseTemplateRepository exerciseTemplateRepository;

    public WorkoutTemplateService(final WorkoutTemplateRepository workoutTemplateRepository,
                                  final UserService userService,
                                  final ModelMapper modelMapper,
                                  final ExerciseTemplateRepository exerciseTemplateRepository) {
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
    }

    public List<WorkoutTemplate> getAllWorkoutTemplates() {
        return workoutTemplateRepository.findAll();
    }

    public List<WorkoutTemplate> getWorkoutTemplatesByUserId(Long userId) throws WorkoutTemplateServiceException {
        try {
            User user = userService.getUserById(userId);
            return workoutTemplateRepository.findByUser(user);
        } catch (Exception e) {
            throw new WorkoutTemplateServiceException("Failed to create workout template: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public WorkoutTemplate getWorkoutTemplateById(Long id) throws WorkoutTemplateServiceException {
        return workoutTemplateRepository.findById(id)
                .orElseThrow(() -> new WorkoutTemplateServiceException("Could not find workout template with id: " + id, ErrorType.ENTITY_NOT_FOUND));
    }

    public WorkoutTemplateDTO createWorkoutTemplate(WorkoutTemplateDTO workoutTemplateDTO) throws WorkoutTemplateServiceException {
        try {
            User user = userService.getUserById(workoutTemplateDTO.getUserId());

            WorkoutTemplate workoutTemplate = modelMapper.map(workoutTemplateDTO, WorkoutTemplate.class);
            workoutTemplate.setUser(user);

            WorkoutTemplate savedTemplate = workoutTemplateRepository.save(workoutTemplate);
            WorkoutTemplateDTO result = modelMapper.map(savedTemplate, WorkoutTemplateDTO.class);
            result.setUserId(user.getId());

            return result;
        } catch (Exception e) {
            throw new WorkoutTemplateServiceException("Failed to create workout template: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public WorkoutTemplateDTO updateWorkoutTemplate(Long workoutTemplateId, WorkoutTemplateDTO workoutTemplateDTO) throws WorkoutTemplateServiceException {
        WorkoutTemplate existingTemplate = workoutTemplateRepository.findById(workoutTemplateId)
                .orElseThrow(() -> new WorkoutTemplateServiceException(
                        "Could not find workout template with id: " + workoutTemplateId,
                        ErrorType.ENTITY_NOT_FOUND));

        try {
            workoutTemplateDTO.setUserId(existingTemplate.getUser().getId());
            User user = userService.getUserById(existingTemplate.getUser().getId());
            modelMapper.map(workoutTemplateDTO, existingTemplate);
            existingTemplate.setId(workoutTemplateId);
            existingTemplate.setUser(user);

            WorkoutTemplate updatedTemplate = workoutTemplateRepository.save(existingTemplate);
            WorkoutTemplateDTO result = modelMapper.map(updatedTemplate, WorkoutTemplateDTO.class);
            result.setUserId(user.getId());

            return result;
        } catch (Exception e) {
            throw new WorkoutTemplateServiceException("Failed to update workout template: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteWorkoutTemplate(Long id) throws WorkoutTemplateServiceException {
        WorkoutTemplate workoutTemplate = workoutTemplateRepository.findById(id)
                .orElseThrow(() -> new WorkoutTemplateServiceException("Could not find workout template with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        workoutTemplateRepository.delete(workoutTemplate);
    }

    public List<WorkoutTemplate> getWorkoutTemplatesByUserUuid(String uuid) throws WorkoutTemplateServiceException {
        try {
            User user = userService.getUserByUuid(UUID.fromString(uuid));
            return workoutTemplateRepository.findByUser(user);
        } catch (Exception e) {
            throw new WorkoutTemplateServiceException("Failed to get workout templates by user UUID: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public WorkoutTemplateWithUserUuidDTO createWorkoutTemplateWithExerciseTemplates(WorkoutTemplateWithExerciseTemplatesDTO request) throws WorkoutTemplateServiceException {
        try {
            User user = userService.getUserByUuid(request.getWorkoutTemplate().getUserHandle());
            WorkoutTemplate workoutTemplate = modelMapper.map(request.getWorkoutTemplate(), WorkoutTemplate.class);

            workoutTemplate.setUser(user);
            WorkoutTemplate savedWorkoutTemplate = workoutTemplateRepository.save(workoutTemplate);

            for (ExerciseTemplateDTO dto : request.getExerciseTemplates()) {
                ExerciseTemplate exerciseTemplate = modelMapper.map(dto, ExerciseTemplate.class);
                exerciseTemplate.setWorkoutTemplate(savedWorkoutTemplate);
                exerciseTemplateRepository.save(exerciseTemplate);
            }

            return modelMapper.map(savedWorkoutTemplate, WorkoutTemplateWithUserUuidDTO.class);

        } catch (Exception e) {
            throw new WorkoutTemplateServiceException("Failed to create workout template with exercise templates: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

}