package hexlet.code.controllers.api;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
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
@RequestMapping("/api/task_statuses")
@AllArgsConstructor
public class TaskStatusController {
	private final TaskStatusService taskStatusService;

	@GetMapping(path = "")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<TaskStatusDTO>> index() {

		List<TaskStatusDTO> taskStatuses = taskStatusService.getAll();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(taskStatuses.size()))
				.body(taskStatuses);
	}

	@PostMapping(path = "")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		TaskStatusDTO taskStatus =  taskStatusService.create(taskStatusData);
		return ResponseEntity.status(HttpStatus.CREATED).body(taskStatus);
	}

	@GetMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
		TaskStatusDTO taskStatus = taskStatusService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(taskStatus);
	}

	@PutMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TaskStatusDTO> update(@RequestBody @Valid TaskStatusUpdateDTO taskStatusData,
	                                            @PathVariable Long id) {
		TaskStatusDTO taskStatus = taskStatusService.update(taskStatusData, id);
		return ResponseEntity.status(HttpStatus.OK).body(taskStatus);
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		taskStatusService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
