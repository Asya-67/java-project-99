package hexlet.code.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserDTO {
    @NotBlank
    private JsonNullable<String> email = JsonNullable.undefined();
    @NotBlank
    private JsonNullable<String> firstName = JsonNullable.undefined();
    @NotBlank
    private JsonNullable<String> lastName = JsonNullable.undefined();
    @Size(min = 3)
    private JsonNullable<String> password = JsonNullable.undefined();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
