package hexlet.code.service;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UpdateUserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static class UserNotFoundException extends RuntimeException {

    }
    public static class UserForbiddenException extends RuntimeException {

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserCreateDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPasswordDigest(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, UpdateUserDTO dto, String principalEmail) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equals(principalEmail)) {
            throw new UserForbiddenException();
        }

        if (dto.getEmail().isPresent()) {
            user.setEmail(dto.getEmail().get());
        }
        if (dto.getFirstName().isPresent()) {
            user.setFirstName(dto.getFirstName().get());
        }
        if (dto.getLastName().isPresent()) {
            user.setLastName(dto.getLastName().get());
        }
        if (dto.getPassword().isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(dto.getPassword().get()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id, String principalEmail) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equals(principalEmail)) {
            throw new UserForbiddenException();
        }

        userRepository.delete(user);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
