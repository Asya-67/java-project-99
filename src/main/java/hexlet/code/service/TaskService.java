package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskRouteDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public List<TaskDTO> getAll(TaskRouteDTO params) {
        Specification<Task> spec = taskSpecification.build(params);
        List<Task> tasks = repository.findAll(spec);
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        Task task = taskMapper.map(taskData);
        repository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));

        // Статус
        JsonNullable<String> statusNullable = taskData.getStatus();
        if (statusNullable != null && statusNullable.isPresent()) {
            String statusValue = statusNullable.orElse(null);
            TaskStatus taskStatus = taskStatusRepository.findBySlug(statusValue)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Task status not found: " + statusValue));
            task.setTaskStatus(taskStatus);
        }

        // Ассайн
        JsonNullable<Long> assigneeIdNullable = taskData.getAssigneeId();
        if (assigneeIdNullable != null && assigneeIdNullable.isPresent()) {
            Long assigneeId = assigneeIdNullable.orElse(null);
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        // Labels
        JsonNullable<Set<Long>> labelIdsNullable = taskData.getTaskLabelIds();
        if (labelIdsNullable != null && labelIdsNullable.isPresent()) {
            Set<Long> labelIds = labelIdsNullable.orElse(Set.of());
            Set<Label> labels = labelIds.stream()
                    .map(labelId -> labelRepository.findById(labelId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Label not found with id: " + labelId)))
                    .collect(Collectors.toSet());
            task.setLabels(labels);
        }

        // Name
        JsonNullable<String> nameNullable = taskData.getName();
        if (nameNullable != null && nameNullable.isPresent()) {
            task.setName(nameNullable.orElse(null));
        }

        // Description
        JsonNullable<String> descriptionNullable = taskData.getDescription();
        if (descriptionNullable != null && descriptionNullable.isPresent()) {
            task.setDescription(descriptionNullable.orElse(null));
        }

        // Index
        JsonNullable<Integer> indexNullable = taskData.getIndex();
        if (indexNullable != null && indexNullable.isPresent()) {
            task.setIndex(indexNullable.orElse(null));
        }

        repository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
