package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioConfiguracionRequest;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioRequest;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioResponse;

@Component
public class UsuarioDtoMapper {

    public Usuario toDomain(UsuarioRequest request) {
        if (request == null) return null;
        Usuario.UsuarioBuilder builder = Usuario.builder()
                .login(request.getLogin())
                .password(request.getPassword())
                .nombre(request.getNombre())
                .dependenciaId(request.getDependenciaId())
                .geograficaId(request.getGeograficaId())
                .googleMail(request.getGoogleMail());

        if (request.getImprimeChequera() != null) builder.imprimeChequera(request.getImprimeChequera());
        if (request.getNumeroOpManual() != null) builder.numeroOpManual(request.getNumeroOpManual());
        if (request.getHabilitaOpEliminacion() != null) builder.habilitaOpEliminacion(request.getHabilitaOpEliminacion());
        if (request.getEliminaChequera() != null) builder.eliminaChequera(request.getEliminaChequera());
        if (request.getModificaChequera() != null) builder.modificaChequera(request.getModificaChequera());
        if (request.getActivo() != null) builder.activo(request.getActivo());
        if (request.getAdministrador() != null) builder.administrador(request.getAdministrador());
        if (request.getUsuarioExterno() != null) builder.usuarioExterno(request.getUsuarioExterno());

        return builder.build();
    }

    public Usuario toDomainConfiguracion(UsuarioConfiguracionRequest request) {
        if (request == null) return null;
        return Usuario.builder()
                .nombre(request.getNombre())
                .dependenciaId(request.getDependenciaId())
                .geograficaId(request.getGeograficaId())
                .googleMail(request.getGoogleMail())
                .imprimeChequera(request.getImprimeChequera())
                .numeroOpManual(request.getNumeroOpManual())
                .habilitaOpEliminacion(request.getHabilitaOpEliminacion())
                .eliminaChequera(request.getEliminaChequera())
                .modificaChequera(request.getModificaChequera())
                .activo(request.getActivo())
                .administrador(request.getAdministrador())
                .usuarioExterno(request.getUsuarioExterno())
                .build();
    }

    public UsuarioResponse toResponse(Usuario domain) {
        if (domain == null) return null;
        return UsuarioResponse.builder()
                .userId(domain.getUserId())
                .login(domain.getLogin())
                .nombre(domain.getNombre())
                .dependenciaId(domain.getDependenciaId())
                .geograficaId(domain.getGeograficaId())
                .imprimeChequera(domain.getImprimeChequera())
                .numeroOpManual(domain.getNumeroOpManual())
                .habilitaOpEliminacion(domain.getHabilitaOpEliminacion())
                .eliminaChequera(domain.getEliminaChequera())
                .modificaChequera(domain.getModificaChequera())
                .lastLog(domain.getLastLog())
                .googleMail(domain.getGoogleMail())
                .activo(domain.getActivo())
                .administrador(domain.getAdministrador())
                .usuarioExterno(domain.getUsuarioExterno())
                .debeCambiarClave(domain.getDebeCambiarClave())
                .build();
    }
}
