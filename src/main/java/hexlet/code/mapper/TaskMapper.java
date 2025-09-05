package hexlet.code.mapper;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
		uses = {JsonNullableMapper.class},
		componentModel = MappingConstants.ComponentModel.SPRING,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
		unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

	@Autowired
	protected LabelRepository labelRepository;

	@Autowired
	protected TaskStatusRepository taskStatusRepository;

	@Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "unwrap")
	@Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusSlugToModel")
	@Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "labelIdsToModel")
	public abstract Task map(TaskCreateDTO dto);

	@Mapping(source = "assignee.id", target = "assigneeId", qualifiedByName = "wrap")
	@Mapping(source = "taskStatus.slug", target = "status", qualifiedByName = "wrap")
	@Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "modelToLabelIds")
	public abstract TaskDTO map(Task model);

	@Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "unwrap")
	@Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusSlugToModel")
	@Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "labelIdsToModel")
	public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

	@Mapping(source = "assignee.id", target = "assigneeId")
	@Mapping(source = "taskStatus.slug", target = "status")
	@Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "modelToLabelIds")
	public abstract TaskCreateDTO mapToCreateDTO(Task model);

	@Named("statusSlugToModel")
	protected TaskStatus statusSlugToModel(String slug) {
		return taskStatusRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found for slug: " + slug));
	}

	@Named("labelIdsToModel")
	protected Set<Label> labelIdsToModel(Set<Long> labelIds) {
		if (labelIds == null || labelIds.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(labelRepository.findByIdIn(labelIds));
	}

	@Named("modelToLabelIds")
	protected Set<Long> modelToLabelIds(Set<Label> labels) {
		if (labels == null || labels.isEmpty()) {
			return new HashSet<>();
		}
		return labels.stream()
				.map(Label::getId)
				.collect(Collectors.toSet());
	}
}
