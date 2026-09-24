package um.tesoreria.core.hexagonal.auth.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.auth.application.service.AuthService;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.ChangePasswordRequest;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginRequest;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginResponse;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper.AuthDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthDtoMapper authDtoMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            UsuarioAuth usuario = authService.login(loginRequest.getLogin(), loginRequest.getPassword());
            LoginResponse response = authDtoMapper.toResponse(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<LoginResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            UsuarioAuth usuario = authService.changePassword(
                    request.getUserId(),
                    request.getLogin(),
                    request.getCurrentPassword(),
                    request.getNewPassword(),
                    request.getReClaveNueva(),
                    request.getNombre()
            );
            LoginResponse response = authDtoMapper.toResponse(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity<LoginResponse> getMe(@PathVariable Long userId) {
        try {
            UsuarioAuth usuario = authService.findById(userId);
            return ResponseEntity.ok(authDtoMapper.toResponse(usuario));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
