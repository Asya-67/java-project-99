package hexlet.code.dto.tasks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
	private Integer index;
	private Long assigneeId;

	@NotBlank
	private String name;

	private String description;

	@NotNull
	private String status;

	private Set<Long> taskLabelIds;
}
