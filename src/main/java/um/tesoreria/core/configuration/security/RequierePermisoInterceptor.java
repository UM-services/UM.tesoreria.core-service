package um.tesoreria.core.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;

/**
 * PEP (Policy Enforcement Point): evalúa {@link RequierePermiso} sobre el handler.
 *
 * <p>Opt-in y desactivado por defecto: si {@code enforce} es false, no hace nada
 * (todos los endpoints existentes/legacy quedan intactos). Sólo actúa sobre
 * endpoints anotados explícitamente.
 */
public class RequierePermisoInterceptor implements HandlerInterceptor {

    /** Header transitorio de identidad hasta que exista JWT (M3). */
    public static final String USER_ID_HEADER = "X-User-Id";

    private final GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;
    private final boolean enforce;

    public RequierePermisoInterceptor(GetPermisosEfectivosUseCase getPermisosEfectivosUseCase, boolean enforce) {
        this.getPermisosEfectivosUseCase = getPermisosEfectivosUseCase;
        this.enforce = enforce;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enforce || !(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RequierePermiso requierePermiso = resolveAnnotation(handlerMethod);
        if (requierePermiso == null) {
            return true;
        }
        Long userId = resolveUserId(request);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Falta identidad del usuario para evaluar el permiso " + requierePermiso.value());
        }
        boolean permitido;
        try {
            PermisoEfectivo efectivo = getPermisosEfectivosUseCase.getPermisosEfectivos(userId);
            permitido = efectivo.getPermisos() != null
                    && efectivo.getPermisos().contains(requierePermiso.value());
        } catch (PermisoEfectivoException e) {
            permitido = false;
        }
        if (!permitido) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Permiso requerido: " + requierePermiso.value());
        }
        return true;
    }

    private RequierePermiso resolveAnnotation(HandlerMethod handlerMethod) {
        RequierePermiso onMethod = handlerMethod.getMethodAnnotation(RequierePermiso.class);
        return onMethod != null ? onMethod : handlerMethod.getBeanType().getAnnotation(RequierePermiso.class);
    }

    private Long resolveUserId(HttpServletRequest request) {
        String header = request.getHeader(USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
