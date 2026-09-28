package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service.UsuarioChequeraClaseChequeraService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service.UsuarioChequeraFacultadService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.service.UsuarioChequeraGeograficaService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Estado de chequeras limitado a las asignaciones completas del usuario: facultad, sede geográfica y
 * clase de chequera ({@code usuario_chequera_facultad}, {@code usuario_chequera_geografica} y
 * {@code usuario_chequera_clase_chequera}). A diferencia de {@link ChequerasPorUsuarioService}, que
 * sólo filtra por facultad, una lista vacía en CUALQUIER dimensión significa que el usuario no ve
 * nada por esa dimensión y la consulta devuelve {@link Page#empty()} sin tocar el repositorio.
 *
 * <p>La clase de chequera no es columna de {@code chequera_serie}: se traduce a los
 * {@code tipoChequeraId} de los tipos pertenecientes a las clases asignadas
 * ({@code tipo_chequera.clase_chequera_id}).</p>
 */
@Service
@RequiredArgsConstructor
public class ChequerasPorUsuarioAsignadasService {

    public static class InvalidQueryException extends RuntimeException {
        public InvalidQueryException() {
            super("Parámetros de consulta inválidos");
        }
    }

    private final UsuarioChequeraFacultadService facultadService;
    private final UsuarioChequeraGeograficaService geograficaService;
    private final UsuarioChequeraClaseChequeraService claseChequeraService;
    private final TipoChequeraService tipoChequeraService;
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

        List<Integer> geograficaIds = geograficaService.findAllByUserId(userId).stream()
                .map(UsuarioChequeraGeografica::getGeograficaId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (geograficaIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Integer> claseChequeraIds = claseChequeraService.findAllByUserId(userId).stream()
                .map(UsuarioChequeraClaseChequera::getClaseChequeraId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (claseChequeraIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Integer> tipoChequeraIds = tipoChequeraService.findAllByClaseChequeraIds(claseChequeraIds).stream()
                .map(TipoChequera::getTipoChequeraId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (tipoChequeraIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<ChequeraSerie> chequeras = personaId == null
                ? chequeraRepository.findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdIn(
                        lectivoId, facultadIds, geograficaIds, tipoChequeraIds, pageable)
                : chequeraRepository.findAllByLectivoIdAndFacultadIdInAndGeograficaIdInAndTipoChequeraIdInAndPersonaIdAndDocumentoId(
                        lectivoId, facultadIds, geograficaIds, tipoChequeraIds, personaId, documentoId, pageable);

        return chequeras.map(chequera -> {
            var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
            chequera.setImporteDeuda(deuda.getDeuda());
            chequera.setCuotasDeuda(deuda.getCuotas());
            return chequera;
        });
    }
}
