package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.mapper.UsuarioChequeraClaseChequeraMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.repository.JpaUsuarioChequeraClaseChequeraRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaUsuarioChequeraClaseChequeraRepositoryAdapter.class, JpaUsuarioChequeraClaseChequeraRepositoryAdapterTest.TestAuditingConfig.class})
class JpaUsuarioChequeraClaseChequeraRepositoryAdapterTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaUsuarioChequeraClaseChequeraRepositoryAdapter adapter;

    @Autowired
    private JpaUsuarioChequeraClaseChequeraRepository jpaRepository;

    @MockitoBean
    private UsuarioChequeraClaseChequeraMapper mapper;

    @Test
    void findAllByUserId_returnsMappedList() {
        jpaRepository.save(UsuarioChequeraClaseChequeraEntity.builder()
                .userId(1L).claseChequeraId(10).build());
        jpaRepository.save(UsuarioChequeraClaseChequeraEntity.builder()
                .userId(1L).claseChequeraId(20).build());
        jpaRepository.save(UsuarioChequeraClaseChequeraEntity.builder()
                .userId(2L).claseChequeraId(30).build());

        when(mapper.toDomain(any(UsuarioChequeraClaseChequeraEntity.class)))
                .thenReturn(UsuarioChequeraClaseChequera.builder().build())
                .thenReturn(UsuarioChequeraClaseChequera.builder().build());

        List<UsuarioChequeraClaseChequera> results = adapter.findAllByUserId(1L);

        assertThat(results).hasSize(2);
        verify(mapper, times(2)).toDomain(any(UsuarioChequeraClaseChequeraEntity.class));
    }

    @Test
    void findAllByUserId_whenNone_returnsEmptyList() {
        var results = adapter.findAllByUserId(999L);

        assertThat(results).isEmpty();
    }

}
