package um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDomicilio;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.TipoDocumentoConsulta;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto.ConsultaDeudaResponse;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto.ConsultaPersonaResponse;

@Component
public class ConsultaDtoMapper {

    public ConsultaPersonaResponse toPersonaResponse(ConsultaPersona domain) {
        if (domain == null) {
            return null;
        }
        return ConsultaPersonaResponse.builder()
                .numeroDocumento(domain.getNumeroDocumento() == null ? null : domain.getNumeroDocumento().toPlainString())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .sexo(domain.getSexo())
                .numeroPrefijo(domain.getNumeroPrefijo())
                .numeroPosfijo(domain.getNumeroPosfijo())
                .documentos(domain.getDocumentos() == null ? null
                        : domain.getDocumentos().stream().map(this::toTipoDocumentoResponse).toList())
                .domicilio(toDomicilioResponse(domain.getDomicilio()))
                .build();
    }

    public ConsultaDeudaResponse toDeudaResponse(ConsultaDeuda domain) {
        if (domain == null) {
            return null;
        }
        return ConsultaDeudaResponse.builder()
                .numeroDocumento(domain.getNumeroDocumento() == null ? null : domain.getNumeroDocumento().toPlainString())
                .cuotas(domain.getCuotas())
                .deuda(domain.getDeuda())
                .deudas(domain.getDeudas())
                .vencimientos(domain.getVencimientos())
                .build();
    }

    private ConsultaPersonaResponse.TipoDocumentoResponse toTipoDocumentoResponse(TipoDocumentoConsulta domain) {
        return ConsultaPersonaResponse.TipoDocumentoResponse.builder()
                .documentoId(domain.getDocumentoId())
                .nombre(domain.getNombre())
                .build();
    }

    private ConsultaPersonaResponse.DomicilioResponse toDomicilioResponse(ConsultaDomicilio domain) {
        if (domain == null) {
            return null;
        }
        return ConsultaPersonaResponse.DomicilioResponse.builder()
                .calle(domain.getCalle())
                .puerta(domain.getPuerta())
                .piso(domain.getPiso())
                .dpto(domain.getDpto())
                .codigoPostal(domain.getCodigoPostal())
                .provinciaId(domain.getProvinciaId())
                .provinciaNombre(domain.getProvinciaNombre())
                .localidadId(domain.getLocalidadId())
                .localidadNombre(domain.getLocalidadNombre())
                .telefono(domain.getTelefono())
                .movil(domain.getMovil())
                .emailPersonal(domain.getEmailPersonal())
                .emailInstitucional(domain.getEmailInstitucional())
                .build();
    }
}
