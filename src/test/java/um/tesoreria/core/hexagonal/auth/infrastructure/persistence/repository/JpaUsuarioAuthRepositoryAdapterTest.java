package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper.UsuarioAuthMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.repository.JpaUsuarioRepository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * Prueba de regresion del bug por el cual el login (que actualiza lastLog
 * mediante save -> merge de JPA) pisaba con NULL las columnas de los flags
 * que el mapper no copiaba (modificaChequera, administrador, usuarioExterno).
 * Con los updates dirigidos del puerto, las columnas no nombradas por el
 * UPDATE no pueden tocarse nunca mas.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaUsuarioAuthRepositoryAdapter.class, UsuarioAuthMapper.class,
        JpaUsuarioAuthRepositoryAdapterTest.TestAuditingConfig.class})
class JpaUsuarioAuthRepositoryAdapterTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaUsuarioAuthRepositoryAdapter adapter;

    @Autowired
    private JpaUsuarioRepository jpaUsuarioRepository;

    @Autowired
    private EntityManager entityManager;

    private Long saveRowWithFlagsEnabled() {
        UsuarioEntity saved = jpaUsuarioRepository.save(UsuarioEntity.builder()
                .login("externo")
                .password("hash")
                .nombre("Usuario Externo")
                .geograficaId(7)
                .modificaChequera((byte) 1)
                .googleMail("externo@um.edu.ar")
                .administrador((byte) 1)
                .usuarioExterno((byte) 1)
                .build());
        entityManager.clear();
        return saved.getUserId();
    }

    @Test
    void findByLogin_exposesFlagsLoadedFromDatabase() {
        saveRowWithFlagsEnabled();

        UsuarioAuth usuario = adapter.findByLogin("externo").orElseThrow();

        assertThat(usuario.getModificaChequera()).isEqualTo((byte) 1);
        assertThat(usuario.getAdministrador()).isEqualTo((byte) 1);
        assertThat(usuario.getUsuarioExterno()).isEqualTo((byte) 1);
    }

    @Test
    void updateLastLog_updatesOnlyLastLogAndPreservesFlags() {
        Long userId = saveRowWithFlagsEnabled();

        OffsetDateTime newLastLog = OffsetDateTime.now();
        adapter.updateLastLog(userId, newLastLog);
        entityManager.flush();
        entityManager.clear();

        UsuarioEntity persisted = jpaUsuarioRepository.findByUserId(userId).orElseThrow();
        assertThat(persisted.getModificaChequera()).isEqualTo((byte) 1);
        assertThat(persisted.getAdministrador()).isEqualTo((byte) 1);
        assertThat(persisted.getUsuarioExterno()).isEqualTo((byte) 1);
        assertThat(persisted.getActivo()).isEqualTo((byte) 1);
        assertThat(persisted.getPassword()).isEqualTo("hash");
        assertThat(persisted.getNombre()).isEqualTo("Usuario Externo");
        assertThat(persisted.getLastLog()).isNotNull();
        assertThat(persisted.getLastLog().toInstant())
                .isCloseTo(newLastLog.toInstant(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void updateCredentials_changesPasswordAndNombre_preservesFlags() {
        Long userId = saveRowWithFlagsEnabled();

        adapter.updateCredentials(userId, "nuevoHash", "Nombre Nuevo");
        entityManager.flush();
        entityManager.clear();

        UsuarioEntity persisted = jpaUsuarioRepository.findByUserId(userId).orElseThrow();
        assertThat(persisted.getPassword()).isEqualTo("nuevoHash");
        assertThat(persisted.getNombre()).isEqualTo("Nombre Nuevo");
        assertThat(persisted.getModificaChequera()).isEqualTo((byte) 1);
        assertThat(persisted.getAdministrador()).isEqualTo((byte) 1);
        assertThat(persisted.getUsuarioExterno()).isEqualTo((byte) 1);
        assertThat(persisted.getGoogleMail()).isEqualTo("externo@um.edu.ar");
    }

    @Test
    void updateCredentials_withNullNombre_keepsExistingNombre() {
        Long userId = saveRowWithFlagsEnabled();

        adapter.updateCredentials(userId, "nuevoHash", null);
        entityManager.flush();
        entityManager.clear();

        UsuarioEntity persisted = jpaUsuarioRepository.findByUserId(userId).orElseThrow();
        assertThat(persisted.getPassword()).isEqualTo("nuevoHash");
        assertThat(persisted.getNombre()).isEqualTo("Usuario Externo");
    }

    @Test
    void updateCredentials_userNotFound_throws() {
        assertThatThrownBy(() -> adapter.updateCredentials(999999L, "hash", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Usuario NO Encontrado");
    }
}
