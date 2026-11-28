package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.service.ClaseChequeraService;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroGeneracion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionPadronItem;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoGeneracionFila;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.service.view.ChequeraSerieAltaService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orquestador de la generación masiva de borradores sobre las series de una facultad y sede y de
 * la emisión previa al envío.
 *
 * <p>No duplica el motor: la etapa de borradores usa {@link BorradorEmisionService} (el mismo que
 * la emisión individual y las chequeras incompletas) y la etapa de emisión usa
 * {@link EmisionChequeraService#emitir} con {@code forzarNueva = false}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeneracionBorradoresService {

    private final ChequeraSerieAltaService chequeraSerieAltaService;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final BajaService bajaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final TipoChequeraService tipoChequeraService;
    private final ClaseChequeraService claseChequeraService;
    private final ArancelTipoService arancelTipoService;
    private final BorradorEmisionService borradorEmisionService;
    private final BorradorChequeraRepository borradorRepository;
    private final EmisionChequeraService emisionChequeraService;

    /**
     * Padrón de series del job. Aplica «sólo no enviadas», clase/tipo y la selección puntual, y
     * descarta las series dadas de baja.
     */
    @Transactional(readOnly = true)
    public List<GeneracionJobTarea> seleccionar(FiltroGeneracion filtro) {
        return candidatas(filtro).stream().map(this::tareaDe).toList();
    }

    /**
     * Vista previa del padrón: mismas series que procesará el job, con los datos que muestra la
     * pantalla (persona, tipo, clase, curso, arancel, enviado y borrador abierto si ya existe).
     */
    @Transactional(readOnly = true)
    public List<GeneracionPadronItem> preview(FiltroGeneracion filtro) {
        List<ChequeraSerieAlta> candidatas = candidatas(filtro);
        if (candidatas.isEmpty()) {
            return List.of();
        }
        Map<Integer, TipoChequera> tipos = tipos();
        Map<Integer, ClaseChequera> clases = clases();
        Map<Integer, String> aranceles = aranceles();
        Map<String, BorradorChequera> abiertos = abiertos(filtro.getFacultadId(), candidatas);
        return candidatas.stream().map(alta -> {
            TipoChequera tipo = tipos.get(alta.getTipoChequeraId());
            Integer claseId = tipo == null ? null : tipo.getClaseChequeraId();
            ClaseChequera clase = claseId == null ? null : clases.get(claseId);
            BorradorChequera abierto = abiertos.get(claveSerie(alta.getTipoChequeraId(),
                    alta.getChequeraSerieId()));
            return GeneracionPadronItem.builder()
                    .chequeraId(alta.getChequeraId())
                    .personaId(alta.getPersonaId())
                    .documentoId(alta.getDocumentoId())
                    .personaNombre(nombreDe(alta))
                    .facultadId(alta.getFacultadId())
                    .tipoChequeraId(alta.getTipoChequeraId())
                    .tipoChequeraNombre(tipo == null ? null : tipo.getNombre())
                    .claseChequeraId(claseId)
                    .claseChequeraNombre(clase == null ? null : clase.getNombre())
                    .chequeraSerieId(alta.getChequeraSerieId())
                    .cursoId(alta.getCursoId())
                    .arancelTipoId(alta.getArancelTipoId())
                    .arancelTipoNombre(aranceles.get(alta.getArancelTipoId()))
                    .alternativaId(alta.getAlternativaId())
                    .enviado(alta.getEnviado())
                    .borradorAbiertoId(abierto == null ? null : abierto.getBorradorId())
                    .build();
        }).toList();
    }

    /**
     * Etapa BORRADORES para una serie: deja un borrador ABIERTO que la referencia, sin escribir en
     * {@code chequera_*}. Si ya hay un borrador abierto, lo reutiliza tal cual.
     */
    public ResultadoGeneracionFila prepararBorrador(GeneracionJobTarea tarea, String usuarioLogin) {
        ChequeraSerie serie;
        try {
            serie = chequeraSerieService.findByChequeraId(tarea.getChequeraId());
        } catch (RuntimeException e) {
            return ResultadoGeneracionFila.error("NO_ENCONTRADA",
                    "No existe la chequera " + tarea.getChequeraId());
        }
        if (bajaService.findDatosByUnique(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId()).isPresent()) {
            return ResultadoGeneracionFila.omitida("BAJA", "La chequera está dada de baja");
        }
        if (esPagada(serie)) {
            return ResultadoGeneracionFila.omitida("PAGADA", "La chequera está totalmente paga");
        }
        if (lectivoCuotaService.findAllByFacultadLectivoTipo(serie.getFacultadId(), serie.getLectivoId(),
                serie.getTipoChequeraId()).isEmpty()) {
            return ResultadoGeneracionFila.omitida("SIN_DISENO",
                    "No hay diseño de cuotas para la facultad/lectivo/tipo de chequera");
        }

        Optional<BorradorChequera> abierto = borradorRepository.findAbiertoBySerie(serie.getFacultadId(),
                serie.getTipoChequeraId(), serie.getChequeraSerieId());
        if (abierto.isPresent()) {
            // Se completa antes de reutilizarlo: un borrador viejo puede tener cuotas sin precio y
            // la emisión las persistiría incompletas.
            BorradorChequera completo = borradorEmisionService.completarBorrador(
                    abierto.get().getBorradorId(), FuenteCompletado.DISENO);
            return ResultadoGeneracionFila.borrador(completo.getBorradorId(), true);
        }

        BorradorChequera borrador = borradorEmisionService.crearBorrador(datosDe(serie), usuarioLogin);
        return ResultadoGeneracionFila.borrador(borrador.getBorradorId(), false);
    }

    /**
     * Etapa EMISION: emite el borrador y actualiza la serie (cuotas, totales, alternativas y
     * débitos). No extiende el débito: el envío masivo tiene su propia operación para eso.
     */
    public ResultadoGeneracionFila emitir(GeneracionJobTarea tarea, GeneracionJob job) {
        if (tarea.getBorradorId() == null) {
            return ResultadoGeneracionFila.error("SIN_BORRADOR", "La tarea no tiene borrador asociado");
        }
        BorradorChequera borrador;
        try {
            borrador = borradorEmisionService.cargar(tarea.getBorradorId());
        } catch (RuntimeException e) {
            return ResultadoGeneracionFila.error("BORRADOR_INEXISTENTE",
                    "No existe el borrador " + tarea.getBorradorId());
        }
        if (borrador.getEstado() != EstadoBorrador.ABIERTO) {
            return ResultadoGeneracionFila.omitida("BORRADOR_NO_ABIERTO",
                    "El borrador está " + borrador.getEstado() + " (retención o emisión previa)");
        }
        try {
            var resultado = emisionChequeraService.emitir(tarea.getBorradorId(), SolicitudEmision.builder()
                    .version(borrador.getVersion())
                    .forzarNueva(false)
                    .forzarDeuda(job != null && job.isForzarDeuda())
                    .motivoOverride(job == null ? null : job.getMotivoOverride())
                    .claveIdempotencia("generacion:" + tarea.getBorradorId())
                    .usuarioLogin(job == null ? null : job.getUsuarioLogin())
                    .build(), null);
            return ResultadoGeneracionFila.emitida(tarea.getBorradorId(), resultado.getChequeraSerieId());
        } catch (EmisionException e) {
            return ResultadoGeneracionFila.error(e.getCodigo(), e.getMessage());
        } catch (RuntimeException e) {
            log.error("Generación: falló la emisión del borrador {}", tarea.getBorradorId(), e);
            return ResultadoGeneracionFila.error("ERROR_INTERNO", e.getMessage());
        }
    }

    private List<ChequeraSerieAlta> candidatas(FiltroGeneracion filtro) {
        List<ChequeraSerieAlta> altas = chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(
                filtro.getLectivoId(), filtro.getFacultadId(), filtro.getGeograficaId());
        Map<Integer, TipoChequera> tipos = tipos();
        Set<Long> pedidas = filtro.getChequeraSerieIds() == null || filtro.getChequeraSerieIds().isEmpty() ? null
                : Set.copyOf(filtro.getChequeraSerieIds());
        List<ChequeraSerieAlta> filtradas = altas.stream()
                .filter(alta -> !filtro.isSoloNoEnviadas() || !esEnviado(alta))
                .filter(alta -> filtro.getClaseChequeraId() == null
                        || Objects.equals(claseDe(tipos, alta.getTipoChequeraId()), filtro.getClaseChequeraId()))
                .filter(alta -> filtro.getTipoChequeraId() == null
                        || Objects.equals(alta.getTipoChequeraId(), filtro.getTipoChequeraId()))
                .filter(alta -> pedidas == null || pedidas.contains(alta.getChequeraId()))
                .toList();
        if (filtradas.isEmpty()) {
            return List.of();
        }
        Set<Long> conBaja = chequeraIdsConBaja(filtradas.stream().map(ChequeraSerieAlta::getChequeraId).toList());
        return filtradas.stream().filter(alta -> !conBaja.contains(alta.getChequeraId())).toList();
    }

    private CrearBorradorData datosDe(ChequeraSerie serie) {
        return CrearBorradorData.builder()
                .personaId(serie.getPersonaId())
                .documentoId(serie.getDocumentoId())
                .facultadId(serie.getFacultadId())
                .lectivoId(serie.getLectivoId())
                .tipoChequeraId(serie.getTipoChequeraId())
                .arancelTipoId(serie.getArancelTipoId())
                .alternativaId(serie.getAlternativaId())
                .cursoId(serie.getCursoId())
                .chequeraSerieId(serie.getChequeraSerieId())
                .becaPorcentaje(serie.getBecaPorcentaje())
                .becaResolucion(serie.getBecaResolucion())
                .becaFecha(serie.getBecaFecha())
                .becaUserId(serie.getBecaUserId())
                .hpum(serie.getHpum())
                .fecha(serie.getFecha())
                .calculaBeca(false)
                .fuente(FuenteCompletado.DISENO)
                .incluyeBarras(false)
                .copiaFacultad(false)
                .build();
    }

    private GeneracionJobTarea tareaDe(ChequeraSerieAlta alta) {
        return GeneracionJobTarea.builder()
                .chequeraId(alta.getChequeraId())
                .personaId(alta.getPersonaId())
                .documentoId(alta.getDocumentoId())
                .facultadId(alta.getFacultadId())
                .tipoChequeraId(alta.getTipoChequeraId())
                .chequeraSerieId(alta.getChequeraSerieId())
                .cursoId(alta.getCursoId())
                .personaNombre(recortar(nombreDe(alta), 120))
                .build();
    }

    private Map<String, BorradorChequera> abiertos(Integer facultadId, List<ChequeraSerieAlta> altas) {
        List<Long> series = altas.stream().map(ChequeraSerieAlta::getChequeraSerieId)
                .filter(Objects::nonNull).distinct().toList();
        return borradorRepository.findAllByFacultadIdAndChequeraSerieIdInAndEstado(facultadId, series,
                        EstadoBorrador.ABIERTO).stream()
                .collect(Collectors.toMap(
                        borrador -> claveSerie(borrador.getTipoChequeraId(), borrador.getChequeraSerieId()),
                        Function.identity(), (primera, segunda) -> primera));
    }

    private Map<Integer, TipoChequera> tipos() {
        Map<Integer, TipoChequera> tipos = new LinkedHashMap<>();
        for (TipoChequera tipo : tipoChequeraService.findAll()) {
            if (tipo.getTipoChequeraId() != null) {
                tipos.putIfAbsent(tipo.getTipoChequeraId(), tipo);
            }
        }
        return tipos;
    }

    private Map<Integer, ClaseChequera> clases() {
        Map<Integer, ClaseChequera> clases = new LinkedHashMap<>();
        for (ClaseChequera clase : claseChequeraService.findAll()) {
            if (clase.getClaseChequeraId() != null) {
                clases.putIfAbsent(clase.getClaseChequeraId(), clase);
            }
        }
        return clases;
    }

    private Map<Integer, String> aranceles() {
        Map<Integer, String> aranceles = new LinkedHashMap<>();
        arancelTipoService.findAll().forEach(tipo -> {
            if (tipo.getArancelTipoId() != null) {
                aranceles.putIfAbsent(tipo.getArancelTipoId(), tipo.getDescripcion());
            }
        });
        return aranceles;
    }

    private boolean esPagada(ChequeraSerie serie) {
        List<ChequeraCuota> cuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        return !cuotas.isEmpty() && cuotas.stream().allMatch(cuota -> BorradorCuota.esVerdadero(cuota.getPagado()));
    }

    private Set<Long> chequeraIdsConBaja(List<Long> chequeraIds) {
        try {
            return bajaService.findAllByChequeraIdIn(chequeraIds).stream()
                    .map(baja -> baja.getChequeraId())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            log.warn("Generación: no se pudieron leer las bajas del padrón: {}", e.getMessage());
            return Set.of();
        }
    }

    private static Integer claseDe(Map<Integer, TipoChequera> tipos, Integer tipoChequeraId) {
        TipoChequera tipo = tipoChequeraId == null ? null : tipos.get(tipoChequeraId);
        return tipo == null ? null : tipo.getClaseChequeraId();
    }

    private static boolean esEnviado(ChequeraSerieAlta alta) {
        return alta.getEnviado() != null && alta.getEnviado() == 1;
    }

    private static String nombreDe(ChequeraSerieAlta alta) {
        if (alta.getPersona() == null) {
            return null;
        }
        String apellido = alta.getPersona().getApellido() == null ? "" : alta.getPersona().getApellido();
        String nombre = alta.getPersona().getNombre() == null ? "" : alta.getPersona().getNombre();
        String completo = (apellido + ", " + nombre).replaceAll("^,\\s*", "").trim();
        return completo.isEmpty() ? null : completo;
    }

    private static String claveSerie(Integer tipoChequeraId, Long chequeraSerieId) {
        return tipoChequeraId + "." + chequeraSerieId;
    }

    private static String recortar(String texto, int largo) {
        return texto == null || texto.length() <= largo ? texto : texto.substring(0, largo);
    }
}
