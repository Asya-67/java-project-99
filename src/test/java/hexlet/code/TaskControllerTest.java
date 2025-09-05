package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.common.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ObjectMapper om;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Autowired
	private ModelGenerator modelGenerator;

	@Autowired
	private TaskMapper taskMapper;

	private Task testTask;
	private User testUser;
	private TaskStatus testTaskStatus;
	private JwtRequestPostProcessor token;

	private final String url = "/api/tasks";

	@BeforeEach
	public void setUp() {
		taskRepository.deleteAll();
		userRepository.deleteAll();
		taskStatusRepository.deleteAll();

		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
				.apply(springSecurity())
				.build();

		testUser = Instancio.of(modelGenerator.getUserModel()).create();
		userRepository.save(testUser);

		testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
		taskStatusRepository.save(testTaskStatus);

		testTask = Instancio.of(modelGenerator.getTaskModel()).create();
		testTask.setAssignee(testUser);
		testTask.setTaskStatus(testTaskStatus);

		token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
	}

	@Test
	public void testIndex() throws Exception {
		taskRepository.save(testTask);

		var response = mockMvc.perform(get(url).with(token))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();

		var body = response.getContentAsString();

		List<TaskDTO> taskDTOs = om.readValue(body, new TypeReference<List<TaskDTO>>() {});

		assertThat(taskDTOs).isNotEmpty();
		assertThat(taskDTOs.get(0).getName().get()).isEqualTo(testTask.getName());
	}

	@Test
	public void testShow() throws Exception {
		taskRepository.save(testTask);

		var response = mockMvc.perform(get(url + "/" + testTask.getId()).with(token))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();

		var body = response.getContentAsString();
		TaskDTO taskDTO = om.readValue(body, TaskDTO.class);

		assertThat(taskDTO.getName().get()).isEqualTo(testTask.getName());
		assertThat(taskDTO.getStatus().get()).isEqualTo(testTaskStatus.getSlug());
	}

	@Test
	public void testCreate() throws Exception {
		var dto = taskMapper.map(testTask);

		var request = post(url)
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(dto));

		mockMvc.perform(request)
				.andExpect(status().isCreated());

		var savedTask = taskRepository.findAll().get(0);
		assertThat(savedTask.getName()).isEqualTo(testTask.getName());
	}

	@Test
	public void testUpdate() throws Exception {
		taskRepository.save(testTask);

		TaskUpdateDTO updateDTO = new TaskUpdateDTO();
		updateDTO.setName(org.openapitools.jackson.nullable.JsonNullable.of("Updated Name"));

		var request = put(url + "/" + testTask.getId())
				.with(token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(updateDTO));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		var updatedTask = taskRepository.findById(testTask.getId()).orElseThrow();
		assertThat(updatedTask.getName()).isEqualTo(updateDTO.getName().get());
	}

	@Test
	public void testDestroy() throws Exception {
		taskRepository.save(testTask);

		mockMvc.perform(delete(url + "/" + testTask.getId()).with(token))
				.andExpect(status().isNoContent());

		assertThat(taskRepository.existsById(testTask.getId())).isFalse();
	}
}
