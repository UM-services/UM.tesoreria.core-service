package um.tesoreria.core.configuration.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequierePermisoInterceptorTest {

    @Mock
    private GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;

    static class Target {
        @RequierePermiso("pagos.reembolsos")
        public void anotado() {
        }

        public void libre() {
        }
    }

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private HandlerMethod handler(String metodo) {
        try {
            return new HandlerMethod(new Target(), Target.class.getMethod(metodo));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private MockHttpServletRequest requestCon(String userId) {
        var request = new MockHttpServletRequest();
        if (userId != null) {
            request.addHeader(RequierePermisoInterceptor.USER_ID_HEADER, userId);
        }
        return request;
    }

    @Test
    void cuandoElEnforcementEstaApagado_noHaceNada() throws Exception {
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, false);

        boolean result = interceptor.preHandle(requestCon(null), response, handler("anotado"));

        assertThat(result).isTrue();
        verifyNoInteractions(getPermisosEfectivosUseCase);
    }

    @Test
    void endpointNoAnotado_pasaAunqueElEnforcementEsteEncendido() throws Exception {
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true);

        boolean result = interceptor.preHandle(requestCon(null), response, handler("libre"));

        assertThat(result).isTrue();
        verifyNoInteractions(getPermisosEfectivosUseCase);
    }

    @Test
    void endpointAnotado_conPermiso_pasa() throws Exception {
        when(getPermisosEfectivosUseCase.getPermisosEfectivos(1L))
                .thenReturn(PermisoEfectivo.builder().userId(1L).permisos(List.of("pagos.reembolsos")).build());
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true);

        boolean result = interceptor.preHandle(requestCon("1"), response, handler("anotado"));

        assertThat(result).isTrue();
    }

    @Test
    void endpointAnotado_sinElPermiso_devuelve403() {
        when(getPermisosEfectivosUseCase.getPermisosEfectivos(1L))
                .thenReturn(PermisoEfectivo.builder().userId(1L).permisos(List.of("otro.permiso")).build());
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true);

        assertThatThrownBy(() -> interceptor.preHandle(requestCon("1"), response, handler("anotado")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void endpointAnotado_sinIdentidad_devuelve403() {
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true);

        assertThatThrownBy(() -> interceptor.preHandle(requestCon(null), response, handler("anotado")))
                .isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(getPermisosEfectivosUseCase);
    }

    @Test
    void endpointAnotado_usuarioInexistente_devuelve403() {
        when(getPermisosEfectivosUseCase.getPermisosEfectivos(9L))
                .thenThrow(new PermisoEfectivoException("no existe"));
        var interceptor = new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true);

        assertThatThrownBy(() -> interceptor.preHandle(requestCon("9"), response, handler("anotado")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }
}
