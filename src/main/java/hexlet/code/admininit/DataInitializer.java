package hexlet.code.admininit;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("hexlet@example.com");
                admin.setPassword("qwerty");
                admin.setFirstName("Admin");
                admin.setLastName("System");
                userRepository.save(admin);
            }
        };
    }
}
