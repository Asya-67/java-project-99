package hexlet.code.admininit;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        Optional<User> firstUser = userRepository.findByEmail("hexlet@example.com");
        if (firstUser.isEmpty()) {
            var email = "hexlet@example.com";
            var userData = new User();
            userData.setEmail(email);
            userData.setPasswordDigest(passwordEncoder.encode("qwerty"));
            userRepository.save(userData);

            taskStatusesInitializer();
            labelsInitializer();
        }
    }

    public final void taskStatusesInitializer() {
        Map<String, String> firstStructure = new HashMap<>();

        firstStructure.put("Draft", "draft");
        firstStructure.put("ToReview", "to_review");
        firstStructure.put("ToBeFixed", "to_be_fixed");
        firstStructure.put("ToPublish", "to_publish");
        firstStructure.put("Published", "published");

        firstStructure.entrySet().stream()
                .map(entry -> {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setName(entry.getKey());
                    taskStatus.setSlug(entry.getValue());
                    return taskStatus;
                })
                .forEach(taskStatusRepository::save);
    }

    public final void labelsInitializer() {
        Label label1 = new Label();
        Label label2 = new Label();

        label1.setName("feature");
        label2.setName("bug");

        labelRepository.save(label1);
        labelRepository.save(label2);
    }
}
