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
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {ReferenceMapper.class, JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    protected LabelRepository labelRepository;

    @Autowired
    protected TaskStatusRepository taskStatusRepository;

    // ===================== Mapping Methods =====================

    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusSlugToModel")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "labelIdsToModel")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "modelToLabelIds")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "unwrap")
    @Mapping(source = "name", target = "name", qualifiedByName = "unwrap")
    @Mapping(source = "description", target = "description", qualifiedByName = "unwrap")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "statusSlugToModelFromNullable")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "unwrapLabelIds")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "modelToLabelIds")
    public abstract TaskCreateDTO mapToCreateDTO(Task model);

    // ===================== Helpers =====================

    @Named("statusSlugToModel")
    public TaskStatus statusSlugToModel(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found with slug: " + slug));
    }

    @Named("statusSlugToModelFromNullable")
    public TaskStatus statusSlugToModelFromNullable(JsonNullable<String> slugNullable) {
        if (slugNullable == null || !slugNullable.isPresent()) {
            return null;
        }
        return statusSlugToModel(slugNullable.get());
    }

    @Named("labelIdsToModel")
    public Set<Label> labelIdsToModel(Set<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findByIdIn(labelIds));
    }

    @Named("unwrapLabelIds")
    public Set<Label> unwrapLabelIds(JsonNullable<Set<Long>> labelIdsNullable) {
        if (labelIdsNullable == null || !labelIdsNullable.isPresent()) {
            return null;
        }
        return labelIdsToModel(labelIdsNullable.get());
    }

    @Named("modelToLabelIds")
    public Set<Long> modelToLabelIds(Set<Label> labels) {
        if (labels == null || labels.isEmpty()) {
            return new HashSet<>();
        }
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }
}
