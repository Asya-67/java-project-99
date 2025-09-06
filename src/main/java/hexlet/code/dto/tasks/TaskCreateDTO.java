package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @NotNull
    @JsonProperty("status")
    private String status;

    @JsonProperty("taskLabelIds")
    private Set<Long> taskLabelIds;
}
