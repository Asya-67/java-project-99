package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.users.CreateUserDTO;
import hexlet.code.dto.users.UpdateUserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtEncoder jwtEncoder;

    private String token;
    private final String url = "/api/users";

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPasswordDigest("hashed");
        testUser = userRepository.save(testUser);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(testUser.getEmail())
                .build();
        token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUserValid() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("newuser@example.com");
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setPassword("password");

        MockHttpServletRequestBuilder request = post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail("newuser@example.com")).isPresent();
    }

    // Валидация: пустой email
    @Test
    void testCreateUserInvalidEmailEmpty() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("");
        dto.setPassword("12345");

        MockHttpServletRequestBuilder request = post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    // Валидация: неверный формат email
    @Test
    void testCreateUserInvalidEmailFormat() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("not-an-email");
        dto.setPassword("12345");

        MockHttpServletRequestBuilder request = post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    // Валидация: пустой пароль
    @Test
    void testCreateUserEmptyPassword() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("");

        MockHttpServletRequestBuilder request = post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    // Валидация: слишком короткий пароль
    @Test
    void testCreateUserShortPassword() throws Exception {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("12");

        MockHttpServletRequestBuilder request = post(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    // Частичное обновление через UpdateUserDTO
    @Test
    void testUpdateUserPartial() throws Exception {
        UpdateUserDTO updateDto = new UpdateUserDTO();
        updateDto.setEmail(JsonNullable.of("updated@example.com"));

        MockHttpServletRequestBuilder request = put(url + "/" + testUser.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    // Удаление пользователя
    @Test
    void testDeleteUser() throws Exception {
        MockHttpServletRequestBuilder request = delete(url + "/" + testUser.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
