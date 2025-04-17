package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.FoodService;
import ba.unsa.etf.nwt.nutrition_service.validators.FoodValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FoodControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FoodService foodService;

    @Mock
    private FoodValidator foodValidator;

    @InjectMocks
    private FoodController foodController;

    private Food food1;
    private Food food2;
    private FoodDTO foodDTO;
    private FoodDTO updatedFoodDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(foodController)
                .build();

        objectMapper = new ObjectMapper();

        food1 = new Food();
        food1.setId(1L);
        food1.setName("Pancakes");
        food1.setCalories(200.0);

        food2 = new Food();
        food2.setId(2L);
        food2.setName("Rice");
        food2.setCalories(100.0);

        foodDTO = new FoodDTO();
        foodDTO.setId(1L);
        foodDTO.setName("Salad");
        foodDTO.setCalories(150.0);
        foodDTO.setMealId(1L);

        updatedFoodDTO = new FoodDTO();
        updatedFoodDTO.setId(2L);
        updatedFoodDTO.setName("Apple");
        updatedFoodDTO.setCalories(50.0);
        updatedFoodDTO.setMealId(1L);
    }

    @Test
    void getAllFoods_ShouldReturnFoodList() throws Exception {
        when(foodService.getAllFoods()).thenReturn(Arrays.asList(food1, food2));

        mockMvc.perform(get("/api/v1/food").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Pancakes")))
                .andExpect(jsonPath("$[0].calories", is(200.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Rice")))
                .andExpect(jsonPath("$[1].calories", is(100.)));

        verify(foodService, times(1)).getAllFoods();
    }

    @Test
    void getFood_WithValidId_ShouldReturnFood() throws Exception {
        when(foodService.getFood(1L)).thenReturn(food1);

        mockMvc.perform(get("/api/v1/food/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Pancakes")))
                .andExpect(jsonPath("$.calories", is(200.0)));

        verify(foodService, times(1)).getFood(1L);
    }

    @Test
    void getFood_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(foodService.getFood(999L))
                .thenThrow(new FoodServiceException("Food with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/food/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Food with ID 999 not found")));

        verify(foodService, times(1)).getFood(999L);
    }

    @Test
    void searchFoodsByName_WithValidName_ShouldReturnMatchingFoods() throws Exception {
        when(foodService.searchFoodsByName("pancakes"))
                .thenReturn(Arrays.asList(food1));

        mockMvc.perform(get("/api/v1/food/search-by-name")
                        .param("name", "pancakes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Pancakes")))
                .andExpect(jsonPath("$[0].calories", is(200.0)));

        verify(foodService, times(1)).searchFoodsByName("pancakes");
    }

    @Test
    void searchFoodsByName_WithNonExistingName_ShouldReturnEmptyList() throws Exception {
        when(foodService.searchFoodsByName("nonexistent"))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/food/search-by-name")
                        .param("name", "nonexistent").
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(foodService, times(1)).searchFoodsByName("nonexistent");
    }

    @Test
    void searchFoodsByCalories_WithValidRange_ShouldReturnMatchingFoods() throws Exception {
        when(foodService.searchFoodsByCalorieRange(50, 250))
                .thenReturn(Arrays.asList(food1, food2));

        mockMvc.perform(get("/api/v1/food/search-by-calories")
                        .param("min", "50")
                        .param("max", "250")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Pancakes")))
                .andExpect(jsonPath("$[1].name", is("Rice")));

        verify(foodService, times(1)).searchFoodsByCalorieRange(50, 250);
    }

    @Test
    void searchFoodsByCalories_WithInvalidRange_ShouldReturnEmptyList() throws Exception {
        when(foodService.searchFoodsByCalorieRange(100, 50))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/food/search-by-calories")
                        .param("min", "100")
                        .param("max", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(foodService, times(1)).searchFoodsByCalorieRange(100, 50);
    }

    @Test
    void createFood_WithValidData_ShouldReturnCreatedFood() throws Exception {
        FoodDTO responseDTO = new FoodDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Salad");
        responseDTO.setCalories(150.0);
        responseDTO.setMealId(1L);

        when(foodService.createFood(any(FoodDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/food")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Salad")))
                .andExpect(jsonPath("$.calories", is(150.0)));

        verify(foodService, times(1)).createFood(any(FoodDTO.class));
    }

    @Test
    void createFood_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(foodService.createFood(any(FoodDTO.class))).thenThrow(
                new FoodServiceException("Failed to create food: Food details not found", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(post("/api/v1/food")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Failed to create food: Food details not found")));

        verify(foodService, times(1)).createFood(any(FoodDTO.class));
    }

    @Test
    void updateFood_WithValidData_ShouldReturnUpdatedFood() throws Exception {
        FoodDTO responseDTO = new FoodDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Green Apple");
        responseDTO.setCalories(150.0);
        responseDTO.setMealId(2L);

        when(foodService.updateFood(eq(1L), any(FoodDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/food/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFoodDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Green Apple")))
                .andExpect(jsonPath("$.calories", is(150.0)))
                .andExpect(jsonPath("$.mealId", is(2)));

        verify(foodService, times(1)).updateFood(eq(1L), any(FoodDTO.class));
    }

    @Test
    void updateFood_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(foodService.updateFood(eq(999L), any(FoodDTO.class))).thenThrow(
                new FoodServiceException("Food with ID 999 not found", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(put("/api/v1/food/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFoodDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Food with ID 999 not found")));

        verify(foodService, times(1)).updateFood(eq(999L), any(FoodDTO.class));
    }

    @Test
    void patchFood_WithValidUpdates_ShouldReturnUpdatedFood() throws Exception {
        FoodDTO responseDTO = new FoodDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Organic Apple");
        responseDTO.setCalories(100.0);
        responseDTO.setMealId(2L);

        when(foodService.patchFood(eq(1L), anyMap())).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/food/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Organic Apple")))
                .andExpect(jsonPath("$.calories", is(100.0)))
                .andExpect(jsonPath("$.mealId", is(2)));

        verify(foodService, times(1)).patchFood(eq(1L), anyMap());
    }

    @Test
    void patchFood_WithInvalidUpdates_ShouldReturnBadRequest() throws Exception {
        when(foodService.patchFood(eq(999L), anyMap())).thenThrow(
                new FoodServiceException("Food with ID 999 not found", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(patch("/api/v1/food/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFoodDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Food with ID 999 not found")));

        verify(foodService, times(1)).patchFood(eq(999L), anyMap());
    }

    @Test
    void deleteFood_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(foodService).deleteFood(1L);

        mockMvc.perform(delete("/api/v1/food/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(foodService, times(1)).deleteFood(1L);
    }

    @Test
    void deleteFood_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        doThrow(new FoodServiceException("Food with ID 999 not found", ErrorType.ENTITY_NOT_FOUND))
                .when(foodService).deleteFood(999L);

        mockMvc.perform(delete("/api/v1/food/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Food with ID 999 not found")));

        verify(foodService, times(1)).deleteFood(999L);
    }
}
