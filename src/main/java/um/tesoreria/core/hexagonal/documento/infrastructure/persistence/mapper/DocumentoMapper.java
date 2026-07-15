package um.tesoreria.core.hexagonal.documento.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.documento.infrastructure.persistence.entity.DocumentoEntity;

@Component
public class DocumentoMapper {

    public Documento toDomainModel(DocumentoEntity entity) {
        if (entity == null) return null;
        return Documento.builder()
                .documentoId(entity.getDocumentoId())
                .nombre(entity.getNombre())
                .build();
    }

    public DocumentoEntity toEntity(Documento domain) {
        if (domain == null) return null;
        DocumentoEntity.DocumentoEntityBuilder builder = DocumentoEntity.builder()
                .documentoId(domain.getDocumentoId());
        
        if (domain.getNombre() != null) {
            builder.nombre(domain.getNombre());
        }
        
        return builder.build();
    }

}
