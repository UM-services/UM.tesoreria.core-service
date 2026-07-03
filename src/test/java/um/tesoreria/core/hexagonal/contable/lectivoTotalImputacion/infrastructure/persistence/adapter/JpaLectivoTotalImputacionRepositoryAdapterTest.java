package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.mapper.LectivoTotalImputacionMapper;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.repository.JpaLectivoTotalImputacionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaLectivoTotalImputacionRepositoryAdapter.class, JpaLectivoTotalImputacionRepositoryAdapterTest.TestAuditingConfig.class})
class JpaLectivoTotalImputacionRepositoryAdapterTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaLectivoTotalImputacionRepositoryAdapter adapter;

    @Autowired
    private JpaLectivoTotalImputacionRepository jpaRepository;

    @MockitoBean
    private LectivoTotalImputacionMapper mapper;

    @Test
    void add_savesAndMaps() {
        var domain = LectivoTotalImputacion.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        var entityWithoutId = LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        var expectedDomain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        when(mapper.toEntity(domain)).thenReturn(entityWithoutId);
        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class))).thenReturn(expectedDomain);

        var result = adapter.add(domain);

        assertThat(result).isEqualTo(expectedDomain);
        verify(mapper).toEntity(domain);
        verify(mapper).toDomain(any(LectivoTotalImputacionEntity.class));
    }

    @Test
    void findById_whenFound_mapsAndReturns() {
        var saved = jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build());

        var expected = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(saved.getLectivoTotalImputacionId())
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build();

        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class))).thenReturn(expected);

        Optional<LectivoTotalImputacion> result = adapter.findById(saved.getLectivoTotalImputacionId());

        assertThat(result).contains(expected);
    }

    @Test
    void findById_whenNotFound_returnsEmpty() {
        var result = adapter.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByTipo_returnsMappedList() {
        jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("10.00")).build());
        jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(5)
                .numeroCuenta(new BigDecimal("20.00")).build());

        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class)))
                .thenReturn(LectivoTotalImputacion.builder().build())
                .thenReturn(LectivoTotalImputacion.builder().build());

        var results = adapter.findAllByTipo(1, 2, 3);

        assertThat(results).hasSize(2);
    }

    @Test
    void findAllByLectivo_returnsMappedList() {
        jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("10.00")).build());

        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class)))
                .thenReturn(LectivoTotalImputacion.builder().build());

        var results = adapter.findAllByLectivo(2);

        assertThat(results).hasSize(1);
    }

    @Test
    void findByProducto_whenFound_mapsAndReturns() {
        jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("10.00")).build());

        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class)))
                .thenReturn(LectivoTotalImputacion.builder().lectivoTotalImputacionId(1L).build());

        var result = adapter.findByProducto(1, 2, 3, 4);

        assertThat(result).isPresent();
    }

    @Test
    void findByProducto_whenNotFound_returnsEmpty() {
        var result = adapter.findByProducto(1, 2, 3, 999);

        assertThat(result).isEmpty();
    }

    @Test
    void update_whenFound_updatesAndMaps() {
        var saved = jpaRepository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("10.00")).build());
        jpaRepository.flush();
        var id = saved.getLectivoTotalImputacionId();

        var updateDomain = LectivoTotalImputacion.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("99.99")).build();

        var updateEntity = LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("99.99")).build();

        when(mapper.toEntity(updateDomain)).thenReturn(updateEntity);
        when(mapper.toDomain(any(LectivoTotalImputacionEntity.class)))
                .thenReturn(LectivoTotalImputacion.builder().lectivoTotalImputacionId(id).build());

        var result = adapter.update(id, updateDomain);

        assertThat(result).isPresent();
    }

    @Test
    void update_whenNotFound_returnsEmpty() {
        var domain = LectivoTotalImputacion.builder().build();

        var result = adapter.update(999L, domain);

        assertThat(result).isEmpty();
    }

}
