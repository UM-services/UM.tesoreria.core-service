package um.tesoreria.core.hexagonal.auth.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.auth.application.service.AuthService;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.ChangePasswordRequest;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginRequest;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginResponse;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper.AuthDtoMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthDtoMapper authDtoMapper;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_successful() {
        LoginRequest req = new LoginRequest("test", "pass");
        UsuarioAuth user = new UsuarioAuth();
        LoginResponse resp = LoginResponse.builder().userId(1L).login("test").build();

        when(authService.login("test", "pass")).thenReturn(user);
        when(authDtoMapper.toResponse(user)).thenReturn(resp);

        ResponseEntity<LoginResponse> response = controller.login(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(resp);
    }

    @Test
    void changePassword_successful() {
        ChangePasswordRequest req = ChangePasswordRequest.builder()
                .userId(1L)
                .login("test")
                .currentPassword("old")
                .newPassword("new")
                .reClaveNueva("new")
                .nombre("Test")
                .build();
        UsuarioAuth user = new UsuarioAuth();
        LoginResponse resp = LoginResponse.builder().userId(1L).login("test").nombre("Test").build();

        when(authService.changePassword(1L, "test", "old", "new", "new", "Test")).thenReturn(user);
        when(authDtoMapper.toResponse(user)).thenReturn(resp);

        ResponseEntity<LoginResponse> response = controller.changePassword(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(resp);
    }

    @Test
    void changePassword_whenIllegalArgument_throwsBadRequest() {
        ChangePasswordRequest req = ChangePasswordRequest.builder()
                .userId(1L)
                .currentPassword("old")
                .newPassword("new")
                .reClaveNueva("different")
                .build();

        when(authService.changePassword(1L, null, "old", "new", "different", null))
                .thenThrow(new IllegalArgumentException("ERROR: Claves NO Coinciden"));

        assertThatThrownBy(() -> controller.changePassword(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST);
    }

    @Test
    void getMe_successful() {
        UsuarioAuth user = new UsuarioAuth();
        user.setUserId(1L);
        user.setLogin("testUser");
        LoginResponse resp = LoginResponse.builder().userId(1L).login("testUser").build();

        when(authService.findById(1L)).thenReturn(user);
        when(authDtoMapper.toResponse(user)).thenReturn(resp);

        ResponseEntity<LoginResponse> response = controller.getMe(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(resp);
        assertThat(response.getBody().getLogin()).isEqualTo("testUser");
    }

    @Test
    void getMe_notFound() {
        when(authService.findById(99L)).thenThrow(new IllegalArgumentException("ERROR: Usuario no encontrado"));

        assertThatThrownBy(() -> controller.getMe(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }
}
