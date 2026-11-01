package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.service.view.CuentaSearchService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaCuentaRepositoryAdapterTest {
    @Mock JpaCuentaRepository jpa;
    @Mock CuentaMovimientoReferenciaRepository movimientos;
    @Mock CuentaSearchService search;
    @Mock EntityManager em;
    JpaCuentaRepositoryAdapter adapter;

    @BeforeEach
    void preparar() {
        adapter = new JpaCuentaRepositoryAdapter(jpa, search, new CuentaMapper(), movimientos, em);
    }

    @Test
    void altaUsaPersistYFlushNuncaMerge() {
        adapter.create(Cuenta.builder().numeroCuenta(new BigDecimal("10000000000")).build());
        verify(em).persist(any(CuentaEntity.class));
        verify(em).flush();
        verifyNoInteractions(jpa);
    }

    @Test
    void fkEnFlushSeTraduceAConflicto() {
        doThrow(new DataIntegrityViolationException("FK")).when(jpa).flush();
        assertThatThrownBy(() -> adapter.deleteById(BigDecimal.ONE)).isInstanceOf(CuentaConflictoException.class);
    }

    @Test
    void edicionConservaIdentidadYAuditoriaDeLaEntidadGestionada() {
        CuentaEntity entity = CuentaEntity.builder().numeroCuenta(BigDecimal.ONE).cuentaContableId(77L).build();
        entity.setCreated(java.time.LocalDateTime.of(2026, 1, 1, 0, 0));
        when(jpa.findByNumeroCuenta(BigDecimal.ONE)).thenReturn(Optional.of(entity));
        Cuenta nueva = Cuenta.builder().numeroCuenta(BigDecimal.TEN).cuentaContableId(99L).nombre("Nuevo").build();
        Cuenta resultado = adapter.update(BigDecimal.ONE, nueva).orElseThrow();
        assertThat(resultado.getNumeroCuenta()).isEqualTo(BigDecimal.ONE);
        assertThat(resultado.getCuentaContableId()).isEqualTo(77L);
        assertThat(entity.getCreated()).isEqualTo(java.time.LocalDateTime.of(2026, 1, 1, 0, 0));
        verify(jpa, never()).save(any());
    }

    @Test
    void recalculoModificaSoloGradosYNoUsaSave() {
        CuentaEntity entity = CuentaEntity.builder().numeroCuenta(new BigDecimal("11010100000"))
                .nombre("Conservar").visible((byte) 1).geograficaId(2).build();
        when(jpa.findAll()).thenReturn(List.of(entity));
        adapter.recalcularGrados();
        assertThat(entity.getGrado()).isEqualTo(4);
        assertThat(entity.getGrado3()).isEqualByComparingTo("11010000000");
        assertThat(entity.getNombre()).isEqualTo("Conservar");
        assertThat(entity.getGeograficaId()).isEqualTo(2);
        verify(jpa, never()).saveAll(any());
        verify(jpa, never()).save(any());
        verify(jpa).flush();
    }
}
