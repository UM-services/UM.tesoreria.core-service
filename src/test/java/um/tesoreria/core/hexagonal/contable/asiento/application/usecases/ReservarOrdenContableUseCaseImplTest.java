package um.tesoreria.core.hexagonal.contable.asiento.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoSecuenciaException;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoSecuencia;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoOrdenMaximoRepository;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoSecuenciaRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservarOrdenContableUseCaseImplTest {

    private static final OffsetDateTime FECHA =
            OffsetDateTime.of(2026, 11, 13, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock AsientoSecuenciaRepository secuenciaRepository;
    @Mock AsientoOrdenMaximoRepository maximoRepository;

    ReservarOrdenContableUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        useCase = new ReservarOrdenContableUseCaseImpl(secuenciaRepository, maximoRepository,
                TransactionOperations.withoutTransaction());
        lenient().when(secuenciaRepository.save(any()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void siembraConElMaximoCuandoLaFechaNoTieneSecuencia() {
        when(secuenciaRepository.findWithLockByFecha(FECHA)).thenReturn(Optional.empty());
        when(maximoRepository.maxOrden(FECHA)).thenReturn(10);

        assertThat(useCase.reservar(FECHA)).isEqualTo(11);

        verify(secuenciaRepository).save(new AsientoSecuencia(FECHA, 10));
        verify(secuenciaRepository).save(new AsientoSecuencia(FECHA, 11));
    }

    @Test
    void incrementaSobreLaSecuenciaExistente() {
        when(secuenciaRepository.findWithLockByFecha(FECHA))
                .thenReturn(Optional.of(new AsientoSecuencia(FECHA, 7)));

        assertThat(useCase.reservar(FECHA)).isEqualTo(8);

        verify(secuenciaRepository).save(new AsientoSecuencia(FECHA, 8));
        verifyNoInteractions(maximoRepository);
    }

    @Test
    void reintentaCuandoOtraTransaccionCreoLaFila() {
        when(secuenciaRepository.findWithLockByFecha(FECHA))
                .thenThrow(new DataIntegrityViolationException("secuencia duplicada"))
                .thenReturn(Optional.of(new AsientoSecuencia(FECHA, 5)));

        assertThat(useCase.reservar(FECHA)).isEqualTo(6);

        verify(secuenciaRepository, times(2)).findWithLockByFecha(FECHA);
    }

    @Test
    void normalizaLaFechaContableAAntesDeReservar() {
        OffsetDateTime conOffset = OffsetDateTime.of(2026, 11, 13, 15, 0, 0, 0, ZoneOffset.ofHours(-3));
        when(secuenciaRepository.findWithLockByFecha(FECHA)).thenReturn(Optional.empty());
        when(maximoRepository.maxOrden(FECHA)).thenReturn(0);

        useCase.reservar(conOffset);

        verify(secuenciaRepository).findWithLockByFecha(FECHA);
        verify(secuenciaRepository, never()).findWithLockByFecha(conOffset);
    }

    @Test
    void agotaLosReintentosYFalla() {
        when(secuenciaRepository.findWithLockByFecha(FECHA))
                .thenThrow(new DataIntegrityViolationException("secuencia duplicada"));

        assertThatThrownBy(() -> useCase.reservar(FECHA))
                .isInstanceOf(AsientoSecuenciaException.class)
                .hasMessageContaining("2026-11-13");

        verify(secuenciaRepository, times(5)).findWithLockByFecha(FECHA);
    }

}
