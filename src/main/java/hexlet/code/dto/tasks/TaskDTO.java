package hexlet.code.dto.tasks;

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
	private LocalDate createdAt;

	private JsonNullable<Long> assigneeId;
	private JsonNullable<String> name;
	private JsonNullable<String> description;
	private JsonNullable<String> status;
	private JsonNullable<Set<Long>> taskLabelIds;
}
