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
        ContratoEntity.ContratoEntityBuilder builder = ContratoEntity.builder()
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
                .hasta(contrato.getHasta());

        if (contrato.getCargo() != null) builder.cargo(contrato.getCargo());
        if (contrato.getMontoFijo() != null) builder.montoFijo(contrato.getMontoFijo());
        if (contrato.getCanonMensual() != null) builder.canonMensual(contrato.getCanonMensual());
        if (contrato.getCanonMensualSinAjuste() != null) builder.canonMensualSinAjuste(contrato.getCanonMensualSinAjuste());
        if (contrato.getCanonMensualLetras() != null) builder.canonMensualLetras(contrato.getCanonMensualLetras());
        if (contrato.getCanonTotal() != null) builder.canonTotal(contrato.getCanonTotal());
        if (contrato.getCanonTotalLetras() != null) builder.canonTotalLetras(contrato.getCanonTotalLetras());
        if (contrato.getMeses() != null) builder.meses(contrato.getMeses());
        if (contrato.getMesesLetras() != null) builder.mesesLetras(contrato.getMesesLetras());
        if (contrato.getAjuste() != null) builder.ajuste(contrato.getAjuste());

        return builder.build();
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
