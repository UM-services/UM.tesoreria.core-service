package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;

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
                .administrador(entity.getAdministrador())
                .usuarioExterno(entity.getUsuarioExterno())
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
        if (domain.getAdministrador() != null) builder.administrador(domain.getAdministrador());
        if (domain.getUsuarioExterno() != null) builder.usuarioExterno(domain.getUsuarioExterno());

        return builder.build();
    }

    /**
     * Aplica los campos del dominio sobre una entidad YA MANAGED (cargada por id).
     * Los flags null se ignoran: "no vino = no se toca". Las columnas que no existen
     * en el dominio quedan intactas por definicion (dirty checking solo emite UPDATE
     * de lo efectivamente modificado).
     */
    public void updateEntity(Usuario domain, UsuarioEntity entity) {
        if (domain == null || entity == null) return;
        entity.setLogin(domain.getLogin());
        entity.setPassword(domain.getPassword());
        entity.setNombre(domain.getNombre());
        entity.setGeograficaId(domain.getGeograficaId());
        entity.setLastLog(domain.getLastLog());
        entity.setGoogleMail(domain.getGoogleMail());
        if (domain.getImprimeChequera() != null) entity.setImprimeChequera(domain.getImprimeChequera());
        if (domain.getNumeroOpManual() != null) entity.setNumeroOpManual(domain.getNumeroOpManual());
        if (domain.getHabilitaOpEliminacion() != null) entity.setHabilitaOpEliminacion(domain.getHabilitaOpEliminacion());
        if (domain.getEliminaChequera() != null) entity.setEliminaChequera(domain.getEliminaChequera());
        if (domain.getModificaChequera() != null) entity.setModificaChequera(domain.getModificaChequera());
        if (domain.getActivo() != null) entity.setActivo(domain.getActivo());
        if (domain.getAdministrador() != null) entity.setAdministrador(domain.getAdministrador());
        if (domain.getUsuarioExterno() != null) entity.setUsuarioExterno(domain.getUsuarioExterno());
    }
}
