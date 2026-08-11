package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.FindPreuniversitarioFromDatosGuaraniUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieSpecialQueriesUseCase;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in.GetFacultadByGuaraniResponsableAcademicaUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in.GetGuaraniUbicacionByUbicacionUseCase;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.in.GetDocumentoByGuaraniTipoDocumentoUseCase;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindPreuniversitarioFromDatosGuaraniUseCaseImpl
        implements FindPreuniversitarioFromDatosGuaraniUseCase {

    private final GetDocumentoByGuaraniTipoDocumentoUseCase getDocumentoUseCase;
    private final GetGuaraniUbicacionByUbicacionUseCase getUbicacionUseCase;
    private final GetFacultadByGuaraniResponsableAcademicaUseCase getFacultadUseCase;
    private final GetChequeraSerieSpecialQueriesUseCase getChequeraSerieUseCase;

    @Override
    public Optional<ChequeraSerie> find(BigDecimal nroDocumento, Integer tipoDocumento, Integer ubicacion,
                                        Integer responsableAcademica, Integer lectivoId) {
        log.debug("FindPreuniversitarioFromDatosGuarani.start nroDocumento={}, tipoDocumento={}, ubicacion={}, "
                        + "responsableAcademica={}, lectivoId={}",
                nroDocumento, tipoDocumento, ubicacion, responsableAcademica, lectivoId);

        try {
            log.debug("Buscando documento para tipoDocumento={}", tipoDocumento);
            var documentoOptional = getDocumentoUseCase.getDocumentoByGuaraniTipoDocumento(tipoDocumento);
            if (documentoOptional.isEmpty()) {
                log.warn("No se encontró documento para tipoDocumento={}", tipoDocumento);
                return Optional.empty();
            }
            var documento = documentoOptional.get();
            log.debug("Documento encontrado documentoId={}", documento.getDocumentoId());

            log.debug("Buscando ubicación para ubicacion={}", ubicacion);
            var ubicacionOptional = getUbicacionUseCase.getByUbicacion(ubicacion);
            if (ubicacionOptional.isEmpty()) {
                log.warn("No se encontró ubicación para ubicacion={}", ubicacion);
                return Optional.empty();
            }
            var guaraniUbicacion = ubicacionOptional.get();
            log.debug("Ubicación encontrada ubicacion={}, geograficaId={}", ubicacion,
                    guaraniUbicacion.getGeograficaId());

            log.debug("Buscando facultad para responsableAcademica={}", responsableAcademica);
            var facultadOptional = getFacultadUseCase.getByGuaraniResponsableAcademica(responsableAcademica);
            if (facultadOptional.isEmpty()) {
                log.warn("No se encontró facultad para responsableAcademica={}", responsableAcademica);
                return Optional.empty();
            }
            var facultad = facultadOptional.get();
            log.debug("Facultad encontrada facultadId={}", facultad.getFacultadId());

            log.debug("Buscando chequera preuniversitaria con nroDocumento={}, documentoId={}, facultadId={}, "
                            + "lectivoId={}, geograficaId={}",
                    nroDocumento, documento.getDocumentoId(), facultad.getFacultadId(), lectivoId,
                    guaraniUbicacion.getGeograficaId());
            var resultado = getChequeraSerieUseCase
                    .findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                            nroDocumento,
                            documento.getDocumentoId(),
                            facultad.getFacultadId(),
                            lectivoId,
                            guaraniUbicacion.getGeograficaId());

            if (resultado.isPresent()) {
                log.debug("Chequera preuniversitaria encontrada");
            } else {
                log.warn("No se encontró chequera preuniversitaria para los parámetros indicados");
            }
            return resultado;
        } catch (RuntimeException exception) {
            log.error("Error en FindPreuniversitarioFromDatosGuarani.find con nroDocumento={}, tipoDocumento={}, "
                            + "ubicacion={}, responsableAcademica={}, lectivoId={}",
                    nroDocumento, tipoDocumento, ubicacion, responsableAcademica, lectivoId, exception);
            throw exception;
        }
    }
}
