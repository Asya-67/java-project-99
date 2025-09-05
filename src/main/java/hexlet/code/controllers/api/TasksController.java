package hexlet.code.controllers.api;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskRouteDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TasksController {
	private final TaskService tasksService;

	@GetMapping(path = "")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<TaskDTO>> index(TaskRouteDTO params) {
		List<TaskDTO> tasks = tasksService.getAll(params);
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(tasks.size()))
				.body(tasks);
	}

	@PostMapping(path = "")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TaskDTO> create(@Valid @RequestBody TaskCreateDTO taskData)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		TaskDTO tasks =  tasksService.create(taskData);
		return ResponseEntity.status(HttpStatus.CREATED).body(tasks);
	}

	@GetMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TaskDTO> show(@PathVariable Long id) {
		TaskDTO task = tasksService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(task);
	}

	@PutMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TaskDTO> update(@RequestBody @Valid TaskUpdateDTO taskData, @PathVariable Long id) {
		TaskDTO task = tasksService.update(taskData, id);
		return ResponseEntity.status(HttpStatus.OK).body(task);
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		tasksService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
