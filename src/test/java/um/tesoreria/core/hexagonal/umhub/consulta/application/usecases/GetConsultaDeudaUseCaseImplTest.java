package um.tesoreria.core.hexagonal.umhub.consulta.application.usecases;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetDeudaPersonaUseCase;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.VencimientoDto;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.TipoDocumentoConsulta;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaPersonaUseCase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetConsultaDeudaUseCaseImplTest {

    private static final BigDecimal NUMERO = new BigDecimal("30123456");

    private final GetConsultaPersonaUseCase personaUseCase = mock(GetConsultaPersonaUseCase.class);
    private final GetDeudaPersonaUseCase deudaUseCase = mock(GetDeudaPersonaUseCase.class);

    private final GetConsultaDeudaUseCaseImpl useCase =
            new GetConsultaDeudaUseCaseImpl(personaUseCase, deudaUseCase);

    private ConsultaPersona personaConTipos(Integer... documentoIds) {
        List<TipoDocumentoConsulta> tipos = new ArrayList<>();
        for (Integer documentoId : documentoIds) {
            tipos.add(TipoDocumentoConsulta.builder().documentoId(documentoId).build());
        }
        return ConsultaPersona.builder().numeroDocumento(NUMERO).documentos(tipos).build();
    }

    private DeudaPersonaDto deuda(Integer cuotas, BigDecimal monto, int series) {
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        for (int i = 0; i < series; i++) {
            DeudaChequeraDto dto = new DeudaChequeraDto();
            dto.setDeuda(monto);
            deudas.add(dto);
        }
        return DeudaPersonaDto.builder()
                .cuotas(cuotas)
                .deuda(monto)
                .deudas(deudas)
                .vencimientos(new ArrayList<>())
                .build();
    }

    @Test
    void agregaDeudaDeTodosLosTiposDelMismoNumero() {
        when(personaUseCase.findByNumeroDocumento(NUMERO))
                .thenReturn(Optional.of(personaConTipos(1, 8)));
        when(deudaUseCase.deudaByPersona(NUMERO, 1)).thenReturn(deuda(2, new BigDecimal("100.00"), 1));
        when(deudaUseCase.deudaByPersona(NUMERO, 8)).thenReturn(deuda(3, new BigDecimal("50.50"), 2));

        Optional<ConsultaDeuda> result = useCase.findByNumeroDocumento(NUMERO, false);

        assertThat(result).isPresent();
        assertThat(result.get().getCuotas()).isEqualTo(5);
        assertThat(result.get().getDeuda()).isEqualByComparingTo("150.50");
        assertThat(result.get().getDeudas()).hasSize(3);
        verify(deudaUseCase, never()).deudaByPersonaExtended(NUMERO, 1);
    }

    @Test
    void modoExtendidoUsaElCalculoExtendidoYConservaVencimientos() {
        when(personaUseCase.findByNumeroDocumento(NUMERO))
                .thenReturn(Optional.of(personaConTipos(8)));
        VencimientoDto vencimiento = new VencimientoDto();
        vencimiento.setInitPoint("https://mercadopago/checkout");
        DeudaPersonaDto base = deuda(1, new BigDecimal("10.00"), 1);
        base.setVencimientos(List.of(vencimiento));
        when(deudaUseCase.deudaByPersonaExtended(NUMERO, 8)).thenReturn(base);

        Optional<ConsultaDeuda> result = useCase.findByNumeroDocumento(NUMERO, true);

        assertThat(result).isPresent();
        assertThat(result.get().getVencimientos()).singleElement()
                .satisfies(v -> assertThat(v.getInitPoint()).isEqualTo("https://mercadopago/checkout"));
    }

    @Test
    void devuelveVacioCuandoLaPersonaNoExiste() {
        when(personaUseCase.findByNumeroDocumento(NUMERO)).thenReturn(Optional.empty());

        assertThat(useCase.findByNumeroDocumento(NUMERO, false)).isEmpty();
        verify(deudaUseCase, never()).deudaByPersona(NUMERO, 8);
    }
}
