package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.mapper.UsuarioMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.repository.JpaUsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cubre el cambio de merge de fila completa a managed-update en save():
 * un dominio parcial (flags null) no debe pisar las columnas de la fila
 * existente; el PUT actual solo toca los campos que el dominio trae cargados.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaUsuarioRepositoryAdapter.class, UsuarioMapper.class,
        JpaUsuarioRepositoryAdapterTest.TestAuditingConfig.class})
class JpaUsuarioRepositoryAdapterTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaUsuarioRepositoryAdapter adapter;

    @Autowired
    private JpaUsuarioRepository jpaUsuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_withNullId_persistsNewUser() {
        Usuario created = adapter.save(Usuario.builder()
                .login("nuevo")
                .password("hash")
                .nombre("Nuevo")
                .geograficaId(1)
                .build());

        assertThat(created.getUserId()).isNotNull();
        assertThat(jpaUsuarioRepository.findByLogin("nuevo")).isPresent();
    }

    @Test
    void save_existingUser_withPartialDomain_preservesUnsetFlags() {
        UsuarioEntity row = jpaUsuarioRepository.save(UsuarioEntity.builder()
                .login("externo")
                .password("hash")
                .nombre("Usuario Externo")
                .geograficaId(7)
                .modificaChequera((byte) 1)
                .administrador((byte) 1)
                .usuarioExterno((byte) 1)
                .build());
        entityManager.clear();

        // dominio con flags null reales: ojo, new Usuario() NO deja null los flags
        // (Lombok >=1.18.2 aplica @Builder.Default tambien en @NoArgsConstructor),
        // por eso se anulan explicitamente. "null = no vino = no se toca".
        Usuario partial = new Usuario();
        partial.setUserId(row.getUserId());
        partial.setLogin("cambiado");
        partial.setPassword("otroHash");
        partial.setNombre("Cambiado");
        partial.setGeograficaId(3);
        partial.setImprimeChequera(null);
        partial.setNumeroOpManual(null);
        partial.setHabilitaOpEliminacion(null);
        partial.setEliminaChequera(null);
        partial.setModificaChequera(null);
        partial.setActivo(null);
        partial.setAdministrador(null);
        partial.setUsuarioExterno(null);

        adapter.save(partial);
        entityManager.flush();
        entityManager.clear();

        UsuarioEntity persisted = jpaUsuarioRepository.findByUserId(row.getUserId()).orElseThrow();
        assertThat(persisted.getLogin()).isEqualTo("cambiado");
        assertThat(persisted.getPassword()).isEqualTo("otroHash");
        assertThat(persisted.getNombre()).isEqualTo("Cambiado");
        assertThat(persisted.getGeograficaId()).isEqualTo(3);
        // flags: el dominio los trajo null -> NO se tocan (antes del refactor quedaban en 0)
        assertThat(persisted.getModificaChequera()).isEqualTo((byte) 1);
        assertThat(persisted.getAdministrador()).isEqualTo((byte) 1);
        assertThat(persisted.getUsuarioExterno()).isEqualTo((byte) 1);
        assertThat(persisted.getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void save_existingUser_appliesNonNullFlags() {
        UsuarioEntity row = jpaUsuarioRepository.save(UsuarioEntity.builder()
                .login("externo")
                .password("hash")
                .nombre("Usuario Externo")
                .geograficaId(7)
                .usuarioExterno((byte) 1)
                .build());
        entityManager.clear();

        adapter.save(Usuario.builder()
                .userId(row.getUserId())
                .login("externo")
                .password("hash")
                .nombre("Usuario Externo")
                .geograficaId(7)
                .usuarioExterno((byte) 0)
                .build());
        entityManager.flush();
        entityManager.clear();

        UsuarioEntity persisted = jpaUsuarioRepository.findByUserId(row.getUserId()).orElseThrow();
        assertThat(persisted.getUsuarioExterno()).isEqualTo((byte) 0);
    }
}
