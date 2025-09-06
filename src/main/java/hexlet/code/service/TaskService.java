package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskRouteDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAll(TaskRouteDTO routes) {
        Specification<Task> spec = taskSpecification.build(routes);
        List<Task> tasks = repository.findAll(spec);
        List<TaskDTO> result = tasks.stream()
                .map(taskMapper::map)
                .toList();
        return result;
    }

    public TaskDTO create(TaskCreateDTO taskData) {
        Task task = taskMapper.map(taskData);
        repository.save(task);
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }

    public TaskDTO findById(Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }

    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        taskMapper.update(taskData, task);
        repository.save(task);
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
