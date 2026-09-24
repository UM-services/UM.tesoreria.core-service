package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service.UsuarioChequeraFacultadService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChequerasPorUsuarioService {

    public static class InvalidQueryException extends RuntimeException {
        public InvalidQueryException() {
            super("Parámetros de consulta inválidos");
        }
    }

    private final UsuarioChequeraFacultadService facultadService;
    private final ChequeraSerieRepository chequeraRepository;
    private final CalculateDeudaUseCase calculateDeudaUseCase;

    public Page<ChequeraSerie> findAll(Long userId, Integer lectivoId, BigDecimal personaId,
                                       Integer documentoId, int page, int size) {
        if (userId == null || userId <= 0 || lectivoId == null || lectivoId <= 0
                || page < 0 || size < 1 || size > 100
                || ((personaId == null) != (documentoId == null))
                || (personaId != null && personaId.signum() <= 0)
                || (documentoId != null && documentoId <= 0)) {
            throw new InvalidQueryException();
        }

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "chequeraId"));
        List<Integer> facultadIds = facultadService.findAllByUserId(userId).stream()
                .map(UsuarioChequeraFacultad::getFacultadId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (facultadIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<ChequeraSerie> chequeras = personaId == null
                ? chequeraRepository.findAllByLectivoIdAndFacultadIdIn(lectivoId, facultadIds, pageable)
                : chequeraRepository.findAllByLectivoIdAndFacultadIdInAndPersonaIdAndDocumentoId(
                        lectivoId, facultadIds, personaId, documentoId, pageable);

        return chequeras.map(chequera -> {
            var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
            chequera.setImporteDeuda(deuda.getDeuda());
            chequera.setCuotasDeuda(deuda.getCuotas());
            return chequera;
        });
    }
}
