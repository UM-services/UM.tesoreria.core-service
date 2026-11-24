package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorSede;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Consultas de borradores ABIERTO para la exportación por facultad y sede.
 *
 * <p>La sede de un borrador no está persistida: se deriva del tipo de chequera, igual que
 * {@code chequera_serie.geografica} (ver {@link BorradorEmisionService#sedeEmision}).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BorradorConsultaService {

    private final BorradorChequeraRepository borradorRepository;
    private final BorradorEmisionService borradorEmisionService;
    private final TipoChequeraService tipoChequeraService;
    private final FacultadService facultadService;
    private final GeograficaService geograficaService;

    @Transactional(readOnly = true)
    public List<FacultadSedeBorradorResponse> resumenLectivo(Integer lectivoId) {
        if (lectivoId == null) {
            return List.of();
        }
        Map<Integer, Integer> sedePorTipo = sedePorTipo();
        List<BorradorSede> sedes = borradorRepository.findAllSedesByLectivoIdAndEstado(lectivoId,
                EstadoBorrador.ABIERTO);

        Map<SedeKey, Long> conteo = new LinkedHashMap<>();
        for (BorradorSede borrador : sedes) {
            Integer geograficaId = sedePorTipo.get(borrador.tipoChequeraId());
            if (borrador.facultadId() == null || geograficaId == null) {
                log.debug("Borrador con facultad/sede sin resolver: {}", borrador);
                continue;
            }
            conteo.merge(new SedeKey(borrador.facultadId(), geograficaId), 1L, Long::sum);
        }
        if (conteo.isEmpty()) {
            return List.of();
        }

        Map<Integer, String> facultades = facultades();
        Map<Integer, String> geograficas = geograficas();
        return conteo.entrySet().stream()
                .map(entry -> new FacultadSedeBorradorResponse(
                        entry.getKey().facultadId(),
                        facultades.get(entry.getKey().facultadId()),
                        entry.getKey().geograficaId(),
                        geograficas.get(entry.getKey().geograficaId()),
                        entry.getValue()))
                .sorted(Comparator.comparing(FacultadSedeBorradorResponse::facultadId)
                        .thenComparing(FacultadSedeBorradorResponse::geograficaId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BorradorChequera> listarPorSede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        if (facultadId == null || lectivoId == null || geograficaId == null) {
            return List.of();
        }
        Integer sede = geograficaId;
        Map<Integer, Integer> sedePorTipo = sedePorTipo();
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-AR"));
        collator.setStrength(Collator.PRIMARY);
        Comparator<String> texto = (a, b) -> collator.compare(a == null ? "" : a, b == null ? "" : b);
        return borradorRepository.findAllByLectivoIdAndFacultadIdAndEstado(lectivoId, facultadId,
                        EstadoBorrador.ABIERTO).stream()
                .filter(borrador -> Objects.equals(sedePorTipo.get(borrador.getTipoChequeraId()), sede))
                .map(borradorEmisionService::enriquecerParaConsulta)
                .sorted(Comparator
                        .comparing(BorradorChequera::getPersonaApellido, texto)
                        .thenComparing(BorradorChequera::getPersonaNombre, texto)
                        .thenComparing(BorradorChequera::getBorradorId))
                .toList();
    }

    private Map<Integer, Integer> sedePorTipo() {
        Map<Integer, Integer> sedePorTipo = new LinkedHashMap<>();
        for (TipoChequera tipo : tipoChequeraService.findAll()) {
            if (tipo.getTipoChequeraId() != null && tipo.getGeograficaId() != null) {
                sedePorTipo.putIfAbsent(tipo.getTipoChequeraId(), tipo.getGeograficaId());
            }
        }
        return sedePorTipo;
    }

    private Map<Integer, String> facultades() {
        Map<Integer, String> facultades = new LinkedHashMap<>();
        for (Facultad facultad : facultadService.findAll()) {
            if (facultad.getFacultadId() != null) {
                facultades.putIfAbsent(facultad.getFacultadId(), facultad.getNombre());
            }
        }
        return facultades;
    }

    private Map<Integer, String> geograficas() {
        Map<Integer, String> geograficas = new LinkedHashMap<>();
        for (Geografica geografica : geograficaService.findAll()) {
            if (geografica.getGeograficaId() != null) {
                geograficas.putIfAbsent(geografica.getGeograficaId(), geografica.getNombre());
            }
        }
        return geograficas;
    }

    private record SedeKey(Integer facultadId, Integer geograficaId) {
    }
}
