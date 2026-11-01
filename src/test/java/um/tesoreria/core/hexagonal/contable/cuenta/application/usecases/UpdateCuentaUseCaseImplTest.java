package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
class UpdateCuentaUseCaseImplTest {
    @Mock CuentaRepository repository;
    @Mock CuentaHistorialPort historial;
    UpdateCuentaUseCaseImpl useCase;
    Cuenta anterior;

    @BeforeEach
    void preparar() {
        useCase = new UpdateCuentaUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
        anterior = Cuenta.builder().numeroCuenta(new BigDecimal("11010100001")).nombre("Original")
                .integradora((byte) 0).visible((byte) 1).geograficaId(2).cuentaContableId(77L)
                .fechaBloqueo(OffsetDateTime.parse("2026-01-01T00:00:00Z")).build();
    }

    void existe() {
        when(repository.findWithLockByNumeroCuenta(anterior.getNumeroCuenta())).thenReturn(Optional.of(anterior));
        when(repository.update(any(), any())).thenAnswer(i -> Optional.of(i.getArgument(1)));
    }

    @Test
    void soloNombreConservaOmitidosEIdentidadYRecalculaGrados() {
        existe();
        Cuenta cambios = Cuenta.builder().nombre("Nuevo").numeroCuenta(BigDecimal.ONE)
                .cuentaContableId(9L).grado(123).grado1(BigDecimal.ONE).build();
        Cuenta guardada = useCase.updateCuenta(anterior.getNumeroCuenta(), cambios).orElseThrow();
        assertThat(guardada.getNombre()).isEqualTo("Nuevo");
        assertThat(guardada.getNumeroCuenta()).isEqualTo(anterior.getNumeroCuenta());
        assertThat(guardada.getCuentaContableId()).isEqualTo(77L);
        assertThat(guardada.getGeograficaId()).isEqualTo(2);
        assertThat(guardada.getVisible()).isEqualTo((byte) 1);
        assertThat(guardada.getIntegradora()).isZero();
        assertThat(guardada.getFechaBloqueo()).isEqualTo(anterior.getFechaBloqueo());
        assertThat(guardada.getGrado()).isEqualTo(5);
        assertThat(guardada.getGrado1()).isEqualByComparingTo("10000000000");
        assertThat(guardada.getGrado2()).isEqualByComparingTo("11000000000");
        assertThat(guardada.getGrado3()).isEqualByComparingTo("11010000000");
        assertThat(guardada.getGrado4()).isEqualByComparingTo("11010100000");
        verify(repository, never()).existenMovimientos(any(), any());
        verify(historial).registrarEdicion(anterior, guardada);
    }

    @Test
    void sedeCeroSeBorraYFlagsCeroSeAplican() {
        existe();
        Cuenta guardada = useCase.updateCuenta(anterior.getNumeroCuenta(), Cuenta.builder()
                .geograficaId(0).visible((byte) 0).integradora((byte) 0).build()).orElseThrow();
        assertThat(guardada.getGeograficaId()).isNull();
        assertThat(guardada.getVisible()).isZero();
        assertThat(guardada.getNombre()).isEqualTo("Original");
    }

    @Test
    void conHijasSiempreQuedaIntegradora() {
        existe();
        when(repository.existenHijas(any(), any())).thenReturn(true);
        Cuenta guardada = useCase.updateCuenta(anterior.getNumeroCuenta(), Cuenta.builder().integradora((byte) 0).build()).orElseThrow();
        assertThat(guardada.getIntegradora()).isEqualTo((byte) 1);
    }

    @Test
    void vacioRechazadoSinEscrituras() {
        when(repository.findWithLockByNumeroCuenta(anterior.getNumeroCuenta())).thenReturn(Optional.of(anterior));
        assertThatThrownBy(() -> useCase.updateCuenta(anterior.getNumeroCuenta(), Cuenta.builder().nombre("  ").build()))
                .isInstanceOf(CuentaConflictoException.class);
        verify(repository, never()).update(any(), any());
        verifyNoInteractions(historial);
    }

    @Test
    void inexistenteNoSeCrea() {
        assertThat(useCase.updateCuenta(anterior.getNumeroCuenta(), Cuenta.builder().build())).isEmpty();
        verify(repository, never()).update(any(), any());
        verifyNoInteractions(historial);
    }
}
