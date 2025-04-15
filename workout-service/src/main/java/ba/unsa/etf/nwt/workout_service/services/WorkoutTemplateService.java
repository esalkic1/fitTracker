package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutTemplateRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutTemplateService {
    private final WorkoutTemplateRepository workoutTemplateRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public WorkoutTemplateService(final WorkoutTemplateRepository workoutTemplateRepository,
                                  final UserService userService,
                                  final ModelMapper modelMapper) {
        this.workoutTemplateRepository = workoutTemplateRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
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
            User user = userService.getUserById(workoutTemplateDTO.getUserId());
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
}