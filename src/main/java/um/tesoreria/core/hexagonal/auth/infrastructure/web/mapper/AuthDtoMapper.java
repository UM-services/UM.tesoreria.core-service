package um.tesoreria.core.hexagonal.auth.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.infrastructure.web.dto.LoginResponse;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

@Component
@RequiredArgsConstructor
public class AuthDtoMapper {

    private final GeograficaService geograficaService;

    public LoginResponse toResponse(UsuarioAuth domain) {
        if (domain == null) return null;
        return LoginResponse.builder()
                .token("dummy-jwt-token-replace-later")
                .userId(domain.getUserId())
                .login(domain.getLogin())
                .nombre(domain.getNombre())
                .dependenciaId(domain.getDependenciaId())
                .geograficaId(domain.getGeograficaId())
                .sede(geograficaService.findByGeograficaId(domain.getGeograficaId())
                        .map(Geografica::getNombre)
                        .orElse("Sede no encontrada"))
                .imprimeChequera(domain.getImprimeChequera())
                .numeroOpManual(domain.getNumeroOpManual())
                .habilitaOpEliminacion(domain.getHabilitaOpEliminacion())
                .eliminaChequera(domain.getEliminaChequera())
                .modificaChequera(domain.getModificaChequera())
                .administrador(domain.getAdministrador())
                .usuarioExterno(domain.getUsuarioExterno())
                .debeCambiarClave(domain.getDebeCambiarClave())
                .build();
    }
}
