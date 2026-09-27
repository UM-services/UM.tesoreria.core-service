package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity.UsuarioChequeraGeograficaEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.mapper.UsuarioChequeraGeograficaMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.repository.JpaUsuarioChequeraGeograficaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaUsuarioChequeraGeograficaRepositoryAdapter.class, JpaUsuarioChequeraGeograficaRepositoryAdapterTest.TestAuditingConfig.class})
class JpaUsuarioChequeraGeograficaRepositoryAdapterTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaUsuarioChequeraGeograficaRepositoryAdapter adapter;

    @Autowired
    private JpaUsuarioChequeraGeograficaRepository jpaRepository;

    @MockitoBean
    private UsuarioChequeraGeograficaMapper mapper;

    @Test
    void findAllByUserId_returnsMappedList() {
        jpaRepository.save(UsuarioChequeraGeograficaEntity.builder()
                .userId(1L).geograficaId(10).build());
        jpaRepository.save(UsuarioChequeraGeograficaEntity.builder()
                .userId(1L).geograficaId(20).build());
        jpaRepository.save(UsuarioChequeraGeograficaEntity.builder()
                .userId(2L).geograficaId(30).build());

        when(mapper.toDomain(any(UsuarioChequeraGeograficaEntity.class)))
                .thenReturn(UsuarioChequeraGeografica.builder().build())
                .thenReturn(UsuarioChequeraGeografica.builder().build());

        List<UsuarioChequeraGeografica> results = adapter.findAllByUserId(1L);

        assertThat(results).hasSize(2);
        verify(mapper, times(2)).toDomain(any(UsuarioChequeraGeograficaEntity.class));
    }

    @Test
    void findAllByUserId_whenNone_returnsEmptyList() {
        var results = adapter.findAllByUserId(999L);

        assertThat(results).isEmpty();
    }

}
