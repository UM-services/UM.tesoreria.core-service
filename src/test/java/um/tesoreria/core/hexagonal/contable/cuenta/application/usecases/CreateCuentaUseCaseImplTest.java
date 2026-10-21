package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCuentaUseCaseImplTest {
    @Mock CuentaRepository repository;
    @Mock CuentaHistorialPort historial;
    CreateCuentaUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        useCase = new CreateCuentaUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    static Cuenta cuenta(String numero) {
        return Cuenta.builder().numeroCuenta(new BigDecimal(numero)).nombre("Cuenta")
                .integradora((byte) 0).visible((byte) 1).build();
    }

    @Test
    void numeroInvalidoNoConsultaNiEscribe() {
        assertThatThrownBy(() -> useCase.createCuenta(cuenta("11000010001")))
                .isInstanceOf(CuentaConflictoException.class);
        verifyNoInteractions(repository, historial);
    }

    @Test
    void altaSinVisibleQuedaVisible() {
        Cuenta raiz = Cuenta.builder().numeroCuenta(new BigDecimal("10000000000")).nombre("Raíz").build();
        when(repository.findWithLockByNumeroCuenta(raiz.getNumeroCuenta())).thenReturn(Optional.empty());
        when(repository.create(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        assertThat(useCase.createCuenta(raiz).getVisible()).isEqualTo((byte) 1);
    }

    @Test
    void ancestroAusenteRechazaSinEscribir() {
        assertThatThrownBy(() -> useCase.createCuenta(cuenta("11000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("10000000000");
        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }

    @Test
    void duplicadaNoPisaCuentaNiRegistraHistorial() {
        Cuenta existente = cuenta("10000000000");
        when(repository.findWithLockByNumeroCuenta(existente.getNumeroCuenta())).thenReturn(Optional.of(existente));
        assertThatThrownBy(() -> useCase.createCuenta(existente))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("ya existe");
        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }

    @Test
    void ancestroConMovimientosRechazaSinCambiarIntegradoras() {
        Cuenta raiz = cuenta("10000000000");
        when(repository.findWithLockByNumeroCuenta(raiz.getNumeroCuenta())).thenReturn(Optional.of(raiz));
        when(repository.existenMovimientos(raiz.getNumeroCuenta(), raiz.getNumeroCuenta())).thenReturn(true);
        assertThatThrownBy(() -> useCase.createCuenta(cuenta("11000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("registraciones");
        verify(repository, never()).create(any());
        verify(repository, never()).update(any(), any());
        verifyNoInteractions(historial);
    }

    @Test
    void bloqueaRaizAPadreAntesDeLeerMovimientosYGuardaGradosCalculados() {
        Cuenta raiz = cuenta("10000000000");
        Cuenta padre = cuenta("11000000000");
        Cuenta nueva = cuenta("11010000000");
        nueva.setGeograficaId(0);
        nueva.setGrado(999);
        when(repository.findWithLockByNumeroCuenta(raiz.getNumeroCuenta())).thenReturn(Optional.of(raiz));
        when(repository.findWithLockByNumeroCuenta(padre.getNumeroCuenta())).thenReturn(Optional.of(padre));
        when(repository.create(any())).thenAnswer(i -> i.getArgument(0));
        when(repository.update(any(), any())).thenAnswer(i -> Optional.of(i.getArgument(1)));

        Cuenta creada = useCase.createCuenta(nueva);

        var orden = inOrder(repository, historial);
        orden.verify(repository).findWithLockByNumeroCuenta(raiz.getNumeroCuenta());
        orden.verify(repository).findWithLockByNumeroCuenta(padre.getNumeroCuenta());
        orden.verify(repository).findWithLockByNumeroCuenta(nueva.getNumeroCuenta());
        orden.verify(repository).existenMovimientos(raiz.getNumeroCuenta(), raiz.getNumeroCuenta());
        orden.verify(repository).existenMovimientos(padre.getNumeroCuenta(), padre.getNumeroCuenta());
        orden.verify(repository).create(nueva);
        orden.verify(historial).registrarAlta(creada);
        orden.verify(repository).update(eq(raiz.getNumeroCuenta()), argThat(c -> c.getIntegradora() == 1));
        orden.verify(historial).registrarEdicion(eq(raiz), argThat(c -> c.getIntegradora() == 1));
        orden.verify(repository).update(eq(padre.getNumeroCuenta()), argThat(c -> c.getIntegradora() == 1));
        orden.verify(historial).registrarEdicion(eq(padre), argThat(c -> c.getIntegradora() == 1));
        assertThat(creada.getGrado()).isEqualTo(3);
        assertThat(creada.getGrado1()).isEqualByComparingTo("10000000000");
        assertThat(creada.getGrado2()).isEqualByComparingTo("11000000000");
        assertThat(creada.getGrado3()).isEqualByComparingTo("11010000000");
        assertThat(creada.getGrado4()).isEqualByComparingTo("11010000000");
        assertThat(creada.getGeograficaId()).isNull();
        assertThat(raiz.getIntegradora()).isZero(); // la copia anterior del historial permanece intacta
    }

    @Test
    void integradoraExistenteNoGeneraEdicionRedundante() {
        Cuenta raiz = cuenta("10000000000");
        raiz.setIntegradora((byte) 1);
        when(repository.findWithLockByNumeroCuenta(raiz.getNumeroCuenta())).thenReturn(Optional.of(raiz));
        when(repository.create(any())).thenAnswer(i -> i.getArgument(0));
        useCase.createCuenta(cuenta("11000000000"));
        verify(repository, never()).update(any(), any());
        verify(historial, never()).registrarEdicion(any(), any());
    }

    @Test
    void duplicadoConcurrenteSeTraduceAConflicto() {
        when(repository.create(any())).thenThrow(new DataIntegrityViolationException("PK"));
        assertThatThrownBy(() -> useCase.createCuenta(cuenta("10000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("duplicado");
        verifyNoInteractions(historial);
    }

    @Test
    void carreraDeRaicesConBloqueoDeHuecoSeTraduceAConflictoSinEscrituras() {
        when(repository.findWithLockByNumeroCuenta(any()))
                .thenThrow(new org.springframework.dao.CannotAcquireLockException("Interbloqueo"));
        assertThatThrownBy(() -> useCase.createCuenta(cuenta("10000000000")))
                .isInstanceOf(CuentaConflictoException.class).hasMessageContaining("concurrente");
        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }
}
