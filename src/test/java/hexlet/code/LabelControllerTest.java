package hexlet.code;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    private final String url = "/api/labels";

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        labelRepository.deleteAll();
    }

    @Test
    void testCreateLabel() throws Exception {
        LabelCreateDTO dto = new LabelCreateDTO();
        dto.setName("Urgent");

        mockMvc.perform(post(url).with(jwt())
                        .contentType(TestUtils.contentTypeJson())
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isCreated());

        Label saved = labelRepository.findAll().get(0);
        assertThat(saved.getName()).isEqualTo("Urgent");
    }

    @Test
    void testUpdateLabel() throws Exception {
        Label label = new Label();
        label.setName("Old Label");
        labelRepository.save(label);

        LabelCreateDTO dto = new LabelCreateDTO();
        dto.setName("Updated Label");

        mockMvc.perform(put(url + "/" + label.getId()).with(jwt())
                        .contentType(TestUtils.contentTypeJson())
                        .content(TestUtils.toJson(dto)))
                .andExpect(status().isOk());

        Label updated = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Label");
    }

    @Test
    void testDestroyLabel() throws Exception {
        Label label = new Label();
        label.setName("Delete Label");
        labelRepository.save(label);

        mockMvc.perform(delete(url + "/" + label.getId()).with(jwt()))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(label.getId())).isEmpty();
    }
}
