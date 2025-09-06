package hexlet.code;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private TaskStatus testStatus;
    private final String url = "/api/task_statuses";

    @BeforeEach
    void setUp() {
        testStatus = new TaskStatus();
        testStatus.setName("In Progress");
        testStatus.setSlug("in-progress");
    }

    @AfterEach
    void tearDown() {
        taskStatusRepository.deleteAll();
    }

    @Test
    void createTaskStatus() throws Exception {
        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();
        dto.setName(testStatus.getName());
        dto.setSlug(testStatus.getSlug());

        mockMvc.perform(post(url).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isCreated());

        TaskStatus status = taskStatusRepository.findAll().get(0);
        assertThat(status.getName()).isEqualTo("In Progress");
    }

    @Test
    void updateTaskStatus() throws Exception {
        taskStatusRepository.save(testStatus);

        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();
        dto.setName("Completed");
        dto.setSlug("completed");

        mockMvc.perform(put(url + "/" + testStatus.getId()).with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isOk());

        TaskStatus status = taskStatusRepository.findById(testStatus.getId())
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found"));
        assertThat(status.getName()).isEqualTo("Completed");
    }

    @Test
    void deleteTaskStatus() throws Exception {
        taskStatusRepository.save(testStatus);

        mockMvc.perform(delete(url + "/" + testStatus.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(testStatus.getId())).isEmpty();
    }
}
