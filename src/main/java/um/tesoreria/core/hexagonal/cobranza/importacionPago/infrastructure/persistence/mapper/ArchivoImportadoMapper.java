package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ArchivoImportadoEntity;

@Component
public class ArchivoImportadoMapper {

    public ArchivoImportadoEntity toEntity(ArchivoImportado domain) {
        if (domain == null) {
            return null;
        }
        return ArchivoImportadoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .fecha(domain.getFecha())
                .registros(domain.getRegistros())
                .build();
    }

    public ArchivoImportado toDomain(ArchivoImportadoEntity entity) {
        if (entity == null) {
            return null;
        }
        return ArchivoImportado.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .fecha(entity.getFecha())
                .registros(entity.getRegistros())
                .build();
    }

}
