package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCuentaUseCaseImplTest {
    @Mock CuentaRepository repository;
    @Mock CuentaHistorialPort historial;
    DeleteCuentaUseCaseImpl useCase;
    BigDecimal numero = new BigDecimal("11010100000");
    BigDecimal hasta = new BigDecimal("11010109999");

    @BeforeEach
    void preparar() {
        useCase = new DeleteCuentaUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    Cuenta existe() {
        Cuenta cuenta = Cuenta.builder().numeroCuenta(numero).build();
        when(repository.findWithLockByNumeroCuenta(numero)).thenReturn(Optional.of(cuenta));
        return cuenta;
    }

    @Test
    void inexistenteEsIdempotente() {
        useCase.deleteCuenta(numero);
        verify(repository, never()).deleteById(any());
        verifyNoInteractions(historial);
    }

    @Test
    void hijasSinMovimientosTambienImpidenBaja() {
        existe();
        when(repository.existenHijas(numero, hasta)).thenReturn(true);
        assertThatThrownBy(() -> useCase.deleteCuenta(numero)).isInstanceOf(CuentaConflictoException.class).hasMessageContaining("hijas");
        verify(repository, never()).deleteById(any());
        verifyNoInteractions(historial);
    }

    @Test
    void movimientoEnCualquierNumeroDelRangoImpideBaja() {
        existe();
        when(repository.existenMovimientos(numero, hasta)).thenReturn(true);
        assertThatThrownBy(() -> useCase.deleteCuenta(numero)).isInstanceOf(CuentaConflictoException.class).hasMessageContaining("registraciones");
        verify(repository, never()).deleteById(any());
        verifyNoInteractions(historial);
    }

    @Test
    void libreBloqueaAntesDeConsultarYRegistraAntesDeBorrar() {
        Cuenta cuenta = existe();
        useCase.deleteCuenta(numero);
        var orden = inOrder(repository, historial);
        orden.verify(repository).findWithLockByNumeroCuenta(numero);
        orden.verify(repository).existenHijas(numero, hasta);
        orden.verify(repository).existenMovimientos(numero, hasta);
        orden.verify(historial).registrarBaja(cuenta);
        orden.verify(repository).deleteById(numero);
    }
}
