package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEjercicioUseCaseImplTest {
    @Mock EjercicioRepository repository;
    @Mock EjercicioHistorialPort historial;
    CreateEjercicioUseCaseImpl useCase;
    Ejercicio ultimo = Ejercicio.builder().ejercicioId(15).nombre("Ejercicio Económico 2026")
            .fechaInicio(LocalDate.parse("2026-03-01")).fechaFinal(LocalDate.parse("2027-02-28")).bloqueado((byte) 0)
            .ordenContableResultado(0).ordenContableBienesUso(0).build();

    @BeforeEach
    void preparar() {
        usarHoy("2026-10-21");
    }

    void usarHoy(String dia) {
        Clock clock = Clock.fixed(Instant.parse(dia + "T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        useCase = new CreateEjercicioUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction(), clock);
    }

    void hayUltimo(Ejercicio e) {
        when(repository.findLastWithLock()).thenReturn(Optional.of(e));
    }

    void creaTalCual() {
        when(repository.create(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void camposOmitidosSeDerivan() {
        hayUltimo(ultimo);
        creaTalCual();
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().build());
        assertThat(creado.getEjercicioId()).isEqualTo(16);
        assertThat(creado.getFechaInicio()).isEqualTo(LocalDate.parse("2027-03-01"));
        assertThat(creado.getFechaFinal()).isEqualTo(LocalDate.parse("2028-02-29"));
        assertThat(creado.getNombre()).isEqualTo("Ejercicio Económico 2027");
        verify(historial).registrarAlta(creado);
    }

    @Test
    void loEnviadoQueCoincideSeAceptaConOrdenesEnCero() {
        hayUltimo(ultimo);
        creaTalCual();
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().ejercicioId(16).nombre("Ejercicio Económico 2027")
                .fechaInicio(LocalDate.parse("2027-03-01")).fechaFinal(LocalDate.parse("2028-02-29")).bloqueado((byte) 0).build());
        assertThat(creado.getOrdenContableResultado()).isZero();
        assertThat(creado.getOrdenContableBienesUso()).isZero();
    }

    @Test
    void saltoSeRechazaSinEscribir() {
        hayUltimo(ultimo);
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(17).build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SALTO));
        verify(repository, never()).create(any());
        verifyNoInteractions(historial);
    }

    @Test
    void duplicadoSeRechazaSinEscribir() {
        hayUltimo(ultimo);
        for (int id : new int[]{15, 3}) {
            assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(id).build()))
                    .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
        }
        verify(repository, never()).create(any());
    }

    @Test
    void fechasQueNoCoincidenSeRechazan() {
        hayUltimo(ultimo);
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().fechaInicio(LocalDate.parse("2027-04-01")).build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SALTO));
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().fechaFinal(LocalDate.parse("2028-02-28")).build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().nombre("Otro").build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CAMPO_INMUTABLE));
        verify(repository, never()).create(any());
    }

    @Test
    void limiteConRelojControlado() {
        creaTalCual();
        // Con reloj 2026: el 16 (inicia 2027) y el 17 (2028) se crean; el 18 se rechaza.
        hayUltimo(ultimo);
        assertThat(useCase.createEjercicio(Ejercicio.builder().build()).getEjercicioId()).isEqualTo(16);
        hayUltimo(EjercicioSiguienteHelper.ejercicio(16, "2027-03-01", "2028-02-29"));
        assertThat(useCase.createEjercicio(Ejercicio.builder().build()).getEjercicioId()).isEqualTo(17);
        hayUltimo(EjercicioSiguienteHelper.ejercicio(17, "2028-03-01", "2029-02-28"));
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.LIMITE_ALTA));
        // Un año después el mismo ejercicio ya no supera el límite.
        usarHoy("2027-01-02");
        assertThat(useCase.createEjercicio(Ejercicio.builder().build()).getEjercicioId()).isEqualTo(18);
    }

    static org.springframework.dao.CannotAcquireLockException deadlock() {
        return new org.springframework.dao.CannotAcquireLockException("deadlock",
                new java.sql.SQLException("Deadlock found", "40001", 1213));
    }

    @Test
    void unLockWaitTimeoutNoSeReintenta() {
        var timeout = new org.springframework.dao.CannotAcquireLockException("timeout",
                new java.sql.SQLException("Lock wait timeout exceeded", "HY000", 1205));
        when(repository.findLastWithLock()).thenThrow(timeout);
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build())).isSameAs(timeout);
        verify(repository, times(1)).findLastWithLock();
    }

    @Test
    void ordenesFueraDeSmallintSonSolicitudInvalida() {
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ordenContableResultado(-1).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ordenContableBienesUso(32768).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verifyNoInteractions(repository, historial);
    }

    @Test
    void unUltimoQueNoTerminaEnFebreroNoDejaContinuarElPeriodo() {
        hayUltimo(EjercicioSiguienteHelper.ejercicio(15, "2026-03-01", "2027-06-30"));
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SALTO));
        verify(repository, never()).create(any());
    }

    @Test
    void tablaVaciaConFechasInvertidasEsSolicitudInvalida() {
        when(repository.findLastWithLock()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(1)
                .fechaInicio(LocalDate.parse("2012-03-01")).fechaFinal(LocalDate.parse("2011-02-28")).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
    }

    @Test
    void nombreEnBlancoEIdEnCeroDeVb6SeTomanComoOmitidos() {
        hayUltimo(ultimo);
        creaTalCual();
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().ejercicioId(0).nombre("  ").build());
        assertThat(creado.getEjercicioId()).isEqualTo(16);
        assertThat(creado.getNombre()).isEqualTo("Ejercicio Económico 2027");
    }

    @Test
    void bloqueadoYOrdenesEnviadosSeConservanEnElAlta() {
        hayUltimo(ultimo);
        creaTalCual();
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().bloqueado((byte) 1)
                .ordenContableResultado(5).ordenContableBienesUso(6).build());
        assertThat(creado.getBloqueado()).isEqualTo((byte) 1);
        assertThat(creado.getOrdenContableResultado()).isEqualTo(5);
        assertThat(creado.getOrdenContableBienesUso()).isEqualTo(6);
    }

    @Test
    void nombreDeMasDe128CaracteresEsSolicitudInvalida() {
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().nombre("x".repeat(129)).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void elPrimerEjercicioTieneQueSerRegularParaQueElAltaPuedaContinuar() {
        when(repository.findLastWithLock()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(1)
                .fechaInicio(LocalDate.parse("2026-01-01")).fechaFinal(LocalDate.parse("2026-12-31")).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(1)
                .fechaInicio(LocalDate.parse("2026-03-01")).fechaFinal(LocalDate.parse("2026-03-01")).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verify(repository, never()).create(any());
    }

    @Test
    void elPrimerEjercicioRegularConservaNombreYValoresEnviados() {
        when(repository.findLastWithLock()).thenReturn(Optional.empty());
        creaTalCual();
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().ejercicioId(1).nombre("Fundacional")
                .fechaInicio(LocalDate.parse("2011-03-01")).fechaFinal(LocalDate.parse("2012-02-29"))
                .bloqueado((byte) 1).ordenContableResultado(3).build());
        assertThat(creado.getNombre()).isEqualTo("Fundacional");
        assertThat(creado.getBloqueado()).isEqualTo((byte) 1);
        assertThat(creado.getOrdenContableResultado()).isEqualTo(3);
        assertThat(creado.getOrdenContableBienesUso()).isZero();
    }

    @Test
    void unDeadlockSeReintentaYLaRepeticionVeElEstadoConfirmado() {
        when(repository.findLastWithLock())
                .thenThrow(deadlock())
                .thenReturn(Optional.of(EjercicioSiguienteHelper.ejercicio(16, "2027-03-01", "2028-02-29")));
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().ejercicioId(16).build()))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.DUPLICADO));
        verify(repository, times(2)).findLastWithLock();
        verify(repository, never()).create(any());
    }

    @Test
    void unDeadlockPersistenteSePropagaTrasLosReintentos() {
        when(repository.findLastWithLock()).thenThrow(deadlock());
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build()))
                .isInstanceOf(org.springframework.dao.CannotAcquireLockException.class);
        verify(repository, times(3)).findLastWithLock();
    }

    @Test
    void tablaVaciaExigeIdYFechas() {
        when(repository.findLastWithLock()).thenReturn(Optional.empty());
        creaTalCual();
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        Ejercicio creado = useCase.createEjercicio(Ejercicio.builder().ejercicioId(1)
                .fechaInicio(LocalDate.parse("2011-03-01")).fechaFinal(LocalDate.parse("2012-02-29")).build());
        assertThat(creado.getNombre()).isEqualTo("Ejercicio Económico 2011");
    }

    @Test
    void bloqueadoFueraDeCeroYUnoEsSolicitudInvalida() {
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().bloqueado((byte) 2).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verifyNoInteractions(repository, historial);
    }

    @Test
    void siFallaElHistorialLaExcepcionPropagaParaRevertirLaTransaccion() {
        hayUltimo(ultimo);
        creaTalCual();
        doThrow(new IllegalStateException("historial")).when(historial).registrarAlta(any());
        assertThatThrownBy(() -> useCase.createEjercicio(Ejercicio.builder().build())).isInstanceOf(IllegalStateException.class);
    }
}
