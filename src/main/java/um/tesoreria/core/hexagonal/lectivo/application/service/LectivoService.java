package um.tesoreria.core.hexagonal.lectivo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.lectivo.application.exception.LectivoException;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectivoService {

    private final CreateLectivoUseCase createLectivoUseCase;
    private final GetAllLectivosUseCase getAllLectivosUseCase;
    private final GetAllLectivosReverseUseCase getAllLectivosReverseUseCase;
    private final GetAllLectivosByPersonaUseCase getAllLectivosByPersonaUseCase;
    private final GetLectivoByIdUseCase getLectivoByIdUseCase;
    private final GetLectivoByFechaUseCase getLectivoByFechaUseCase;
    private final GetLastLectivoUseCase getLastLectivoUseCase;
    private final DeleteLectivoUseCase deleteLectivoUseCase;

    public Lectivo createLectivo(Lectivo lectivo) {
        return createLectivoUseCase.createLectivo(lectivo);
    }

    public List<Lectivo> findAll() {
        return getAllLectivosUseCase.getAllLectivos();
    }

    public List<Lectivo> findAllReverse() {
        return getAllLectivosReverseUseCase.getAllLectivosReverse();
    }

    public List<Lectivo> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return getAllLectivosByPersonaUseCase.getAllLectivosByPersona(personaId, documentoId);
    }

    public Lectivo findByLectivoId(Integer lectivoId) {
        return getLectivoByIdUseCase.getLectivoById(lectivoId).orElseThrow(() -> new LectivoException(lectivoId));
    }

    public Lectivo findByFecha(OffsetDateTime fecha) {
        return getLectivoByFechaUseCase.getLectivoByFecha(fecha).orElseThrow(() -> new LectivoException(fecha));
    }

    public Optional<Lectivo> findLast() {
        return getLastLectivoUseCase.getLastLectivo();
    }

    public void deleteByLectivoId(Integer lectivoId) {
        deleteLectivoUseCase.deleteLectivo(lectivoId);
    }
}
