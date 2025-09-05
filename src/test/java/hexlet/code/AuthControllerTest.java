package hexlet.code;

import hexlet.code.dto.AuthRequest;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper om;

	@Value("/api/login")
	private String url;

	@Test
	public void testIndex() throws Exception {
		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername("hexlet@example.com");
		authRequest.setPassword("qwerty");

		MockHttpServletRequestBuilder request = post(url).with(jwt())
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(authRequest));

		mockMvc.perform(request)
				.andExpect(status().isOk());
	}
}
