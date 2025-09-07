package hexlet.code;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.common.ModelGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import org.instancio.Instancio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @Value("/api/users")
    private String url;

    @Autowired
    private UserMapper userMapper;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() throws Exception {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail())); //токен для выполнения  операций над
        // определённым юзером из-под его аутентификации
    }
    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get(url).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public  void testCreateUser() throws Exception {
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(testUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + testUser.getEmail()));

        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public  void testCreateUserWithNotValidFirstName() throws Exception {
        testUser.setFirstName("");
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public  void testCreateUserWithNotValidLastName() throws Exception {
        testUser.setLastName("");
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public  void testCreateUserWithNotValidEmail() throws Exception {
        testUser.setEmail("Not Email Type");
        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShowUser() throws Exception {
        userRepository.save(testUser);
        MockHttpServletRequestBuilder request = get(url + "/{id}", testUser.getId()).with(jwt());

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testUpdateUser() throws Exception {
        userRepository.save(testUser);

        testUser.setFirstName("First Name");
        testUser.setLastName("Last Name");
        testUser.setEmail("email@email.com");

        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        MockHttpServletRequestBuilder request = put(url + "/{id}", testUser.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        User user = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + testUser.getEmail()));

        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }
    @Test
    public void testUpdateUserPartial() throws Exception {
        userRepository.save(testUser);

        testUser.setEmail("email@email.com");

        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        var request = put(url + "/{id}", testUser.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User user = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + testUser.getEmail()));

        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
    }
    @Test
    public void testUpdateUserWithNotValidFirstName() throws Exception {
        userRepository.save(testUser);

        testUser.setFirstName("");

        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        var request = put(url + "/{id}", testUser.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testUpdateUserWithNotValidLastName() throws Exception {
        userRepository.save(testUser);

        testUser.setLastName("");

        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        var request = put(url + "/{id}", testUser.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testUpdateUserWithNotValidEmail() throws Exception {
        userRepository.save(testUser);

        testUser.setEmail("Not Email Type");

        UserCreateDTO dto = userMapper.mapToCreateDTO(testUser);

        var request = put(url + "/{id}", testUser.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDestroy() throws Exception {
        userRepository.save(testUser);

        var request = delete(url + "/{id}", testUser.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
