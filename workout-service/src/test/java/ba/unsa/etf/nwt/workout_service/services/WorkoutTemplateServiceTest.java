package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutTemplateServiceTest {

    @Mock
    private WorkoutTemplateRepository workoutTemplateRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WorkoutTemplateService workoutTemplateService;

    private User testUser;
    private WorkoutTemplate testWorkoutTemplate;
    private WorkoutTemplateDTO testWorkoutTemplateDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testWorkoutTemplate = new WorkoutTemplate();
        testWorkoutTemplate.setId(1L);
        testWorkoutTemplate.setName("Test Template");
        testWorkoutTemplate.setDescription("Test Description");
        testWorkoutTemplate.setUser(testUser);

        testWorkoutTemplateDTO = new WorkoutTemplateDTO();
        testWorkoutTemplateDTO.setName("Test Template");
        testWorkoutTemplateDTO.setDescription("Test Description");
        testWorkoutTemplateDTO.setUserId(1L);
    }

    @Test
    void getAllWorkoutTemplates_ShouldReturnAllTemplates() {
        List<WorkoutTemplate> templates = Arrays.asList(testWorkoutTemplate);
        when(workoutTemplateRepository.findAll()).thenReturn(templates);

        List<WorkoutTemplate> result = workoutTemplateService.getAllWorkoutTemplates();

        assertEquals(templates, result);
        verify(workoutTemplateRepository).findAll();
    }

    @Test
    void getWorkoutTemplatesByUserId_WhenUserExists_ShouldReturnUserTemplates() throws WorkoutTemplateServiceException, UserServiceException {
        List<WorkoutTemplate> templates = Arrays.asList(testWorkoutTemplate);
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(workoutTemplateRepository.findByUser(any(User.class))).thenReturn(templates);

        List<WorkoutTemplate> result = workoutTemplateService.getWorkoutTemplatesByUserId(1L);

        assertEquals(templates, result);
        verify(userService).getUserById(1L);
        verify(workoutTemplateRepository).findByUser(testUser);
    }

    @Test
    void getWorkoutTemplatesByUserId_WhenUserDoesNotExist_ShouldThrowException() throws UserServiceException {
        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.getWorkoutTemplatesByUserId(1L);
        });
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService).getUserById(1L);
        verifyNoInteractions(workoutTemplateRepository);
    }

    @Test
    void getWorkoutTemplateById_WhenTemplateExists_ShouldReturnTemplate() throws WorkoutTemplateServiceException {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.of(testWorkoutTemplate));

        WorkoutTemplate result = workoutTemplateService.getWorkoutTemplateById(1L);

        assertEquals(testWorkoutTemplate, result);
        verify(workoutTemplateRepository).findById(1L);
    }

    @Test
    void getWorkoutTemplateById_WhenTemplateDoesNotExist_ShouldThrowException() {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.getWorkoutTemplateById(1L);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutTemplateRepository).findById(1L);
    }

    @Test
    void createWorkoutTemplate_WhenUserExists_ShouldCreateAndReturnTemplate() throws WorkoutTemplateServiceException, UserServiceException {
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(modelMapper.map(any(WorkoutTemplateDTO.class), eq(WorkoutTemplate.class))).thenReturn(testWorkoutTemplate);
        when(workoutTemplateRepository.save(any(WorkoutTemplate.class))).thenReturn(testWorkoutTemplate);
        when(modelMapper.map(any(WorkoutTemplate.class), eq(WorkoutTemplateDTO.class))).thenReturn(testWorkoutTemplateDTO);

        WorkoutTemplateDTO result = workoutTemplateService.createWorkoutTemplate(testWorkoutTemplateDTO);

        assertNotNull(result);
        assertEquals(testWorkoutTemplateDTO, result);
        verify(userService).getUserById(testWorkoutTemplateDTO.getUserId());
        verify(modelMapper).map(testWorkoutTemplateDTO, WorkoutTemplate.class);
        verify(workoutTemplateRepository).save(testWorkoutTemplate);
        verify(modelMapper).map(testWorkoutTemplate, WorkoutTemplateDTO.class);
    }

    @Test
    void createWorkoutTemplate_WhenUserDoesNotExist_ShouldThrowException() throws UserServiceException {
        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.createWorkoutTemplate(testWorkoutTemplateDTO);
        });
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService).getUserById(testWorkoutTemplateDTO.getUserId());
        verifyNoInteractions(workoutTemplateRepository);
    }

    @Test
    void updateWorkoutTemplate_WhenTemplateAndUserExist_ShouldUpdateAndReturnTemplate() throws WorkoutTemplateServiceException, UserServiceException {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.of(testWorkoutTemplate));
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(workoutTemplateRepository.save(any(WorkoutTemplate.class))).thenReturn(testWorkoutTemplate);

        doAnswer(invocation -> {
            WorkoutTemplate template = invocation.getArgument(1);
            template.setName(testWorkoutTemplateDTO.getName());
            template.setDescription(testWorkoutTemplateDTO.getDescription());
            return null;
        }).when(modelMapper).map(any(WorkoutTemplateDTO.class), any(WorkoutTemplate.class));

        doReturn(testWorkoutTemplateDTO).when(modelMapper).map(any(WorkoutTemplate.class), eq(WorkoutTemplateDTO.class));

        WorkoutTemplateDTO result = workoutTemplateService.updateWorkoutTemplate(1L, testWorkoutTemplateDTO);

        assertNotNull(result);
        assertEquals(testWorkoutTemplateDTO, result);
        verify(workoutTemplateRepository).findById(1L);
        verify(userService).getUserById(testWorkoutTemplateDTO.getUserId());
        verify(workoutTemplateRepository).save(testWorkoutTemplate);
        verify(modelMapper).map(testWorkoutTemplate, WorkoutTemplateDTO.class);
    }

    @Test
    void updateWorkoutTemplate_WhenTemplateDoesNotExist_ShouldThrowException() {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.updateWorkoutTemplate(1L, testWorkoutTemplateDTO);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutTemplateRepository).findById(1L);
        verifyNoMoreInteractions(userService, workoutTemplateRepository);
    }

    @Test
    void updateWorkoutTemplate_WhenUserDoesNotExist_ShouldThrowException() throws UserServiceException {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.of(testWorkoutTemplate));
        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.updateWorkoutTemplate(1L, testWorkoutTemplateDTO);
        });
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(workoutTemplateRepository).findById(1L);
        verify(userService).getUserById(testWorkoutTemplateDTO.getUserId());
        verifyNoMoreInteractions(workoutTemplateRepository);
    }

    @Test
    void deleteWorkoutTemplate_WhenTemplateExists_ShouldDeleteTemplate() throws WorkoutTemplateServiceException {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.of(testWorkoutTemplate));
        doNothing().when(workoutTemplateRepository).delete(any(WorkoutTemplate.class));

        workoutTemplateService.deleteWorkoutTemplate(1L);

        verify(workoutTemplateRepository).findById(1L);
        verify(workoutTemplateRepository).delete(testWorkoutTemplate);
    }

    @Test
    void deleteWorkoutTemplate_WhenTemplateDoesNotExist_ShouldThrowException() {
        when(workoutTemplateRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutTemplateServiceException exception = assertThrows(WorkoutTemplateServiceException.class, () -> {
            workoutTemplateService.deleteWorkoutTemplate(1L);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutTemplateRepository).findById(1L);
        verifyNoMoreInteractions(workoutTemplateRepository);
    }
}