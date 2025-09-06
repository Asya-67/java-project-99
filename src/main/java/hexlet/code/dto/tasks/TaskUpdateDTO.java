package hexlet.code.dto.tasks;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();
    private JsonNullable<String> status = JsonNullable.undefined();
    private JsonNullable<Set<Long>> taskLabelIds = JsonNullable.undefined();
}
