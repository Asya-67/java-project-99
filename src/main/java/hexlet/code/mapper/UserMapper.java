package hexlet.code.mapper;

import hexlet.code.dto.users.UserDTO;
import hexlet.code.model.User;
import org.openapitools.jackson.nullable.JsonNullable;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(JsonNullable.of(user.getEmail()));
        dto.setFirstName(JsonNullable.of(user.getFirstName()));
        dto.setLastName(JsonNullable.of(user.getLastName()));
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public static void updateEntity(User user, UserDTO dto) {
        if (dto.getEmail() != null && dto.getEmail().isPresent()) {
            user.setEmail(dto.getEmail().get());
        }
        if (dto.getFirstName() != null && dto.getFirstName().isPresent()) {
            user.setFirstName(dto.getFirstName().get());
        }
        if (dto.getLastName() != null && dto.getLastName().isPresent()) {
            user.setLastName(dto.getLastName().get());
        }
    }
}
