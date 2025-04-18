package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseDetailsServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseDetailsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseDetailsServiceTest {

    @Mock
    private ExerciseDetailsRepository exerciseDetailsRepository;

    @InjectMocks
    private ExerciseDetailsService exerciseDetailsService;

    private ExerciseDetails testExerciseDetails;
    private List<ExerciseDetails> exerciseDetailsList;

    @BeforeEach
    public void setup() {
        testExerciseDetails = new ExerciseDetails();
        testExerciseDetails.setId(1L);
        testExerciseDetails.setName("Bench Press");
        testExerciseDetails.setDescription("Chest exercise");
        testExerciseDetails.setMuscleGroup("Chest");
        testExerciseDetails.setEquipment("Barbell");
        testExerciseDetails.setDifficultyLevel("Intermediate");

        ExerciseDetails details2 = new ExerciseDetails();
        details2.setId(2L);
        details2.setName("Squat");
        details2.setDescription("Leg exercise");
        details2.setMuscleGroup("Legs");
        details2.setEquipment("Barbell");
        details2.setDifficultyLevel("Advanced");

        exerciseDetailsList = Arrays.asList(testExerciseDetails, details2);
    }

    @Test
    public void testGetAllExerciseDetails() {
        when(exerciseDetailsRepository.findAll()).thenReturn(exerciseDetailsList);

        List<ExerciseDetails> result = exerciseDetailsService.getAllExerciseDetails();

        assertEquals(2, result.size());
        assertEquals("Bench Press", result.get(0).getName());
        assertEquals("Squat", result.get(1).getName());
        verify(exerciseDetailsRepository, times(1)).findAll();
    }

    @Test
    public void testGetExerciseDetailsById_Success() throws ExerciseDetailsServiceException {
        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(testExerciseDetails));

        ExerciseDetails result = exerciseDetailsService.getExerciseDetailsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bench Press", result.getName());
        verify(exerciseDetailsRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetExerciseDetailsById_NotFound() {
        when(exerciseDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.getExerciseDetailsById(999L)
        );

        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Could not find exercise details with id: 999"));
        verify(exerciseDetailsRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetExerciseDetailsByMuscleGroup() {
        when(exerciseDetailsRepository.findByMuscleGroup("Chest")).thenReturn(Collections.singletonList(testExerciseDetails));

        List<ExerciseDetails> result = exerciseDetailsService.getExerciseDetailsByMuscleGroup("Chest");

        assertEquals(1, result.size());
        assertEquals("Chest", result.get(0).getMuscleGroup());
        verify(exerciseDetailsRepository, times(1)).findByMuscleGroup("Chest");
    }

    @Test
    public void testGetExerciseDetailsByDifficultyLevel() {
        ExerciseDetails advancedExercise = exerciseDetailsList.get(1);
        when(exerciseDetailsRepository.findByDifficultyLevel("Advanced")).thenReturn(Collections.singletonList(advancedExercise));

        List<ExerciseDetails> result = exerciseDetailsService.getExerciseDetailsByDifficultyLevel("Advanced");

        assertEquals(1, result.size());
        assertEquals("Advanced", result.get(0).getDifficultyLevel());
        verify(exerciseDetailsRepository, times(1)).findByDifficultyLevel("Advanced");
    }

    @Test
    public void testSearchExerciseDetailsByName() {
        when(exerciseDetailsRepository.findByName("Bench Press")).thenReturn(Collections.singletonList(testExerciseDetails));

        List<ExerciseDetails> result = exerciseDetailsService.searchExerciseDetailsByName("Bench Press");

        assertEquals(1, result.size());
        assertEquals("Bench Press", result.get(0).getName());
        verify(exerciseDetailsRepository, times(1)).findByName("Bench Press");
    }

    @Test
    public void testCreateExerciseDetails_Success() throws ExerciseDetailsServiceException {
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenReturn(testExerciseDetails);

        ExerciseDetails result = exerciseDetailsService.createExerciseDetails(testExerciseDetails);

        assertNotNull(result);
        assertEquals("Bench Press", result.getName());
        verify(exerciseDetailsRepository, times(1)).save(testExerciseDetails);
    }

    @Test
    public void testCreateExerciseDetails_Exception() {
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenThrow(new RuntimeException("Database error"));

        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.createExerciseDetails(testExerciseDetails)
        );

        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Failed to create exercise details"));
        verify(exerciseDetailsRepository, times(1)).save(testExerciseDetails);
    }

    @Test
    public void testUpdateExerciseDetails_Success() throws ExerciseDetailsServiceException {
        ExerciseDetails existingDetails = new ExerciseDetails();
        existingDetails.setId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setDescription("Old Description");
        existingDetails.setMuscleGroup("Old Group");
        existingDetails.setEquipment("Old Equipment");
        existingDetails.setDifficultyLevel("Beginner");

        ExerciseDetails updatedDetails = new ExerciseDetails();
        updatedDetails.setName("New Name");
        updatedDetails.setDescription("New Description");
        updatedDetails.setMuscleGroup("New Group");
        updatedDetails.setEquipment("New Equipment");
        updatedDetails.setDifficultyLevel("Advanced");

        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(existingDetails));
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExerciseDetails result = exerciseDetailsService.updateExerciseDetails(1L, updatedDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals("New Group", result.getMuscleGroup());
        assertEquals("New Equipment", result.getEquipment());
        assertEquals("Advanced", result.getDifficultyLevel());
        verify(exerciseDetailsRepository, times(1)).findById(1L);
        verify(exerciseDetailsRepository, times(1)).save(existingDetails);
    }

    @Test
    public void testUpdateExerciseDetails_NotFound() {
        when(exerciseDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.updateExerciseDetails(999L, testExerciseDetails)
        );

        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Could not find exercise details with id: 999"));
        verify(exerciseDetailsRepository, times(1)).findById(999L);
        verify(exerciseDetailsRepository, never()).save(any());
    }

    @Test
    public void testUpdateExerciseDetails_Exception() {
        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(testExerciseDetails));
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenThrow(new RuntimeException("Database error"));

        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.updateExerciseDetails(1L, testExerciseDetails)
        );

        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Failed to update exercise details"));
        verify(exerciseDetailsRepository, times(1)).findById(1L);
        verify(exerciseDetailsRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateExerciseDetails_PartialUpdate() throws ExerciseDetailsServiceException {
        ExerciseDetails existingDetails = new ExerciseDetails();
        existingDetails.setId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setDescription("Old Description");
        existingDetails.setMuscleGroup("Old Group");
        existingDetails.setEquipment("Old Equipment");
        existingDetails.setDifficultyLevel("Beginner");

        ExerciseDetails partialUpdate = new ExerciseDetails();
        partialUpdate.setName("New Name");

        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(existingDetails));
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExerciseDetails result = exerciseDetailsService.updateExerciseDetails(1L, partialUpdate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals("Old Group", result.getMuscleGroup());
        assertEquals("Old Equipment", result.getEquipment());
        assertEquals("Beginner", result.getDifficultyLevel());
        verify(exerciseDetailsRepository, times(1)).findById(1L);
        verify(exerciseDetailsRepository, times(1)).save(existingDetails);
    }

    @Test
    public void testDeleteExerciseDetails_Success() {
        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(testExerciseDetails));
        doNothing().when(exerciseDetailsRepository).delete(any(ExerciseDetails.class));

        assertDoesNotThrow(() -> exerciseDetailsService.deleteExerciseDetails(1L));

        verify(exerciseDetailsRepository, times(1)).findById(1L);
        verify(exerciseDetailsRepository, times(1)).delete(testExerciseDetails);
    }

    @Test
    public void testDeleteExerciseDetails_NotFound() {
        when(exerciseDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.deleteExerciseDetails(999L)
        );

        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Could not find exercise details with id: 999"));
        verify(exerciseDetailsRepository, times(1)).findById(999L);
        verify(exerciseDetailsRepository, never()).delete(any());
    }

    @Test
    public void testPatchExerciseDetails_Success() throws Exception {
        ExerciseDetails existingDetails = new ExerciseDetails();
        existingDetails.setId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setDescription("Old Description");
        existingDetails.setMuscleGroup("Old Group");
        existingDetails.setEquipment("Old Equipment");
        existingDetails.setDifficultyLevel("Beginner");

        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(existingDetails));
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ObjectMapper objectMapper = new ObjectMapper();
        String patchString = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"New Patched Name\"}]";
        JsonPatch patch = objectMapper.readValue(patchString, JsonPatch.class);

        ExerciseDetails result = exerciseDetailsService.patchExerciseDetails(1L, patch);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Patched Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals("Old Group", result.getMuscleGroup());
        assertEquals("Old Equipment", result.getEquipment());
        assertEquals("Beginner", result.getDifficultyLevel());

        verify(exerciseDetailsRepository, times(1)).findById(1L);
        verify(exerciseDetailsRepository, times(1)).save(any(ExerciseDetails.class));

        ArgumentCaptor<ExerciseDetails> exerciseDetailsCaptor = ArgumentCaptor.forClass(ExerciseDetails.class);
        verify(exerciseDetailsRepository).save(exerciseDetailsCaptor.capture());
        ExerciseDetails capturedDetails = exerciseDetailsCaptor.getValue();
        assertEquals("New Patched Name", capturedDetails.getName());
    }

    @Test
    public void testPatchExerciseDetails_MultipleOperations() throws Exception {
        ExerciseDetails existingDetails = new ExerciseDetails();
        existingDetails.setId(1L);
        existingDetails.setName("Old Name");
        existingDetails.setDescription("Old Description");
        existingDetails.setMuscleGroup("Old Group");
        existingDetails.setEquipment("Old Equipment");
        existingDetails.setDifficultyLevel("Beginner");

        when(exerciseDetailsRepository.findById(1L)).thenReturn(Optional.of(existingDetails));
        when(exerciseDetailsRepository.save(any(ExerciseDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ObjectMapper objectMapper = new ObjectMapper();
        String patchString = "[" +
                "{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"New Patched Name\"}," +
                "{\"op\": \"replace\", \"path\": \"/muscleGroup\", \"value\": \"New Muscle Group\"}," +
                "{\"op\": \"replace\", \"path\": \"/difficultyLevel\", \"value\": \"Advanced\"}" +
                "]";
        JsonPatch patch = objectMapper.readValue(patchString, JsonPatch.class);

        ExerciseDetails result = exerciseDetailsService.patchExerciseDetails(1L, patch);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Patched Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals("New Muscle Group", result.getMuscleGroup());
        assertEquals("Old Equipment", result.getEquipment());
        assertEquals("Advanced", result.getDifficultyLevel());
    }

    @Test
    public void testPatchExerciseDetails_NotFound() {
        when(exerciseDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        ObjectMapper objectMapper = new ObjectMapper();
        String patchString = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"New Name\"}]";
        JsonPatch patch = null;
        try {
            patch = objectMapper.readValue(patchString, JsonPatch.class);
        } catch (Exception e) {
            fail("Failed to create JSON patch: " + e.getMessage());
        }

        JsonPatch finalPatch = patch;
        ExerciseDetailsServiceException exception = assertThrows(
                ExerciseDetailsServiceException.class,
                () -> exerciseDetailsService.patchExerciseDetails(999L, finalPatch)
        );

        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Could not find exercise details with id: 999"));
        verify(exerciseDetailsRepository, times(1)).findById(999L);
        verify(exerciseDetailsRepository, never()).save(any());
    }
}