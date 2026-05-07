package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.kotlin.model.Usuario;

@Component
public class UsuarioAuthMapper {

    public Usuario toEntity(UsuarioAuth domain) {
        if (domain == null) return null;
        Usuario entity = new Usuario();
        entity.setUserId(domain.getUserId());
        entity.setLogin(domain.getLogin());
        entity.setPassword(domain.getPassword());
        entity.setNombre(domain.getNombre());
        entity.setGeograficaId(domain.getGeograficaId());
        entity.setImprimeChequera(domain.getImprimeChequera());
        entity.setNumeroOpManual(domain.getNumeroOpManual());
        entity.setHabilitaOpEliminacion(domain.getHabilitaOpEliminacion());
        entity.setEliminaChequera(domain.getEliminaChequera());
        entity.setLastLog(domain.getLastLog());
        entity.setGoogleMail(domain.getGoogleMail());
        entity.setActivo(domain.getActivo());
        return entity;
    }

    public UsuarioAuth toDomainModel(Usuario entity) {
        if (entity == null) return null;
        return new UsuarioAuth(
            entity.getUserId(),
            entity.getLogin(),
            entity.getPassword(),
            entity.getNombre(),
            entity.getGeograficaId(),
            entity.getImprimeChequera(),
            entity.getNumeroOpManual(),
            entity.getHabilitaOpEliminacion(),
            entity.getEliminaChequera(),
            entity.getLastLog(),
            entity.getGoogleMail(),
            entity.getActivo()
        );
    }
}
