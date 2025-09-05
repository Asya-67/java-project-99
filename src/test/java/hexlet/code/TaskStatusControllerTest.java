package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.common.ModelGenerator;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskStatusRepository taskStatusRepository;

	@Autowired
	private ModelGenerator modelGenerator;

	@Autowired
	private ObjectMapper om;

	@Autowired
	private TaskStatusMapper taskStatusMapper;

	private JwtRequestPostProcessor token;

	private TaskStatus testStatus;

	private final String url = "/api/task_statuses";

	@BeforeEach
	public void setUp() {
		taskStatusRepository.deleteAll();

		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
				.apply(springSecurity())
				.build();

		testStatus = Instancio.of(modelGenerator.getStatusModel()).create();
		taskStatusRepository.save(testStatus);

		token = jwt().jwt(builder -> builder.subject("user@example.com"));
	}

	@Test
	public void testIndex() throws Exception {
		var result = mockMvc.perform(get(url).with(token))
				.andExpect(status().isOk())
				.andReturn();
		var body = result.getResponse().getContentAsString();

		List<TaskStatusDTO> taskStatuses = om.readValue(body, new TypeReference<>() {});
		Assertions.assertThat(taskStatuses)
				.extracting(dto -> dto.getName().get())
				.contains(testStatus.getName());
	}

	@Test
	public void testShow() throws Exception {
		var result = mockMvc.perform(get(url + "/" + testStatus.getId()).with(token))
				.andExpect(status().isOk())
				.andReturn();
		var body = result.getResponse().getContentAsString();

		TaskStatusDTO dto = om.readValue(body, TaskStatusDTO.class);
		Assertions.assertThat(dto.getName().get()).isEqualTo(testStatus.getName());
		Assertions.assertThat(dto.getSlug().get()).isEqualTo(testStatus.getSlug());
	}

	@Test
	public void testCreate() throws Exception {
		var dto = taskStatusMapper.mapToCreateDTO(testStatus);

		var request = post(url)
				.with(token)
				.contentType("application/json")
				.content(om.writeValueAsString(dto));

		mockMvc.perform(request)
				.andExpect(status().isCreated());

		var saved = taskStatusRepository.findAll().get(0);
		Assertions.assertThat(saved.getName()).isEqualTo(testStatus.getName());
	}

	@Test
	public void testUpdate() throws Exception {
		var updateDTO = new hexlet.code.dto.taskStatus.TaskStatusUpdateDTO();
		updateDTO.setName(org.openapitools.jackson.nullable.JsonNullable.of("Updated Name"));

		var request = put(url + "/" + testStatus.getId())
				.with(token)
				.contentType("application/json")
				.content(om.writeValueAsString(updateDTO));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		var updated = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
		Assertions.assertThat(updated.getName()).isEqualTo("Updated Name");
	}

	@Test
	public void testDelete() throws Exception {
		var request = delete(url + "/" + testStatus.getId()).with(token);

		mockMvc.perform(request)
				.andExpect(status().isNoContent());

		Assertions.assertThat(taskStatusRepository.existsById(testStatus.getId())).isFalse();
	}
}
