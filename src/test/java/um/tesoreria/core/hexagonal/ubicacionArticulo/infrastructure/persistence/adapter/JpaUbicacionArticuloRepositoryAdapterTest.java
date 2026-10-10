package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Simula la fila que devuelve el bloqueo después de una reasignación; no prueba bloqueos de MySQL. */
class JpaUbicacionArticuloRepositoryAdapterTest {
    private final JpaUbicacionArticuloRepository repository = mock(JpaUbicacionArticuloRepository.class);
    private final EntityManager em = mock(EntityManager.class);
    private final UbicacionArticuloMapper mapper = mock(UbicacionArticuloMapper.class);
    private final Query bloqueo = mock(Query.class);
    private final UbicacionArticuloEntity fila = new UbicacionArticuloEntity();
    private final UbicacionArticulo pedido = UbicacionArticulo.builder()
            .ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("200")).build();
    private JpaUbicacionArticuloRepositoryAdapter adapter;

    @BeforeEach
    void preparar() {
        adapter = new JpaUbicacionArticuloRepositoryAdapter(repository, mapper, em);
        when(repository.findIdByUbicacionIdAndArticuloId(1, 2L)).thenReturn(Optional.of(9L));
        when(em.createNativeQuery(anyString(), eq(UbicacionArticuloEntity.class))).thenReturn(bloqueo);
        when(bloqueo.setParameter("id", 9L)).thenReturn(bloqueo);
        when(bloqueo.getResultList()).thenReturn(List.of(fila));
        fila.setUbicacionArticuloId(9L);
        fila.setUbicacionId(1);
        fila.setArticuloId(2L);
        fila.setNumeroCuenta(new BigDecimal("100"));
    }

    @ParameterizedTest
    @CsvSource({"1,3", "4,2", "4,3"})
    void parReasignadoMientrasEsperaba_noModificaLaCuentaYExigeOtraTransaccion(int ubicacion, long articulo) {
        fila.setUbicacionId(ubicacion);
        fila.setArticuloId(articulo);

        assertThatThrownBy(() -> adapter.save(pedido))
                .isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> {
                    assertThat(ex.isReintentable()).isTrue();
                    assertThat(ex.isBloqueado()).isFalse();
                });

        assertThat(fila.getNumeroCuenta()).isEqualByComparingTo("100");
        verify(em, never()).flush();
        verify(em, never()).persist(any());
        verifyNoInteractions(mapper);
    }

    @Test
    void parSinReasignar_actualizaYConservaElEstadoAnterior() {
        when(mapper.toDomainModel(fila)).thenReturn(pedido);

        var guardada = adapter.save(pedido);

        assertThat(guardada.anterior().getNumeroCuenta()).isEqualByComparingTo("100");
        assertThat(fila.getNumeroCuenta()).isEqualByComparingTo("200");
        assertThat(guardada.guardada()).isSameAs(pedido);
        verify(em).flush();
    }
}
