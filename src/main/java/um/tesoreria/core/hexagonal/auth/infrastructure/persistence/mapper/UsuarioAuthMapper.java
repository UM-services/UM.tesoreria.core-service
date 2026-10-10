package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;

@Component
public class UsuarioAuthMapper {

    public UsuarioAuth toDomainModel(UsuarioEntity entity) {
        if (entity == null) return null;
        return UsuarioAuth.builder()
                .userId(entity.getUserId())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .nombre(entity.getNombre())
                .dependenciaId(entity.getDependenciaId())
                .geograficaId(entity.getGeograficaId())
                .imprimeChequera(entity.getImprimeChequera())
                .numeroOpManual(entity.getNumeroOpManual())
                .habilitaOpEliminacion(entity.getHabilitaOpEliminacion())
                .eliminaChequera(entity.getEliminaChequera())
                .modificaChequera(entity.getModificaChequera())
                .lastLog(entity.getLastLog())
                .googleMail(entity.getGoogleMail())
                .activo(entity.getActivo())
                .administrador(entity.getAdministrador())
                .usuarioExterno(entity.getUsuarioExterno())
                .debeCambiarClave(entity.getDebeCambiarClave())
                .build();
    }
}
