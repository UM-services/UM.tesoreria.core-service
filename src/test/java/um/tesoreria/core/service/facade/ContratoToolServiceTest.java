package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contratos.contrato.application.service.ContratoService;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.application.service.CursoCargoContratadoService;
import um.tesoreria.core.model.ContratoPeriodo;
import um.tesoreria.core.service.ContratoFacturaService;
import um.tesoreria.core.service.ContratoPeriodoService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratoToolServiceTest {

    @Mock
    private ContratoFacturaService contratoFacturaService;

    @Mock
    private ContratoPeriodoService contratoPeriodoService;

    @Mock
    private ContratoService contratoService;

    @Mock
    private CursoCargoContratadoService cursoCargoContratadoService;

    @InjectMocks
    private ContratoToolService service;

    @Test
    void resetMarcaTemporal_poneEnCeroLosPeriodosMarcados() {
        ContratoPeriodo primero = new ContratoPeriodo();
        primero.setContratoPeriodoId(1L);
        primero.setMarcaTemporal((byte) 1);
        ContratoPeriodo segundo = new ContratoPeriodo();
        segundo.setContratoPeriodoId(2L);
        segundo.setMarcaTemporal((byte) 1);
        when(contratoPeriodoService.findAllMarcadoByContrato(5L)).thenReturn(List.of(primero, segundo));

        Boolean result = service.resetMarcaTemporal(5L);

        assertThat(result).isTrue();
        assertThat(primero.getMarcaTemporal()).isZero();
        assertThat(segundo.getMarcaTemporal()).isZero();
        verify(contratoPeriodoService).saveAll(List.of(primero, segundo));
    }

    @Test
    void resetMarcaTemporal_sinPeriodosMarcados_noGuardaNada() {
        when(contratoPeriodoService.findAllMarcadoByContrato(5L)).thenReturn(List.of());

        Boolean result = service.resetMarcaTemporal(5L);

        assertThat(result).isTrue();
        verify(contratoPeriodoService, never()).saveAll(any());
    }
}
