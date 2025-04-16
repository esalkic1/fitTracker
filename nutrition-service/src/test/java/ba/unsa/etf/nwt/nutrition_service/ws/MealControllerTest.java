package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.MealService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MealControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private MealService mealService;

    @InjectMocks
    private MealController mealController;

    private Meal meal1;
    private Meal meal2;
    private MealDTO mealDTO;
    private MealDTO updatedMealDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(mealController)
                .build();

        objectMapper = new ObjectMapper();

        meal1 = new Meal();
        meal1.setId(1L);
        meal1.setName("Breakfast");

        meal2 = new Meal();
        meal2.setId(2L);
        meal2.setName("Lunch");

        mealDTO = new MealDTO();
        mealDTO.setId(1L);
        mealDTO.setName("Dinner");
        mealDTO.setUserId(1L);

        updatedMealDTO = new MealDTO();
        updatedMealDTO.setId(2L);
        updatedMealDTO.setName("Snack");
        updatedMealDTO.setUserId(2L);
    }

    @Test
    void getAllMeals_ShouldReturnMealList() throws Exception {
        Mockito.when(mealService.getAllMeals()).thenReturn(Arrays.asList(meal1, meal2));

        mockMvc.perform(get("/api/v1/meal").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Breakfast")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Lunch")));

        verify(mealService, times(1)).getAllMeals();
    }

    @Test
    void getMeal_WithValidId_ShouldReturnMeal() throws Exception {
        Mockito.when(mealService.getMeal(1L)).thenReturn(meal1);

        mockMvc.perform(get("/api/v1/meal/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Breakfast")));

        verify(mealService, times(1)).getMeal(1L);
    }

    @Test
    void getMeal_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(mealService.getMeal(999L))
                .thenThrow(new MealServiceException("Meal with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/meal/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Meal with ID 999 not found")));

        verify(mealService, times(1)).getMeal(999L);
    }

    @Test
    void searchMeals_WithValidName_ShouldReturnMatchingMeals() throws Exception {
        Mockito.when(mealService.searchMealsByName("break"))
                .thenReturn(List.of(meal1));

        mockMvc.perform(get("/api/v1/meal/search")
                        .param("name", "break")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Breakfast")));

        verify(mealService, times(1)).searchMealsByName("break");
    }

    @Test
    void searchMeals_WithNonExistingName_ShouldReturnEmptyList() throws Exception {
        Mockito.when(mealService.searchMealsByName("nonexistent"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/meal/search")
                        .param("name", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(mealService, times(1)).searchMealsByName("nonexistent");
    }

    @Test
    void createMeal_WithValidData_ShouldReturnCreatedMeal() throws Exception {
        Mockito.when(mealService.createMeal(any(MealDTO.class))).thenReturn(mealDTO);

        mockMvc.perform(post("/api/v1/meal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mealDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dinner")))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(mealService, times(1)).createMeal(any(MealDTO.class));
    }

    @Test
    void createMeal_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(mealService.createMeal(any(MealDTO.class))).thenThrow(
                new MealServiceException("Failed to create meal: Meal details not found", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(post("/api/v1/meal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mealDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Failed to create meal: Meal details not found")));

        verify(mealService, times(1)).createMeal(any(MealDTO.class));
    }

    @Test
    void updateMeal_WithValidData_ShouldReturnUpdatedMeal() throws Exception {
        when(mealService.updateMeal(eq(2L), any(MealDTO.class))).thenReturn(updatedMealDTO);

        mockMvc.perform(put("/api/v1/meal/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMealDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Snack")))
                .andExpect(jsonPath("$.userId", is(2)));

        verify(mealService, times(1)).updateMeal(eq(2L), any(MealDTO.class));
    }

    @Test
    void updateMeal_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(mealService.updateMeal(eq(999L), any(MealDTO.class))).thenThrow(
                new MealServiceException("Meal with ID 999 not found", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(put("/api/v1/meal/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMealDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Meal with ID 999 not found")));

        verify(mealService, times(1)).updateMeal(eq(999L), any(MealDTO.class));
    }

    @Test
    void deleteMeal_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(mealService).deleteMeal(1L);

        mockMvc.perform(delete("/api/v1/meal/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(mealService, times(1)).deleteMeal(1L);
    }

    @Test
    void deleteMeal_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        doThrow(new MealServiceException("Meal with ID 999 not found", ErrorType.ENTITY_NOT_FOUND))
                .when(mealService).deleteMeal(999L);

        mockMvc.perform(delete("/api/v1/meal/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Meal with ID 999 not found")));

        verify(mealService, times(1)).deleteMeal(999L);
    }
}