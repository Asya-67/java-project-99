package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskRouteDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

	private final TaskRepository repository;
	private final TaskMapper taskMapper;
	private final TaskSpecification taskSpecification;

	public List<TaskDTO> getAll(TaskRouteDTO params) {
		Specification<Task> spec = taskSpecification.build(params);
		List<Task> tasks = repository.findAll(spec);
		return tasks.stream()
				.map(taskMapper::map)
				.toList();
	}

	public TaskDTO create(TaskCreateDTO dto) {
		Task task = taskMapper.map(dto);
		Task savedTask = repository.save(task);
		return taskMapper.map(savedTask);
	}

	public TaskDTO findById(Long id) {
		Task task = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
		return taskMapper.map(task);
	}

	@Transactional
	public TaskDTO update(TaskUpdateDTO dto, Long id) {
		Task task = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

		taskMapper.update(dto, task);
		Task updatedTask = repository.save(task);
		return taskMapper.map(updatedTask);
	}

	public void delete(Long id) {
		Task task = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
		repository.delete(task);
	}
}
