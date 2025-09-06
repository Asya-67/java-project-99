package hexlet.code;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private User testUser;
    private final String url = "/api/users";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() throws Exception {
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        mockMvc.perform(post(url).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(testUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
    }

    @Test
    void updateUser() throws Exception {
        userRepository.save(testUser);

        testUser.setFirstName("Updated");
        testUser.setEmail("updated@example.com");
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        mockMvc.perform(put(url + "/" + testUser.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isOk());

        User user = userRepository.findById(testUser.getId())
                .orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void deleteUser() throws Exception {
        userRepository.save(testUser);

        mockMvc.perform(delete(url + "/" + testUser.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
