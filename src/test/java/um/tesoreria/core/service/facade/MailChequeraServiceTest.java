package um.tesoreria.core.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.service.CorreoEventPublisher;

/**
 * El envío de una cuota se resuelve por ID: core traduce la clave compuesta que manda el VB6
 * (facultad/tipo/serie/alternativa/producto/cuota) al {@code chequeraCuotaId} que consume el sender.
 */
@ExtendWith(MockitoExtension.class)
class MailChequeraServiceTest {

    @Mock
    private ChequeraCuotaService chequeraCuotaService;

    @Mock
    private CorreoEventPublisher correoEventPublisher;

    @InjectMocks
    private MailChequeraService service;

    @Test
    void sendCuotaResuelveElIdYPublicaElEvento() {
        var cuota = mock(ChequeraCuota.class);
        when(cuota.getChequeraCuotaId()).thenReturn(9L);
        when(chequeraCuotaService.findByUnique(1, 2, 3L, 4, 1, 1)).thenReturn(cuota);

        String mensaje = service.sendCuota(1, 2, 3L, 1, 4, 1, false, true);

        assertThat(mensaje).isEqualTo(MailChequeraService.MENSAJE_ENVIO);
        verify(correoEventPublisher).publicarCuota(9L);
    }

}
