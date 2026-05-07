package um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginResponse;
import um.tesoreria.core.hexagonal.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;

@Component
@RequiredArgsConstructor
public class AuthDtoMapper {

    private final GeograficaService geograficaService;

    public LoginResponse toResponse(UsuarioAuth domain) {
        if (domain == null) return null;
        return LoginResponse.builder()
                .token("dummy-jwt-token-replace-later")
                .userId(domain.getUserId())
                .nombre(domain.getNombre())
                .sede(geograficaService.findByGeograficaId(domain.getGeograficaId())
                        .map(Geografica::getNombre)
                        .orElse("Sede no encontrada"))
                .build();
    }
}
