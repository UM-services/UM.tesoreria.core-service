package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.AgrupadorMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.FuenteMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriaAgrupable;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.ports.in.GetMateriasInscriptasUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service.PendienteInfoFacultadService;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.MateriaCursada;

import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.ports.out.FindInscripcionesGuaraniPort;
import um.tesoreria.core.hexagonal.extern.guarani.cursada.domain.model.InscripcionMateriasGuarani;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Arma las materias inscriptas de una chequera de grado con los datos de SIUM y Guaraní, sin mezclar fuentes.
 * <ul>
 *   <li>La chequera debe existir: si no, {@code ChequeraSerieException} sube al controller.</li>
 *   <li>Sin clase de chequera, o con {@code grado != 1}: lista vacía, sin llamar a la facultad.</li>
 *   <li>Cualquier {@code RuntimeException} de la llamada, la conversión o el agrupado da
 *       {@code SIUM/NO_DISPONIBLE} y un {@code warn} con facultad, chequera, clase de la excepción y status HTTP.
 *       El {@code warn} nunca lleva el mensaje, el cuerpo, la URL ni el stack trace.</li>
 *   <li>Sin inscripción: {@code SIUM/SIN_INSCRIPCION}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetMateriasInscriptasUseCaseImpl implements GetMateriasInscriptasUseCase {

    private static final byte GRADO = 1;

    private final ChequeraSerieService chequeraSerieService;
    private final TipoChequeraService tipoChequeraService;
    private final PendienteInfoFacultadService pendienteInfoFacultadService;
    private final FacultadService facultadService;
    private final LectivoService lectivoService;
    private final FindInscripcionesGuaraniPort guarani;

    @Override
    public List<MateriasEstado> getMateriasInscriptas(Integer facultadId, Integer tipoChequeraId,
                                                      Long chequeraSerieId) {
        log.debug("Processing GetMateriasInscriptasUseCaseImpl.getMateriasInscriptas");
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        TipoChequera tipoChequera = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId());
        if (!esDeGrado(tipoChequera)) {
            return List.of();
        }
        List<MateriasEstado> fuentes = new ArrayList<>();
        fuentes.add(materiasDeSium(serie));
        fuentes.addAll(materiasDeGuarani(serie));
        return List.copyOf(fuentes);
    }

    private boolean esDeGrado(TipoChequera tipoChequera) {
        return tipoChequera.getClaseChequera() != null
                && tipoChequera.getClaseChequera().getGrado() != null
                && tipoChequera.getClaseChequera().getGrado() == GRADO;
    }

    private MateriasEstado materiasDeSium(ChequeraSerie serie) {
        try {
            Optional<InscripcionMaterias> inscripcion = pendienteInfoFacultadService.findByAlumno(
                    serie.getFacultadId(), serie.getLectivoId(), serie.getGeograficaId(), serie.getPersonaId(),
                    serie.getDocumentoId());
            return inscripcion.map(this::conDatos).orElseGet(() -> sinDatos(EstadoMaterias.SIN_INSCRIPCION));
        } catch (RuntimeException e) {
            Integer status = e instanceof RestClientResponseException respuesta
                    ? respuesta.getStatusCode().value() : null;
            log.warn("Materias SIUM no disponibles: facultadId={} chequeraSerieId={} excepcion={} status={}",
                    serie.getFacultadId(), serie.getChequeraSerieId(), e.getClass().getName(), status);
            return sinDatos(EstadoMaterias.NO_DISPONIBLE);
        }
    }

    private MateriasEstado conDatos(InscripcionMaterias inscripcion) {
        List<MateriaAgrupable> materias = inscripcion.materias().stream().map(this::aAgrupable).toList();
        return new MateriasEstado(FuenteMaterias.SIUM, EstadoMaterias.OK, inscripcion.curso(), inscripcion.plan(),
                inscripcion.carrera(), inscripcion.observaciones(), AgrupadorMaterias.agrupar(materias));
    }

    private MateriaAgrupable aAgrupable(MateriaCursada materia) {
        PeriodoCursada periodo = materia.periodo() == null ? null : PeriodoCursada.desde(materia.periodo());
        return new MateriaAgrupable(materia.facultadId(), materia.planId(), materia.materiaId(), materia.nombre(),
                materia.curso(), periodo);
    }

    private List<MateriasEstado> materiasDeGuarani(ChequeraSerie serie) {
        try {
            var facultad = facultadService.findByFacultadId(serie.getFacultadId());
            if (facultad == null || facultad.getGuaraniResponsableAcademica() == null) return List.of();
            var lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
            var inscripciones = guarani.findByDocumentoAndAnioAcademico(serie.getPersonaId().toPlainString(),
                    lectivo.getFechaInicio().getYear(), facultad.getGuaraniResponsableAcademica());
            if (inscripciones.isEmpty()) return List.of(sinDatosGuarani(EstadoMaterias.SIN_INSCRIPCION));
            return inscripciones.stream().map(i -> conDatosGuarani(serie.getFacultadId(), i)).toList();
        } catch (RuntimeException e) {
            // Nunca registrar el mensaje, la URL, el cuerpo o la excepción como Throwable.
            Integer status = e instanceof feign.FeignException respuesta ? respuesta.status() : null;
            log.warn("Materias GUARANI no disponibles: facultadId={} chequeraSerieId={} excepcion={} status={}",
                    serie.getFacultadId(), serie.getChequeraSerieId(), e.getClass().getName(), status);
            return List.of(sinDatosGuarani(EstadoMaterias.NO_DISPONIBLE));
        }
    }

    private MateriasEstado conDatosGuarani(Integer facultadId, InscripcionMateriasGuarani inscripcion) {
        var materias = inscripcion.materias().stream().map(m -> new MateriaAgrupable(
                facultadId, inscripcion.planVersion(), m.actividadCodigo(), m.nombre(), m.curso(),
                periodoGuarani(m.periodo()), m.periodoLectivo())).toList();
        return new MateriasEstado(FuenteMaterias.GUARANI, EstadoMaterias.OK, inscripcion.cursoInscripcion(),
                "Versión " + inscripcion.planVersion(), inscripcion.carrera(), null,
                AgrupadorMaterias.agrupar(materias, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private PeriodoCursada periodoGuarani(String periodo) {
        return switch (periodo) {
            case "ANUAL" -> PeriodoCursada.ANUAL;
            case "PRIMER_SEMESTRE" -> PeriodoCursada.PRIMER_SEMESTRE;
            case "SEGUNDO_SEMESTRE" -> PeriodoCursada.SEGUNDO_SEMESTRE;
            case "OTRO" -> null;
            case null -> null;
            default -> throw new IllegalArgumentException("Período Guaraní fuera del contrato");
        };
    }

    private MateriasEstado sinDatosGuarani(EstadoMaterias estado) {
        return new MateriasEstado(FuenteMaterias.GUARANI, estado, null, null, null, null, List.of());
    }

    private MateriasEstado sinDatos(EstadoMaterias estado) {
        return new MateriasEstado(FuenteMaterias.SIUM, estado, null, null, null, null, List.of());
    }
}
