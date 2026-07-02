package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieRequest;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieResponse;

@Component
public class ChequeraSerieDtoMapper {

    public ChequeraSerie toDomain(ChequeraSerieRequest request) {
        if (request == null) return null;
        return ChequeraSerie.builder()
                .chequeraId(request.getChequeraId())
                .facultadId(request.getFacultadId())
                .tipoChequeraId(request.getTipoChequeraId())
                .chequeraSerieId(request.getChequeraSerieId())
                .personaId(request.getPersonaId())
                .documentoId(request.getDocumentoId())
                .lectivoId(request.getLectivoId())
                .arancelTipoId(request.getArancelTipoId())
                .cursoId(request.getCursoId())
                .asentado(request.getAsentado())
                .geograficaId(request.getGeograficaId())
                .fecha(request.getFecha())
                .cuotasPagadas(request.getCuotasPagadas())
                .observaciones(request.getObservaciones())
                .alternativaId(request.getAlternativaId())
                .algoPagado(request.getAlgoPagado())
                .tipoImpresionId(request.getTipoImpresionId())
                .flagPayperTic(request.getFlagPayperTic())
                .usuarioId(request.getUsuarioId())
                .enviado(request.getEnviado())
                .retenida(request.getRetenida())
                .version(request.getVersion())
                .hpum(request.getHpum())
                .becaPorcentaje(request.getBecaPorcentaje())
                .becaResolucion(request.getBecaResolucion())
                .becaFecha(request.getBecaFecha())
                .becaUserId(request.getBecaUserId())
                .build();
    }

    public ChequeraSerieResponse toResponse(ChequeraSerie domain) {
        if (domain == null) return null;
        return ChequeraSerieResponse.builder()
                .chequeraId(domain.getChequeraId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .lectivoId(domain.getLectivoId())
                .arancelTipoId(domain.getArancelTipoId())
                .arancelTipo(domain.getArancelTipo())
                .domicilio(domain.getDomicilio())
                .facultad(domain.getFacultad())
                .lectivo(domain.getLectivo())
                .persona(domain.getPersona())
                .tipoChequera(domain.getTipoChequera())
                .geografica(domain.getGeografica())
                .cursoId(domain.getCursoId())
                .asentado(domain.getAsentado())
                .geograficaId(domain.getGeograficaId())
                .fecha(domain.getFecha())
                .cuotasPagadas(domain.getCuotasPagadas())
                .observaciones(domain.getObservaciones())
                .alternativaId(domain.getAlternativaId())
                .algoPagado(domain.getAlgoPagado())
                .tipoImpresionId(domain.getTipoImpresionId())
                .flagPayperTic(domain.getFlagPayperTic())
                .usuarioId(domain.getUsuarioId())
                .enviado(domain.getEnviado())
                .retenida(domain.getRetenida())
                .version(domain.getVersion())
                .hpum(domain.getHpum())
                .becaPorcentaje(domain.getBecaPorcentaje())
                .becaResolucion(domain.getBecaResolucion())
                .becaFecha(domain.getBecaFecha())
                .becaUserId(domain.getBecaUserId())
                .cuotasDeuda(domain.getCuotasDeuda())
                .importeDeuda(domain.getImporteDeuda())
                .ultimoEnvio(domain.getUltimoEnvio())
                .build();
    }
}
