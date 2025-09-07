package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.common.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Set;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Task testTask;
    private User testUser;
    private TaskStatus testTaskStatus;
    private Label testLabel;

    @Value("/api/tasks")
    private String url;

    @Autowired
    private TaskMapper taskMapper;


    @BeforeEach
    public void setUp() throws Exception {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        testTask.setLabels(Set.of(testLabel));

        taskRepository.save(testTask);

    }
    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    private Label generatedTestLabel() {
        var testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        return labelRepository.findByName(testLabel.getName()).orElse(null);
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
    public void testIndexWithTitleContains() throws Exception {
        var testTaskTitle = testTask.getName();
        var result = mockMvc.perform(get(url + "?titleCont=" + testTaskTitle).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().contains(testTaskTitle))
        );
    }
    @Test
    public void testIndexWithAssigneeId() throws Exception {
        var testAssigneeId = testTask.getAssignee().getId();
        var result = mockMvc.perform(get(url + "?assigneeId=" + testAssigneeId).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(testAssigneeId))
        );
    }

    @Test
    public void testIndexWithStatus() throws Exception {
        var testStatus = testTask.getTaskStatus().getSlug();
        var result = mockMvc.perform(get(url + "?status=" + testStatus).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").asString().contains(testStatus))
        );
    }

    @Test
    public void testIndexWithLabelId() throws Exception {
        Long testLabelId = testTask.getLabels()
                .stream()
                .map(Label::getId)
                .findFirst()
                .orElse(1L);
        var result = mockMvc.perform(get(url + "?labelId=" + testLabelId).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("taskLabelIds").isArray().contains(testLabelId))
        );
    }

    @Test
    public void testIndexWithFullFilters() throws Exception {
        var textTaskTitle = testTask.getName();
        var testAssigneeId = testTask.getAssignee().getId();
        var testStatus = testTask.getTaskStatus().getSlug();
        Long testLabelId = testTask.getLabels()
                .stream()
                .map(Label::getId)
                .findFirst()
                .orElse(1L);

        var result = mockMvc.perform(get(url
                        + "?titleCont=" + textTaskTitle
                        + "&assigneeId=" + testAssigneeId
                        + "&status=" + testStatus
                        + "&labelId=" + testLabelId).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().contains(textTaskTitle))
        );
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(testAssigneeId))
        );
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").asString().contains(testStatus))
        );
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("taskLabelIds").isArray().contains(testLabelId))
        );
    }

    @Test
    public  void testCreateTask() throws Exception {
        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Task task = taskRepository.findById(testTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with name: " + testTask.getName()));

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTask.getTaskStatus().getSlug());
        assertThat(task.getAssignee().getFirstName()).isEqualTo(testTask.getAssignee().getFirstName());
        assertThat(task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()))
                .isEqualTo(testTask.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
    }
    @Test
    public  void testCreateTaskWithNotValidStatus() throws Exception {
        testTask.setTaskStatus(null);
        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public  void testCreateTaskWithNotValidName() throws Exception {
        testTask.setName("");
        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        MockHttpServletRequestBuilder request = post(url).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShowTask() throws Exception {
        taskRepository.save(testTask);

        MockHttpServletRequestBuilder request = get(url + "/{id}", testTask.getId()).with(jwt());

        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("taskLabelIds").isEqualTo(testTask.getLabels().stream()
                        .map(Label::getId).collect(Collectors.toSet()))
        );
    }

    @Test
    public void testUpdateTask() throws Exception {
        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        TaskStatus testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(Set.of(generatedTestLabel()));

        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        var request = put(url + "/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Task task = taskRepository.findById(testTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with name: " + testTask.getName()));

        assertThat(task.getName()).isEqualTo((dto.getName()));
        assertThat(task.getDescription()).isEqualTo((dto.getDescription()));
        assertThat(task.getIndex()).isEqualTo((dto.getIndex()));
        assertThat(task.getAssignee().getId()).isEqualTo((dto.getAssigneeId()));
        assertThat(task.getTaskStatus().getSlug()).isEqualTo((dto.getStatus()));
        assertThat(task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()))
                .isEqualTo((dto.getTaskLabelIds()));
    }

    @Test
    public void testUpdateTaskPartial() throws Exception {
        User testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        TaskStatus testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);

        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        var request = put(url + "/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Task task = taskRepository.findById(testTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with name: " + testTask.getName()));

        assertThat(task.getName()).isEqualTo((testTask.getName()));
        assertThat(task.getDescription()).isEqualTo((testTask.getDescription()));
        assertThat(task.getIndex()).isEqualTo((testTask.getIndex()));
        assertThat(task.getAssignee().getId()).isEqualTo((testTask.getAssignee().getId()));
        assertThat(task.getTaskStatus().getSlug()).isEqualTo((testTask.getTaskStatus().getSlug()));
        assertThat(task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()))
                .isEqualTo((testTask.getLabels().stream().map(Label::getId).collect(Collectors.toSet())));

    }

    @Test
    public void testUpdateTaskWithNotValidName() throws Exception {
        testTask.setName("");

        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);

        var request = put(url + "/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testUpdateTaskWithNotValidStatus() throws Exception {
        TaskCreateDTO dto = taskMapper.mapToCreateDTO(testTask);
        dto.setStatus("Not Present Task Status");

        var request = put(url + "/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete(url + "/{id}", testTask.getId()).with(jwt());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isNotPresent();
    }
}
