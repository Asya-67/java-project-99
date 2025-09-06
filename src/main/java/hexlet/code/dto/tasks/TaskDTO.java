package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private JsonNullable<Long> index;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @JsonProperty("title")
    private JsonNullable<String> name;

    @JsonProperty("content")
    private JsonNullable<String> description;

    @JsonProperty("status")
    private JsonNullable<String> status;

    @JsonProperty("taskLabelIds")
    private JsonNullable<Set<Long>> taskLabelIds;
}
