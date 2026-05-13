package um.tesoreria.core.hexagonal.contrato.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.infrastructure.web.dto.ContratoRequest;
import um.tesoreria.core.hexagonal.contrato.infrastructure.web.dto.ContratoResponse;

@Component
public class ContratoDtoMapper {

    public Contrato toDomain(ContratoRequest request) {
        if (request == null) {
            return null;
        }
        return Contrato.builder()
                .personaId(request.getPersonaId())
                .documentoId(request.getDocumentoId())
                .desde(request.getDesde())
                .facultadId(request.getFacultadId())
                .planId(request.getPlanId())
                .materiaId(request.getMateriaId())
                .geograficaId(request.getGeograficaId())
                .cargoMateriaId(request.getCargoMateriaId())
                .primerVencimiento(request.getPrimerVencimiento())
                .cargo(request.getCargo())
                .montoFijo(request.getMontoFijo())
                .canonMensual(request.getCanonMensual())
                .canonMensualSinAjuste(request.getCanonMensualSinAjuste())
                .hasta(request.getHasta())
                .canonMensualLetras(request.getCanonMensualLetras())
                .canonTotal(request.getCanonTotal())
                .canonTotalLetras(request.getCanonTotalLetras())
                .meses(request.getMeses())
                .mesesLetras(request.getMesesLetras())
                .ajuste(request.getAjuste())
                .build();
    }

    public ContratoResponse toResponse(Contrato domain) {
        if (domain == null) {
            return null;
        }
        return ContratoResponse.builder()
                .contratoId(domain.getContratoId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .desde(domain.getDesde())
                .facultadId(domain.getFacultadId())
                .planId(domain.getPlanId())
                .materiaId(domain.getMateriaId())
                .geograficaId(domain.getGeograficaId())
                .cargoMateriaId(domain.getCargoMateriaId())
                .primerVencimiento(domain.getPrimerVencimiento())
                .cargo(domain.getCargo())
                .montoFijo(domain.getMontoFijo())
                .canonMensual(domain.getCanonMensual())
                .canonMensualSinAjuste(domain.getCanonMensualSinAjuste())
                .hasta(domain.getHasta())
                .canonMensualLetras(domain.getCanonMensualLetras())
                .canonTotal(domain.getCanonTotal())
                .canonTotalLetras(domain.getCanonTotalLetras())
                .meses(domain.getMeses())
                .mesesLetras(domain.getMesesLetras())
                .ajuste(domain.getAjuste())
                .build();
    }
}
