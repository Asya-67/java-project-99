package hexlet.code.controllers.api;

import hexlet.code.dto.users.AuthResponseDTO;
import hexlet.code.dto.users.LoginRequest;
import hexlet.code.Utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthResponseDTO create(@RequestBody LoginRequest authRequest) {
        try {
            var authentication = new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword());
            authenticationManager.authenticate(authentication);

            var token = jwtUtils.generateToken(authRequest.getUsername());
            return new AuthResponseDTO(token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid login or password");
        }
    }
}
