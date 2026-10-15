package um.tesoreria.core.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;

/**
 * Registra el interceptor de permisos. El enforcement es opt-in por entorno:
 * {@code app.permissions.enforce} (env {@code APP_PERMISSIONS_ENFORCE}).
 *
 * <p><b>Sólo se activa cuando la propiedad vale {@code true}</b>
 * ({@link ConditionalOnProperty}). Por defecto no se registra ningún interceptor,
 * así ningún endpoint cambia de comportamiento (legacy intacto) y los slices
 * {@code @WebMvcTest} no necesitan el bean de permisos. Cuando se activa, sólo
 * afecta a endpoints anotados con {@link RequierePermiso}.
 */
@Configuration
@ConditionalOnProperty(name = "app.permissions.enforce", havingValue = "true")
@RequiredArgsConstructor
public class PermissionWebConfig implements WebMvcConfigurer {

    private final GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequierePermisoInterceptor(getPermisosEfectivosUseCase, true));
    }
}
