package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseMetadataDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseMetadataServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseMetadataRepository;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseMetadataServiceTest {

    @Mock
    private ExerciseMetadataRepository exerciseMetadataRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExerciseMetadataService exerciseMetadataService;

    private Exercise exercise;
    private ExerciseMetadata exerciseMetadata;
    private ExerciseMetadataDTO exerciseMetadataDTO;

    @BeforeEach
    void setUp() {
        exercise = new Exercise();
        exercise.setId(1L);

        exerciseMetadata = new ExerciseMetadata();
        exerciseMetadata.setId(1L);
        exerciseMetadata.setExercise(exercise);
        exerciseMetadata.setAdditionalNotes("Keep your back straight");

        exerciseMetadataDTO = new ExerciseMetadataDTO();
        exerciseMetadataDTO.setExerciseId(1L);
        exerciseMetadataDTO.setAdditionalNotes("Keep your back straight");
    }

    @Test
    void getAllExerciseMetadata_ShouldReturnAllMetadata() {
        List<ExerciseMetadata> expectedMetadata = Arrays.asList(exerciseMetadata);
        when(exerciseMetadataRepository.findAll()).thenReturn(expectedMetadata);

        List<ExerciseMetadata> result = exerciseMetadataService.getAllExerciseMetadata();

        assertEquals(expectedMetadata, result);
        verify(exerciseMetadataRepository, times(1)).findAll();
    }

    @Test
    void getExerciseMetadataById_WhenMetadataExists_ShouldReturnMetadata() throws ExerciseMetadataServiceException {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.of(exerciseMetadata));

        ExerciseMetadata result = exerciseMetadataService.getExerciseMetadataById(1L);

        assertEquals(exerciseMetadata, result);
        verify(exerciseMetadataRepository, times(1)).findById(1L);
    }

    @Test
    void getExerciseMetadataById_WhenMetadataDoesNotExist_ShouldThrowException() {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.empty());

        ExerciseMetadataServiceException exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.getExerciseMetadataById(1L)
        );
        assertEquals("Could not find exercise metadata with id: 1", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void getExerciseMetadataByExerciseId_WhenExerciseAndMetadataExist_ShouldReturnMetadata() throws ExerciseMetadataServiceException {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseMetadataRepository.findByExerciseId(1L)).thenReturn(Optional.of(exerciseMetadata));

        ExerciseMetadata result = exerciseMetadataService.getExerciseMetadataByExerciseId(1L);

        assertEquals(exerciseMetadata, result);
        verify(exerciseRepository, times(1)).findById(1L);
        verify(exerciseMetadataRepository, times(1)).findByExerciseId(1L);
    }

    @Test
    void getExerciseMetadataByExerciseId_WhenExerciseDoesNotExist_ShouldThrowException() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        ExerciseMetadataServiceException exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.getExerciseMetadataByExerciseId(1L)
        );
        assertEquals("Could not find exercise with id: 1", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void getExerciseMetadataByExerciseId_WhenExerciseExistsButNoMetadata_ShouldThrowException() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseMetadataRepository.findByExerciseId(1L)).thenReturn(Optional.empty());

        ExerciseMetadataServiceException exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.getExerciseMetadataByExerciseId(1L)
        );
        assertEquals("No metadata found for exercise with id: 1", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void createExerciseMetadata_WhenValidData_ShouldCreateAndReturnDTO() throws ExerciseMetadataServiceException {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseMetadataRepository.findByExerciseId(1L)).thenReturn(Optional.empty());
        when(modelMapper.map(exerciseMetadataDTO, ExerciseMetadata.class)).thenReturn(exerciseMetadata);
        when(exerciseMetadataRepository.save(any(ExerciseMetadata.class))).thenReturn(exerciseMetadata);
        when(modelMapper.map(exerciseMetadata, ExerciseMetadataDTO.class)).thenReturn(exerciseMetadataDTO);

        ExerciseMetadataDTO result = exerciseMetadataService.createExerciseMetadata(exerciseMetadataDTO);

        assertEquals(exerciseMetadataDTO, result);
        verify(exerciseRepository, times(1)).findById(1L);
        verify(exerciseMetadataRepository, times(1)).findByExerciseId(1L);
        verify(exerciseMetadataRepository, times(1)).save(exerciseMetadata);
    }

    @Test
    void createExerciseMetadata_WhenExerciseDoesNotExist_ShouldThrowException() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.createExerciseMetadata(exerciseMetadataDTO)
        );
    }

    @Test
    void createExerciseMetadata_WhenMetadataAlreadyExists_ShouldThrowException() {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseMetadataRepository.findByExerciseId(1L)).thenReturn(Optional.of(exerciseMetadata));

        Exception exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.createExerciseMetadata(exerciseMetadataDTO)
        );
        assertTrue(exception.getMessage().contains("Failed to create exercise metadata"));
    }

    @Test
    void updateExerciseMetadata_WhenValidData_ShouldUpdateAndReturnDTO() throws ExerciseMetadataServiceException {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.of(exerciseMetadata));
        when(exerciseMetadataRepository.save(any(ExerciseMetadata.class))).thenReturn(exerciseMetadata);
        when(modelMapper.map(exerciseMetadata, ExerciseMetadataDTO.class)).thenReturn(exerciseMetadataDTO);

        ExerciseMetadataDTO result = exerciseMetadataService.updateExerciseMetadata(1L, exerciseMetadataDTO);

        assertEquals(exerciseMetadataDTO, result);
        verify(exerciseMetadataRepository, times(1)).findById(1L);
        verify(exerciseMetadataRepository, times(1)).save(exerciseMetadata);
    }

    @Test
    void updateExerciseMetadata_WhenChangingExercise_ShouldUpdateExerciseAndReturnDTO() throws ExerciseMetadataServiceException {
        Exercise newExercise = new Exercise();
        newExercise.setId(2L);

        ExerciseMetadataDTO updateDTO = new ExerciseMetadataDTO();
        updateDTO.setExerciseId(2L);
        updateDTO.setAdditionalNotes("New notes");

        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.of(exerciseMetadata));
        when(exerciseRepository.findById(2L)).thenReturn(Optional.of(newExercise));
        when(exerciseMetadataRepository.findByExerciseId(2L)).thenReturn(Optional.empty());
        when(exerciseMetadataRepository.save(any(ExerciseMetadata.class))).thenReturn(exerciseMetadata);
        when(modelMapper.map(exerciseMetadata, ExerciseMetadataDTO.class)).thenReturn(updateDTO);

        ExerciseMetadataDTO result = exerciseMetadataService.updateExerciseMetadata(1L, updateDTO);

        assertEquals(updateDTO, result);
        verify(exerciseMetadataRepository, times(1)).findById(1L);
        verify(exerciseRepository, times(1)).findById(2L);
        verify(exerciseMetadataRepository, times(1)).findByExerciseId(2L);
        verify(exerciseMetadataRepository, times(1)).save(exerciseMetadata);
    }

    @Test
    void updateExerciseMetadata_WhenMetadataDoesNotExist_ShouldThrowException() {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.empty());

        ExerciseMetadataServiceException exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.updateExerciseMetadata(1L, exerciseMetadataDTO)
        );
        assertEquals("Could not find exercise metadata with id: 1", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
    }

    @Test
    void deleteExerciseMetadata_WhenMetadataExists_ShouldDelete() throws ExerciseMetadataServiceException {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.of(exerciseMetadata));
        doNothing().when(exerciseMetadataRepository).delete(exerciseMetadata);

        exerciseMetadataService.deleteExerciseMetadata(1L);

        verify(exerciseMetadataRepository, times(1)).findById(1L);
        verify(exerciseMetadataRepository, times(1)).delete(exerciseMetadata);
    }

    @Test
    void deleteExerciseMetadata_WhenMetadataDoesNotExist_ShouldThrowException() {
        when(exerciseMetadataRepository.findById(1L)).thenReturn(Optional.empty());

        ExerciseMetadataServiceException exception = assertThrows(
                ExerciseMetadataServiceException.class,
                () -> exerciseMetadataService.deleteExerciseMetadata(1L)
        );

        assertEquals("Could not find exercise metadata with id: 1", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
    }
}