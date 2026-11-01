package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper.UbicacionArticuloMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository.JpaUbicacionArticuloRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Simula las filas que devuelven los bloqueos (reasignación, baja de vínculos); no prueba bloqueos de MySQL. */
class JpaUbicacionArticuloRepositoryAdapterTest {
    private final JpaUbicacionArticuloRepository repository = mock(JpaUbicacionArticuloRepository.class);
    private final EntityManager em = mock(EntityManager.class);
    private final UbicacionArticuloMapper mapper = mock(UbicacionArticuloMapper.class);
    private final UbicacionArticuloEntity fila = new UbicacionArticuloEntity();
    private final UbicacionArticulo pedido = UbicacionArticulo.builder()
            .ubicacionId(1).articuloId(2L).numeroCuenta(new BigDecimal("200")).build();
    private JpaUbicacionArticuloRepositoryAdapter adapter;

    @BeforeEach
    void preparar() {
        adapter = new JpaUbicacionArticuloRepositoryAdapter(repository, mapper, em, new SQLErrorCodeSQLExceptionTranslator("MySQL"));
        when(repository.findIdByUbicacionIdAndArticuloId(1, 2L)).thenReturn(Optional.of(() -> 9L));
        when(repository.findWithLockByUbicacionArticuloId(9L)).thenReturn(Optional.of(fila));
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
    void vinculoBorradoEntreLaBusquedaYElBloqueo_conflictoReintentableSinEscribir() {
        when(repository.findWithLockByUbicacionArticuloId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(pedido))
                .isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> assertThat(ex.isReintentable()).isTrue());

        verify(em, never()).flush();
        verify(em, never()).persist(any());
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

    @Test
    void bajaDeVinculos_copiaCadaCampoDeLaFilaBloqueada_yBorraExactamenteEsosIds() {
        when(repository.findWithLockByArticuloIdOrderByUbicacionArticuloId(2L))
                .thenReturn(List.of(escalares(9L, 1, 2L, "100"), escalares(11L, 3, 2L, null)));

        var borrados = adapter.deleteAllByArticuloId(2L);

        assertThat(borrados).extracting(UbicacionArticulo::getUbicacionArticuloId, UbicacionArticulo::getUbicacionId,
                        UbicacionArticulo::getArticuloId, UbicacionArticulo::getNumeroCuenta)
                .containsExactly(tuple(9L, 1, 2L, new BigDecimal("100")), tuple(11L, 3, 2L, null));
        verify(repository).deleteAllById(List.of(9L, 11L));
    }

    @Test
    void bajaSinVinculos_noBorraNada() {
        when(repository.findWithLockByArticuloIdOrderByUbicacionArticuloId(2L)).thenReturn(List.of());

        assertThat(adapter.deleteAllByArticuloId(2L)).isEmpty();
        verify(repository, never()).deleteAllById(any());
    }

    @Test
    void bajaDeVinculos_interbloqueoAlBloquear_conflictoReintentable() {
        // Como llega de Spring Data: Hibernate envuelve el 1213 del driver
        when(repository.findWithLockByArticuloIdOrderByUbicacionArticuloId(2L)).thenThrow(new CannotAcquireLockException("x",
                new LockAcquisitionException("x", new SQLException("Deadlock found when trying to get lock", "40001", 1213))));

        assertThatThrownBy(() -> adapter.deleteAllByArticuloId(2L))
                .isInstanceOfSatisfying(UbicacionArticuloConflictException.class, ex -> {
                    assertThat(ex.isReintentable()).isTrue();
                    assertThat(ex.isBloqueado()).isFalse();
                });
        verify(repository, never()).deleteAllById(any());
    }

    private static JpaUbicacionArticuloRepository.Escalares escalares(Long id, Integer ubicacionId, Long articuloId, String cuenta) {
        return new JpaUbicacionArticuloRepository.Escalares() {
            @Override public Long getUbicacionArticuloId() { return id; }
            @Override public Integer getUbicacionId() { return ubicacionId; }
            @Override public Long getArticuloId() { return articuloId; }
            @Override public BigDecimal getNumeroCuenta() { return cuenta == null ? null : new BigDecimal(cuenta); }
        };
    }
}
