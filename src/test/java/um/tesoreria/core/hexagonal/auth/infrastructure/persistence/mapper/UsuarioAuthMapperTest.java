package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioAuthMapperTest {

    private final UsuarioAuthMapper mapper = new UsuarioAuthMapper();

    private UsuarioEntity fullEntity() {
        return UsuarioEntity.builder()
                .userId(1L)
                .login("externo")
                .password("hash")
                .nombre("Usuario Externo")
                .geograficaId(7)
                .imprimeChequera((byte) 1)
                .numeroOpManual((byte) 0)
                .habilitaOpEliminacion((byte) 1)
                .eliminaChequera((byte) 0)
                .modificaChequera((byte) 1)
                .lastLog(OffsetDateTime.parse("2026-01-01T00:00:00Z"))
                .googleMail("externo@um.edu.ar")
                .activo((byte) 1)
                .administrador((byte) 1)
                .usuarioExterno((byte) 1)
                .build();
    }

    @Test
    void toDomainModel_loadsAllFieldsIncludingFlags() {
        UsuarioEntity entity = fullEntity();

        UsuarioAuth domain = mapper.toDomainModel(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getUserId()).isEqualTo(entity.getUserId());
        assertThat(domain.getLogin()).isEqualTo(entity.getLogin());
        assertThat(domain.getPassword()).isEqualTo(entity.getPassword());
        assertThat(domain.getNombre()).isEqualTo(entity.getNombre());
        assertThat(domain.getGeograficaId()).isEqualTo(entity.getGeograficaId());
        assertThat(domain.getImprimeChequera()).isEqualTo(entity.getImprimeChequera());
        assertThat(domain.getNumeroOpManual()).isEqualTo(entity.getNumeroOpManual());
        assertThat(domain.getHabilitaOpEliminacion()).isEqualTo(entity.getHabilitaOpEliminacion());
        assertThat(domain.getEliminaChequera()).isEqualTo(entity.getEliminaChequera());
        assertThat(domain.getModificaChequera()).isEqualTo(entity.getModificaChequera());
        assertThat(domain.getLastLog()).isEqualTo(entity.getLastLog());
        assertThat(domain.getGoogleMail()).isEqualTo(entity.getGoogleMail());
        assertThat(domain.getActivo()).isEqualTo(entity.getActivo());
        assertThat(domain.getAdministrador()).isEqualTo(entity.getAdministrador());
        assertThat(domain.getUsuarioExterno()).isEqualTo(entity.getUsuarioExterno());
    }

    @Test
    void toDomainModel_null_returnsNull() {
        assertThat(mapper.toDomainModel(null)).isNull();
    }
}
