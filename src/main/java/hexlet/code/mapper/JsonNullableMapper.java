package hexlet.code.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class JsonNullableMapper {

    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

    @Named("unwrap")
    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    @Named("unwrap")
    public static String unwrapString(JsonNullable<String> value) {
        return value == null ? null : value.orElse(null);
    }

    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
