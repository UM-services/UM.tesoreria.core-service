package um.tesoreria.core.extern.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetFacultadByIdUseCase;

@Component
@RequiredArgsConstructor
public class FacultadUrlResolver {

    private final GetFacultadByIdUseCase getFacultadByIdUseCase;

    public String getBaseUrl(Integer facultadId) {
        if (facultadId == null) {
            throw new IllegalArgumentException("FacultadId cannot be null");
        }
        Facultad facultad = getFacultadByIdUseCase.getById(facultadId)
                .orElseThrow(() -> new FacultadException(facultadId));
        return getBaseUrl(facultad);
    }

    public String getBaseUrl(Facultad facultad) {
        if (facultad == null || facultad.getApiserver() == null || facultad.getApiserver().isEmpty()) {
            throw new IllegalArgumentException("Facultad apiserver is null or empty");
        }
        Long port = facultad.getApiport() != null ? facultad.getApiport() : 80L;
        return "http://" + facultad.getApiserver() + ":" + port;
    }
}
