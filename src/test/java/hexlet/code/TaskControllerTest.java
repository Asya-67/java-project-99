package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private User user;
    private TaskStatus status;
    private Label label;
    private final String url = "/api/tasks";

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        userRepository.save(user);

        status = new TaskStatus();
        status.setName("In Progress");
        status.setSlug("in-progress");
        taskStatusRepository.save(status);

        label = new Label();
        label.setName("Urgent");
        labelRepository.save(label);
    }

    @AfterEach
    public void clear() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    public void testCreateTask() throws Exception {
        String body = String.format(
                "{\"name\": \"Test Task\", \"description\": \"Description\", \"assigneeId\": %d, \"taskStatusId\": %d, \"labelIds\": [%d]}",
                user.getId(), status.getId(), label.getId()
        );

        mockMvc.perform(post(url).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        Task task = taskRepository.findAll().get(0);

        assertThat(task.getName()).isEqualTo("Test Task");
        assertThat(task.getAssignee().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("in-progress");
        assertThat(task.getLabels().iterator().next().getName()).isEqualTo("Urgent");
    }
}
