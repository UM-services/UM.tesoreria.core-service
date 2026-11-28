package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ActualizarBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoIncompleta;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.service.facade.ChequeraService;
import um.tesoreria.core.service.view.ChequeraIncompletaService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquestador de la reparación de chequeras incompletas ({@code frmIncompletas}).
 *
 * <p>No duplica el motor: la etapa de borradores usa {@link BorradorEmisionService} (que es el
 * puerto de {@code constructChequera} + {@code completeChequeraInGrid}) y la etapa de emisión usa
 * {@link EmisionChequeraService#emitir} con {@code nueva = false} (el puerto de
 * {@code makeChequera}) más {@code extendDebito}, tal como el legacy.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompletarChequerasIncompletasService {

    private final ChequeraIncompletaService chequeraIncompletaService;
    private final BajaService bajaService;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final BorradorEmisionService borradorEmisionService;
    private final BorradorChequeraRepository borradorRepository;
    private final EmisionChequeraService emisionChequeraService;
    private final ChequeraService chequeraService;

    /**
     * Padrón del job: las series de la vista, menos las dadas de baja (la vista ya las excluye,
     * esto es defensa) y aplicando el filtro «sólo no enviadas».
     */
    @Transactional(readOnly = true)
    public List<IncompletasJobTarea> seleccionar(FiltroIncompletas filtro) {
        List<ChequeraIncompleta> incompletas = chequeraIncompletaService
                .findAllByLectivoIdAndFacultadIdAndGeograficaIdAndClaseChequeraId(filtro.getLectivoId(),
                        filtro.getFacultadId(), filtro.getGeograficaId(), filtro.getClaseChequeraId());

        Set<Long> pedidas = filtro.getChequeraIds() == null || filtro.getChequeraIds().isEmpty() ? null
                : Set.copyOf(filtro.getChequeraIds());

        List<ChequeraIncompleta> candidatas = incompletas.stream()
                .filter(serie -> !filtro.isSoloNoEnviadas() || !esEnviada(serie))
                .filter(serie -> pedidas == null || pedidas.contains(serie.getChequeraId()))
                .toList();
        if (candidatas.isEmpty()) {
            return List.of();
        }

        Set<Long> conBaja = chequeraIdsConBaja(candidatas.stream().map(ChequeraIncompleta::getChequeraId).toList());
        return candidatas.stream()
                .filter(serie -> !conBaja.contains(serie.getChequeraId()))
                .map(this::tareaDe)
                .toList();
    }

    /**
     * Etapa A para una serie: deja un borrador abierto con las cuotas completas, preservando la
     * beca y el hpum de la serie. Nunca escribe en {@code chequera_*}.
     *
     * <p>La reparación no mira el estado de la persona: una chequera incompleta se completa siempre
     * que no esté de baja, totalmente paga o sin diseño de cuotas. La suspensión por deuda no es un
     * motivo para dejar cuotas sin generar.</p>
     */
    public ResultadoIncompleta prepararBorrador(IncompletasJobTarea tarea, String usuarioLogin) {
        ChequeraSerie serie;
        try {
            serie = chequeraSerieService.findByChequeraId(tarea.getChequeraId());
        } catch (RuntimeException e) {
            return ResultadoIncompleta.error("NO_ENCONTRADA", "No existe la chequera " + tarea.getChequeraId());
        }

        if (bajaService.findDatosByUnique(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId()).isPresent()) {
            return ResultadoIncompleta.omitida("BAJA", "La chequera está dada de baja");
        }
        if (esPagada(serie)) {
            return ResultadoIncompleta.omitida("PAGADA", "La chequera está totalmente paga");
        }
        if (lectivoCuotaService.findAllByFacultadLectivoTipo(serie.getFacultadId(), serie.getLectivoId(),
                serie.getTipoChequeraId()).isEmpty()) {
            return ResultadoIncompleta.omitida("SIN_DISENO",
                    "No hay diseño de cuotas para la facultad/lectivo/tipo de chequera");
        }

        Optional<BorradorChequera> abierto = borradorRepository.findAbiertoBySerie(serie.getFacultadId(),
                serie.getTipoChequeraId(), serie.getChequeraSerieId());
        if (abierto.isPresent()) {
            BorradorChequera borrador = sincronizarBeca(abierto.get(), serie);
            BorradorChequera completo = borradorEmisionService.completarBorrador(borrador.getBorradorId(),
                    FuenteCompletado.PLANTILLA);
            return ResultadoIncompleta.borrador(completo.getBorradorId(), true);
        }

        BorradorChequera borrador = borradorEmisionService.crearBorrador(datosDe(serie), usuarioLogin);
        return ResultadoIncompleta.borrador(borrador.getBorradorId(), false);
    }

    /**
     * Etapa B: emite el borrador (chequera nueva o actualización de la existente) y extiende el
     * débito, replicando {@code makeChequera} + {@code extendDebito}.
     */
    public ResultadoIncompleta emitir(IncompletasJobTarea tarea, String usuarioLogin) {
        if (tarea.getBorradorId() == null) {
            return ResultadoIncompleta.error("SIN_BORRADOR", "La tarea no tiene borrador asociado");
        }
        BorradorChequera borrador;
        try {
            borrador = borradorEmisionService.cargar(tarea.getBorradorId());
        } catch (RuntimeException e) {
            return ResultadoIncompleta.error("BORRADOR_INEXISTENTE",
                    "No existe el borrador " + tarea.getBorradorId());
        }
        if (borrador.getEstado() != EstadoBorrador.ABIERTO) {
            return ResultadoIncompleta.omitida("BORRADOR_NO_ABIERTO",
                    "El borrador está " + borrador.getEstado() + " (retención o emisión previa)");
        }
        try {
            var resultado = emisionChequeraService.emitir(tarea.getBorradorId(), SolicitudEmision.builder()
                    .version(borrador.getVersion())
                    .forzarNueva(false)
                    .claveIdempotencia("incompletas:" + tarea.getBorradorId())
                    .usuarioLogin(usuarioLogin)
                    .build(), null);
            extenderDebito(tarea);
            return ResultadoIncompleta.emitida(tarea.getBorradorId(), resultado.getChequeraSerieId());
        } catch (EmisionException e) {
            return ResultadoIncompleta.error(e.getCodigo(), e.getMessage());
        } catch (RuntimeException e) {
            log.error("Incompletas: falló la emisión del borrador {}", tarea.getBorradorId(), e);
            return ResultadoIncompleta.error("ERROR_INTERNO", e.getMessage());
        }
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
                .fuente(FuenteCompletado.PLANTILLA)
                .incluyeBarras(false)
                .copiaFacultad(false)
                .build();
    }

    /**
     * Un borrador abierto creado a mano puede tener la beca vieja: la reparación la sincroniza con
     * la serie antes de recalcular las cuotas.
     */
    private BorradorChequera sincronizarBeca(BorradorChequera borrador, ChequeraSerie serie) {
        boolean cambia = !Objects.equals(borrador.getBecaPorcentaje(), serie.getBecaPorcentaje())
                || !Objects.equals(borrador.getBecaResolucion(), serie.getBecaResolucion())
                || !Objects.equals(borrador.getBecaFecha(), serie.getBecaFecha())
                || !Objects.equals(borrador.getBecaUserId(), serie.getBecaUserId())
                || !Objects.equals(borrador.getHpum(), serie.getHpum());
        if (!cambia) {
            return borrador;
        }
        return borradorEmisionService.actualizarBorrador(borrador.getBorradorId(), ActualizarBorradorData.builder()
                .version(borrador.getVersion())
                .becaPorcentaje(serie.getBecaPorcentaje())
                .becaResolucion(serie.getBecaResolucion())
                .becaFecha(serie.getBecaFecha())
                .becaUserId(serie.getBecaUserId())
                .hpum(serie.getHpum())
                .build());
    }

    private void extenderDebito(IncompletasJobTarea tarea) {
        try {
            chequeraService.extendDebito(tarea.getFacultadId(), tarea.getTipoChequeraId(),
                    tarea.getChequeraSerieId());
        } catch (RuntimeException e) {
            // El legacy tampoco evaluaba el resultado de extendDebito.
            log.warn("Incompletas: no se pudo extender el débito de {}/{}/{}: {}",
                    tarea.getFacultadId(), tarea.getTipoChequeraId(), tarea.getChequeraSerieId(), e.getMessage());
        }
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
            log.warn("Incompletas: no se pudieron leer las bajas del padrón: {}", e.getMessage());
            return Set.of();
        }
    }

    private IncompletasJobTarea tareaDe(ChequeraIncompleta serie) {
        String nombre = serie.getPersona() == null ? null
                : (serie.getPersona().getApellido() + ", " + serie.getPersona().getNombre()).trim();
        return IncompletasJobTarea.builder()
                .chequeraId(serie.getChequeraId())
                .personaId(serie.getPersonaId())
                .documentoId(serie.getDocumentoId())
                .facultadId(serie.getFacultadId())
                .tipoChequeraId(serie.getTipoChequeraId())
                .chequeraSerieId(serie.getChequeraSerieId())
                .personaNombre(recortar(nombre, 120))
                .build();
    }

    private static boolean esEnviada(ChequeraIncompleta serie) {
        return serie.getEnviado() != null && serie.getEnviado() == 1;
    }

    private static String recortar(String texto, int largo) {
        return texto == null || texto.length() <= largo ? texto : texto.substring(0, largo);
    }
}
