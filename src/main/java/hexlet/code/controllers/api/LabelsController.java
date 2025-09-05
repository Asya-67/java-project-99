package hexlet.code.controllers.api;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.service.LabelService;
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
@RequestMapping(path = "/api/labels")
@AllArgsConstructor
public class LabelsController {
	private final LabelService labelService;

	@GetMapping(path = "")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<LabelDTO>> index() {
		List<LabelDTO> labels = labelService.getAll();
		return ResponseEntity.ok()
				.header("X-Total-Count", String.valueOf(labels.size()))
				.body(labels);
	}

	@PostMapping(path = "")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<LabelDTO> create(@Valid @RequestBody LabelCreateDTO labelData)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		LabelDTO labels =  labelService.create(labelData);
		return ResponseEntity.status(HttpStatus.CREATED).body(labels);
	}

	@GetMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<LabelDTO> show(@PathVariable Long id) {
		LabelDTO label = labelService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(label);
	}

	@PutMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<LabelDTO> update(@RequestBody @Valid LabelUpdateDTO labelData, @PathVariable Long id) {
		LabelDTO label = labelService.update(labelData, id);
		return ResponseEntity.status(HttpStatus.OK).body(label);
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		labelService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
