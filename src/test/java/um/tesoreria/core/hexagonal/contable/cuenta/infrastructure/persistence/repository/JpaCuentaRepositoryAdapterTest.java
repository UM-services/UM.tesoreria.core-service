package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
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

    static final Sort ORG = Sort.by("numeroCuenta").ascending();
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
        when(jpa.findAll(ORG)).thenReturn(List.of(entity));
        adapter.recalcularGrados();
        assertThat(entity.getGrado()).isEqualTo(4);
        assertThat(entity.getGrado3()).isEqualByComparingTo("11010000000");
        assertThat(entity.getNombre()).isEqualTo("Conservar");
        assertThat(entity.getGeograficaId()).isEqualTo(2);
        verify(jpa, never()).saveAll(any());
        verify(jpa, never()).save(any());
        verify(jpa).flush();
    }

    // protects=el resumen cuenta sólo las cuentas cuyos grados cambian (sin importar la escala) y las de nombre con \r\n; fails_when=cuenta todas las filas o una diferencia de escala como cambio; no_claim=no prueba el UPDATE emitido
    @Test
    void recalculoYLimpiezaDevuelvenCuantasCuentasCambian() {
        CuentaEntity consistente = CuentaEntity.builder().numeroCuenta(new BigDecimal("11010100000")).grado(4)
                .grado1(new BigDecimal("10000000000.00")).grado2(new BigDecimal("11000000000"))
                .grado3(new BigDecimal("11010000000")).grado4(new BigDecimal("11010100000")).build();
        CuentaEntity desactualizada = CuentaEntity.builder().numeroCuenta(new BigDecimal("11010100001")).grado(4).build();
        CuentaEntity legada = CuentaEntity.builder().numeroCuenta(new BigDecimal("123")).grado(5).build();
        when(jpa.findAll(ORG)).thenReturn(List.of(consistente, desactualizada, legada));
        CuentaEntity sucia = CuentaEntity.builder().numeroCuenta(BigDecimal.ONE).nombre("Caja\r\n").build();
        when(jpa.findAllByNombreContainingOrderByNumeroCuenta("\r\n")).thenReturn(List.of(sucia));

        assertThat(adapter.recalcularGrados()).isEqualTo(1);
        assertThat(adapter.limpiarNombres()).isEqualTo(1);
        assertThat(desactualizada.getGrado()).isEqualTo(5);
        assertThat(legada.getGrado()).isEqualTo(5);
        assertThat(sucia.getNombre()).isEqualTo("Caja");
    }
}
