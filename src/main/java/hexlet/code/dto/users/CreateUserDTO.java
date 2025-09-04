package hexlet.code.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {
    @Email
    @NotBlank
    private String email;

    private String firstName;
    private String lastName;

    @NotBlank
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;
}
