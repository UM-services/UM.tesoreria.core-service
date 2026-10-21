package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception.ReservaVacanteException;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Claim atómico de la notificación de pago: sólo la primera entrega notifica, el reintento de
 * Kafka es un no-op y un envío fallido libera la marca.
 */
@ExtendWith(MockitoExtension.class)
class ReclamarNotificacionPagoUseCaseImplTest {

    private static final UUID RESERVA_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Mock
    private ReservaVacanteRepository reservaVacanteRepository;

    @InjectMocks
    private ReclamarNotificacionPagoUseCaseImpl useCase;

    @Test
    void laPrimeraEntregaReclamaLaNotificacion() {
        when(reservaVacanteRepository.findWithLockByReservaVacanteId(RESERVA_ID))
                .thenReturn(Optional.of(reserva(false)));

        assertThat(useCase.reclamar(RESERVA_ID)).isTrue();

        verify(reservaVacanteRepository).updateNotificacionPago(
                argThat(reserva -> Boolean.TRUE.equals(reserva.getNotificacionPago())), eq(RESERVA_ID));
    }

    @Test
    void elReintentoNoVuelveAReclamar() {
        when(reservaVacanteRepository.findWithLockByReservaVacanteId(RESERVA_ID))
                .thenReturn(Optional.of(reserva(true)));

        assertThat(useCase.reclamar(RESERVA_ID)).isFalse();

        verify(reservaVacanteRepository, never()).updateNotificacionPago(any(), any());
    }

    @Test
    void liberarDevuelveLaMarcaACero() {
        when(reservaVacanteRepository.findWithLockByReservaVacanteId(RESERVA_ID))
                .thenReturn(Optional.of(reserva(true)));

        useCase.liberar(RESERVA_ID);

        verify(reservaVacanteRepository).updateNotificacionPago(
                argThat(reserva -> Boolean.FALSE.equals(reserva.getNotificacionPago())), eq(RESERVA_ID));
    }

    @Test
    void unaReservaInesxistenteFalla() {
        when(reservaVacanteRepository.findWithLockByReservaVacanteId(RESERVA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.reclamar(RESERVA_ID))
                .isInstanceOf(ReservaVacanteException.class);

        verify(reservaVacanteRepository, never()).updateNotificacionPago(any(), any());
    }

    private ReservaVacante reserva(boolean notificada) {
        return ReservaVacante.builder()
                .reservaVacanteId(RESERVA_ID)
                .estado("pagado")
                .notificacionPago(notificada)
                .build();
    }

}
