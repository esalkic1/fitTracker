package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.UserService;
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

class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        objectMapper = new ObjectMapper();

        user1 = new User();
        user1.setId(1L);

        user2 = new User();
        user2.setId(2L);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUser_WithValidId_ShouldReturnUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/v1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void getUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(userService.getUser(999L))
                .thenThrow(new UserServiceException("User with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("User with ID 999 not found")));

        verify(userService, times(1)).getUser(999L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(userService.createUser(any(User.class))).thenThrow(
                new UserServiceException("Failed to create user: User details not found", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Failed to create user: User details not found")));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        doThrow(new UserServiceException("User with ID 999 not found", ErrorType.ENTITY_NOT_FOUND))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/v1/user/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("User with ID 999 not found")));

        verify(userService, times(1)).deleteUser(999L);
    }
}
