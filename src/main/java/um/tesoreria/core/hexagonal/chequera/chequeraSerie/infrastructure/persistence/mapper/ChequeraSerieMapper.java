package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.mapper.ArancelTipoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.mapper.DomicilioMapper;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper.LectivoMapper;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.mapper.PersonaMapper;

@Component
@RequiredArgsConstructor
public class ChequeraSerieMapper {

    private final ArancelTipoMapper arancelTipoMapper;
    private final DomicilioMapper domicilioMapper;
    private final FacultadMapper facultadMapper;
    private final GeograficaMapper geograficaMapper;
    private final LectivoMapper lectivoMapper;
    private final PersonaMapper personaMapper;
    private final TipoChequeraMapper tipoChequeraMapper;

    public ChequeraSerie toDomainModel(ChequeraSerieEntity entity) {
        if (entity == null) return null;
        return ChequeraSerie.builder()
                .chequeraId(entity.getChequeraId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .lectivoId(entity.getLectivoId())
                .arancelTipoId(entity.getArancelTipoId())
                .arancelTipo(arancelTipoMapper.toDomainModel(entity.getArancelTipo()))
                .domicilio(domicilioMapper.toDomainModel(entity.getDomicilio()))
                .facultad(facultadMapper.toDomain(entity.getFacultad()))
                .lectivo(lectivoMapper.toDomainModel(entity.getLectivo()))
                .persona(personaMapper.toDomainModel(entity.getPersona()))
                .tipoChequera(tipoChequeraMapper.toDomainModel(entity.getTipoChequera()))
                .geografica(geograficaMapper.toDomainModel(entity.getGeografica()))
                .cursoId(entity.getCursoId())
                .asentado(entity.getAsentado())
                .geograficaId(entity.getGeograficaId())
                .fecha(entity.getFecha())
                .cuotasPagadas(entity.getCuotasPagadas())
                .observaciones(entity.getObservaciones())
                .alternativaId(entity.getAlternativaId())
                .algoPagado(entity.getAlgoPagado())
                .tipoImpresionId(entity.getTipoImpresionId())
                .flagPayperTic(entity.getFlagPayperTic() != null ? entity.getFlagPayperTic() : (byte) 0)
                .usuarioId(entity.getUsuarioId())
                .enviado(entity.getEnviado() != null ? entity.getEnviado() : (byte) 0)
                .retenida(entity.getRetenida() != null ? entity.getRetenida() : (byte) 0)
                .version(entity.getVersion())
                .hpum(entity.getHpum() != null ? entity.getHpum() : (byte) 0)
                .becaPorcentaje(entity.getBecaPorcentaje() != null ? entity.getBecaPorcentaje() : BigDecimal.ZERO)
                .becaResolucion(entity.getBecaResolucion())
                .becaFecha(entity.getBecaFecha())
                .becaUserId(entity.getBecaUserId())
                .cuotasDeuda(entity.getCuotasDeuda())
                .importeDeuda(entity.getImporteDeuda() != null ? entity.getImporteDeuda() : BigDecimal.ZERO)
                .ultimoEnvio(entity.getUltimoEnvio())
                .build();
    }

    public ChequeraSerieEntity toEntity(ChequeraSerie domain) {
        if (domain == null) return null;
        ChequeraSerieEntity.ChequeraSerieEntityBuilder builder = ChequeraSerieEntity.builder()
                .chequeraId(domain.getChequeraId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .lectivoId(domain.getLectivoId())
                .arancelTipoId(domain.getArancelTipoId())
                .cursoId(domain.getCursoId())
                .asentado(domain.getAsentado())
                .geograficaId(domain.getGeograficaId())
                .fecha(domain.getFecha())
                .cuotasPagadas(domain.getCuotasPagadas())
                .observaciones(domain.getObservaciones())
                .alternativaId(domain.getAlternativaId())
                .algoPagado(domain.getAlgoPagado())
                .tipoImpresionId(domain.getTipoImpresionId())
                .usuarioId(domain.getUsuarioId())
                .version(domain.getVersion())
                .becaResolucion(domain.getBecaResolucion())
                .becaFecha(domain.getBecaFecha())
                .becaUserId(domain.getBecaUserId())
                .ultimoEnvio(domain.getUltimoEnvio());

        if (domain.getFlagPayperTic() != null) builder.flagPayperTic(domain.getFlagPayperTic());
        if (domain.getEnviado() != null) builder.enviado(domain.getEnviado());
        if (domain.getRetenida() != null) builder.retenida(domain.getRetenida());
        if (domain.getHpum() != null) builder.hpum(domain.getHpum());
        if (domain.getBecaPorcentaje() != null) builder.becaPorcentaje(domain.getBecaPorcentaje());
        if (domain.getCuotasDeuda() != null) builder.cuotasDeuda(domain.getCuotasDeuda());
        if (domain.getImporteDeuda() != null) builder.importeDeuda(domain.getImporteDeuda());

        return builder.build();
    }
}
