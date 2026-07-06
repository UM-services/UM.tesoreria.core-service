package um.tesoreria.core.hexagonal.documento.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.documento.infrastructure.web.dto.DocumentoRequest;
import um.tesoreria.core.hexagonal.documento.infrastructure.web.dto.DocumentoResponse;

@Component
public class DocumentoDtoMapper {

    public Documento toDomain(DocumentoRequest request) {
        if (request == null) return null;
        return Documento.builder()
                .documentoId(request.getDocumentoId())
                .nombre(request.getNombre())
                .build();
    }

    public DocumentoResponse toResponse(Documento domain) {
        if (domain == null) return null;
        return DocumentoResponse.builder()
                .documentoId(domain.getDocumentoId())
                .nombre(domain.getNombre())
                .build();
    }

}
