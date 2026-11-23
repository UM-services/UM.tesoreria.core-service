package um.tesoreria.core.hexagonal.compras.correlativo;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaCorrelativoAnualTest {

    private static final Integer EJERCICIO = 15;

    @Mock
    SecuenciaAnualRepository<SecuenciaPrueba> repository;
    @Mock
    EntityManager entityManager;

    ReservaCorrelativoAnual reserva;

    @BeforeEach
    void preparar() {
        reserva = new ReservaCorrelativoAnual(entityManager, TransactionOperations.withoutTransaction());
    }

    @Test
    void incrementaElUltimoNumeroDeLaFilaExistente() {
        SecuenciaPrueba secuencia = new SecuenciaPrueba(EJERCICIO, 2026, 4);
        when(repository.existsById(EJERCICIO)).thenReturn(true);
        when(repository.findWithLockByEjercicioId(EJERCICIO)).thenReturn(Optional.of(secuencia));

        assertThat(reserva.reservar(repository, EJERCICIO, () -> new SecuenciaPrueba(EJERCICIO, 2026, 0)))
                .isEqualTo(5);
        assertThat(secuencia.getUltimoNumero()).isEqualTo(5);
        verify(entityManager, never()).persist(any());
    }

    @Test
    void creaLaFilaDelEjercicioCuandoNoExisteYArrancaEnUno() {
        SecuenciaPrueba secuencia = new SecuenciaPrueba(EJERCICIO, 2026, 0);
        when(repository.existsById(EJERCICIO)).thenReturn(false);
        when(repository.findWithLockByEjercicioId(EJERCICIO)).thenReturn(Optional.of(secuencia));

        assertThat(reserva.reservar(repository, EJERCICIO, () -> secuencia)).isEqualTo(1);
        verify(entityManager).persist(secuencia);
        verify(entityManager).flush();
    }

    @Test
    void siOtraTransaccionCreoLaFilaSigueConLaExistente() {
        when(repository.existsById(EJERCICIO)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("clave duplicada")).when(entityManager).flush();
        when(repository.findWithLockByEjercicioId(EJERCICIO))
                .thenReturn(Optional.of(new SecuenciaPrueba(EJERCICIO, 2026, 7)));

        assertThat(reserva.reservar(repository, EJERCICIO, () -> new SecuenciaPrueba(EJERCICIO, 2026, 0)))
                .isEqualTo(8);
    }

    @Test
    void sinFilaDespuesDeAsegurarFallaEnLugarDeInventarUnNumero() {
        when(repository.existsById(EJERCICIO)).thenReturn(true);
        when(repository.findWithLockByEjercicioId(EJERCICIO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reserva.reservar(repository, EJERCICIO, () -> new SecuenciaPrueba(EJERCICIO, 2026, 0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("correlativo");
    }

    @Test
    void siElAseguradoFallaYNoHayFilaPropagaLaCausa() {
        when(repository.existsById(EJERCICIO)).thenReturn(false);
        doThrow(new IllegalStateException("ejercicio inexistente")).when(entityManager).flush();
        when(repository.findWithLockByEjercicioId(EJERCICIO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reserva.reservar(repository, EJERCICIO, () -> new SecuenciaPrueba(EJERCICIO, 2026, 0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("correlativo")
                .hasRootCauseMessage("ejercicio inexistente");
    }

    private static final class SecuenciaPrueba implements SecuenciaAnual {

        private final Integer ejercicioId;
        private final Integer anio;
        private Integer ultimoNumero;

        private SecuenciaPrueba(Integer ejercicioId, Integer anio, Integer ultimoNumero) {
            this.ejercicioId = ejercicioId;
            this.anio = anio;
            this.ultimoNumero = ultimoNumero;
        }

        @Override
        public Integer getEjercicioId() {
            return ejercicioId;
        }

        @Override
        public Integer getUltimoNumero() {
            return ultimoNumero;
        }

        @Override
        public void setUltimoNumero(Integer ultimoNumero) {
            this.ultimoNumero = ultimoNumero;
        }

        Integer getAnio() {
            return anio;
        }
    }

}
