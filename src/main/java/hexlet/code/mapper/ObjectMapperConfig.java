package hexlet.code.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

	@Bean
	public ObjectMapper objectMapper(ObjectMapper defaultMapper) {
		defaultMapper.registerModule(new JsonNullableModule());
		return defaultMapper;
	}
}
