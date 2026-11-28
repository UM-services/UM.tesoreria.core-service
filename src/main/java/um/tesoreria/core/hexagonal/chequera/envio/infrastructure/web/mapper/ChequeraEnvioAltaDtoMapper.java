package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.ChequeraEnvioAltaResponse;

import java.util.List;

@Component
public class ChequeraEnvioAltaDtoMapper {

    public ChequeraEnvioAltaResponse toResponse(ChequeraEnvioAlta domain) {
        if (domain == null) {
            return null;
        }
        return ChequeraEnvioAltaResponse.builder()
                .chequeraId(domain.chequeraId())
                .facultadId(domain.facultadId())
                .tipoChequeraId(domain.tipoChequeraId())
                .chequeraSerieId(domain.chequeraSerieId())
                .alternativaId(domain.alternativaId())
                .personaId(domain.personaId())
                .documentoId(domain.documentoId())
                .lectivoId(domain.lectivoId())
                .geograficaId(domain.geograficaId())
                .numero(domain.numero())
                .personaApellidoNombre(domain.apellidoNombre())
                .tipoChequera(domain.tipoChequera())
                .arancelTipo(domain.arancelTipo())
                .emailPersonal(domain.emailPersonal())
                .emailInstitucional(domain.emailInstitucional())
                .emailPersonalValido(domain.emailPersonalValido())
                .emailInstitucionalValido(domain.emailInstitucionalValido())
                .enviado(domain.enviado())
                .build();
    }

    public List<ChequeraEnvioAltaResponse> toResponse(List<ChequeraEnvioAlta> domains) {
        if (domains == null) {
            return List.of();
        }
        return domains.stream().map(this::toResponse).toList();
    }
}
