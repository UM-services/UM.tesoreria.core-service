package um.tesoreria.core.hexagonal.umhub.consulta.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetDeudaPersonaUseCase;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.VencimientoDto;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.TipoDocumentoConsulta;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaDeudaUseCase;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaPersonaUseCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Deuda agregada por numero de documento: recorre todos los tipos registrados bajo el
 * numero (mismo titular, resuelto por GetConsultaPersonaUseCase) y fusiona los
 * resultados del caso de uso existente de la slice personas sin modificar su firma.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GetConsultaDeudaUseCaseImpl implements GetConsultaDeudaUseCase {

    private final GetConsultaPersonaUseCase getConsultaPersonaUseCase;
    private final GetDeudaPersonaUseCase getDeudaPersonaUseCase;

    @Override
    public Optional<ConsultaDeuda> findByNumeroDocumento(BigDecimal numeroDocumento, boolean extended) {
        Optional<ConsultaPersona> persona = getConsultaPersonaUseCase.findByNumeroDocumento(numeroDocumento);
        if (persona.isEmpty()) {
            return Optional.empty();
        }

        int cuotas = 0;
        BigDecimal deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        List<VencimientoDto> vencimientos = new ArrayList<>();

        for (TipoDocumentoConsulta tipo : persona.get().getDocumentos()) {
            DeudaPersonaDto deudaTipo = extended
                    ? getDeudaPersonaUseCase.deudaByPersonaExtended(numeroDocumento, tipo.getDocumentoId())
                    : getDeudaPersonaUseCase.deudaByPersona(numeroDocumento, tipo.getDocumentoId());
            if (deudaTipo.getCuotas() != null) {
                cuotas += deudaTipo.getCuotas();
            }
            if (deudaTipo.getDeuda() != null) {
                deudaTotal = deudaTotal.add(deudaTipo.getDeuda());
            }
            if (deudaTipo.getDeudas() != null) {
                deudas.addAll(deudaTipo.getDeudas());
            }
            if (deudaTipo.getVencimientos() != null) {
                vencimientos.addAll(deudaTipo.getVencimientos());
            }
        }

        return Optional.of(ConsultaDeuda.builder()
                .numeroDocumento(numeroDocumento)
                .cuotas(cuotas)
                .deuda(deudaTotal.setScale(2, RoundingMode.HALF_UP))
                .deudas(deudas)
                .vencimientos(vencimientos)
                .build());
    }
}
