package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.workout_service.services.UserService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(1L);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_WhenExceptionOccurs_ShouldReturnBadRequest() throws Exception {
        when(userService.getAllUsers()).thenThrow(
                new UserServiceException("Failed to retrieve users", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Failed to retrieve users"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUser_WithValidId_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUser_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        when(userService.getUserById(99L)).thenThrow(
                new UserServiceException("Could not find user with id: 99", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Could not find user with id: 99"));

        verify(userService, times(1)).getUserById(99L);
    }

    @Test
    void createUser_WithValidData_ShouldCreateAndReturnUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_WhenExceptionOccurs_ShouldReturnBadRequest() throws Exception {
        when(userService.createUser(any(User.class))).thenThrow(
                new UserServiceException("Failed to create user", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Failed to create user"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        doThrow(new UserServiceException("Could not find user with id: 99", ErrorType.ENTITY_NOT_FOUND))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/v1/user/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Could not find user with id: 99"));

        verify(userService, times(1)).deleteUser(99L);
    }
}