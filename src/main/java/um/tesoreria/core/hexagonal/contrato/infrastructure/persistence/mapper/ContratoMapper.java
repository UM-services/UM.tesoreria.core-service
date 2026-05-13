package um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.infrastructure.persistence.entity.ContratoEntity;

@Component
public class ContratoMapper {

    public ContratoEntity toEntity(Contrato contrato) {
        if (contrato == null) {
            return null;
        }
        return ContratoEntity.builder()
                .contratoId(contrato.getContratoId())
                .personaId(contrato.getPersonaId())
                .documentoId(contrato.getDocumentoId())
                .desde(contrato.getDesde())
                .facultadId(contrato.getFacultadId())
                .planId(contrato.getPlanId())
                .materiaId(contrato.getMateriaId())
                .geograficaId(contrato.getGeograficaId())
                .cargoMateriaId(contrato.getCargoMateriaId())
                .primerVencimiento(contrato.getPrimerVencimiento())
                .cargo(contrato.getCargo())
                .montoFijo(contrato.getMontoFijo())
                .canonMensual(contrato.getCanonMensual())
                .canonMensualSinAjuste(contrato.getCanonMensualSinAjuste())
                .hasta(contrato.getHasta())
                .canonMensualLetras(contrato.getCanonMensualLetras())
                .canonTotal(contrato.getCanonTotal())
                .canonTotalLetras(contrato.getCanonTotalLetras())
                .meses(contrato.getMeses())
                .mesesLetras(contrato.getMesesLetras())
                .ajuste(contrato.getAjuste())
                .build();
    }

    public Contrato toDomainModel(ContratoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Contrato.builder()
                .contratoId(entity.getContratoId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .desde(entity.getDesde())
                .facultadId(entity.getFacultadId())
                .planId(entity.getPlanId())
                .materiaId(entity.getMateriaId())
                .geograficaId(entity.getGeograficaId())
                .cargoMateriaId(entity.getCargoMateriaId())
                .primerVencimiento(entity.getPrimerVencimiento())
                .cargo(entity.getCargo())
                .montoFijo(entity.getMontoFijo())
                .canonMensual(entity.getCanonMensual())
                .canonMensualSinAjuste(entity.getCanonMensualSinAjuste())
                .hasta(entity.getHasta())
                .canonMensualLetras(entity.getCanonMensualLetras())
                .canonTotal(entity.getCanonTotal())
                .canonTotalLetras(entity.getCanonTotalLetras())
                .meses(entity.getMeses())
                .mesesLetras(entity.getMesesLetras())
                .ajuste(entity.getAjuste())
                .build();
    }
}
