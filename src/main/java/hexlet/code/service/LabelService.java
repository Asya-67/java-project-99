package hexlet.code.service;

import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private final LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        List<Label> labels = labelRepository.findAll();
        var result = labels.stream()
                .map(labelMapper::map)
                .toList();
        return result;
    }

    public LabelDTO create(LabelCreateDTO labelData) {
        Label label = labelMapper.map(labelData);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found!"));
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    public LabelDTO update(LabelUpdateDTO labelData, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found!"));
        labelMapper.update(labelData, label);
        labelRepository.save(label);
        var labelDTO = labelMapper.map(label);
        return labelDTO;
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
