package um.tesoreria.core.hexagonal.chequera.envio.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetChequeraEnvioAltasUseCaseImplTest {

    @Mock
    private ChequeraEnvioAltaReader reader;

    @InjectMocks
    private GetChequeraEnvioAltasUseCaseImpl useCase;

    @Test
    void delegaElPadronConLosMismosFiltrosDelLegacy() {
        var esperada = new ChequeraEnvioAlta(1L, 2, 520, 446L, 1, new BigDecimal("1001"), 1, 37, 1,
                "ABALLAY MURUA", "Luna Martina", "Matrícula y Arancel", "Ciclo Completo",
                "a@b.com", null, true, false, false, false);
        when(reader.findAllByLectivoIdAndFacultadIdAndGeograficaId(37, 2, 1)).thenReturn(List.of(esperada));

        var resultado = useCase.getAltas(37, 2, 1);

        assertThat(resultado).containsExactly(esperada);
        verify(reader).findAllByLectivoIdAndFacultadIdAndGeograficaId(37, 2, 1);
    }
}
