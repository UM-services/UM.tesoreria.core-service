package um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.entity.UsuarioEntity;

@Component
public class UsuarioMapper {

    public Usuario toDomainModel(UsuarioEntity entity) {
        if (entity == null) return null;
        return Usuario.builder()
                .userId(entity.getUserId())
                .login(entity.getLogin())
                .password(entity.getPassword())
                .nombre(entity.getNombre())
                .geograficaId(entity.getGeograficaId())
                .imprimeChequera(entity.getImprimeChequera())
                .numeroOpManual(entity.getNumeroOpManual())
                .habilitaOpEliminacion(entity.getHabilitaOpEliminacion())
                .eliminaChequera(entity.getEliminaChequera())
                .modificaChequera(entity.getModificaChequera())
                .lastLog(entity.getLastLog())
                .googleMail(entity.getGoogleMail())
                .activo(entity.getActivo())
                .build();
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity.UsuarioEntityBuilder builder = UsuarioEntity.builder()
                .userId(domain.getUserId())
                .login(domain.getLogin())
                .password(domain.getPassword())
                .nombre(domain.getNombre())
                .geograficaId(domain.getGeograficaId())
                .lastLog(domain.getLastLog())
                .googleMail(domain.getGoogleMail());

        if (domain.getImprimeChequera() != null) builder.imprimeChequera(domain.getImprimeChequera());
        if (domain.getNumeroOpManual() != null) builder.numeroOpManual(domain.getNumeroOpManual());
        if (domain.getHabilitaOpEliminacion() != null) builder.habilitaOpEliminacion(domain.getHabilitaOpEliminacion());
        if (domain.getEliminaChequera() != null) builder.eliminaChequera(domain.getEliminaChequera());
        if (domain.getModificaChequera() != null) builder.modificaChequera(domain.getModificaChequera());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());

        return builder.build();
    }
}
