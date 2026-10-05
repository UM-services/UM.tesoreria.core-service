package um.tesoreria.core.hexagonal.umhub.consulta.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDomicilio;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.TipoDocumentoConsulta;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaPersonaUseCase;
import um.tesoreria.core.service.LocalidadService;
import um.tesoreria.core.service.ProvinciaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Resuelve la identidad de una persona a partir del numero de documento (sin tipo).
 * Regla de colision: un mismo numero puede estar registrado bajo varios tipos
 * (LE/LC comparten numero con DNI). Se toma como primaria la fila de menor
 * documentoId; si otras filas del numero tienen nombre o apellido distintos
 * (colision fortuita entre personas diferentes) se excluyen y solo se expone la primaria.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GetConsultaPersonaUseCaseImpl implements GetConsultaPersonaUseCase {

    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final DocumentoService documentoService;
    private final ProvinciaService provinciaService;
    private final LocalidadService localidadService;

    @Override
    public Optional<ConsultaPersona> findByNumeroDocumento(BigDecimal numeroDocumento) {
        List<Persona> filas = personaService.findAllByNumeroDocumento(numeroDocumento);
        if (filas.isEmpty()) {
            return Optional.empty();
        }

        Persona primaria = filas.getFirst();
        List<Persona> mismoTitular = filas.stream()
                .filter(fila -> mismaIdentidad(primaria, fila))
                .toList();
        if (mismoTitular.size() < filas.size()) {
            log.warn("Numero de documento {} tiene filas con titulares divergentes: "
                    + "se expone solo la identidad primaria (documentoId {})",
                    numeroDocumento, primaria.getDocumentoId());
        }

        return Optional.of(ConsultaPersona.builder()
                .numeroDocumento(primaria.getPersonaId())
                .nombre(primaria.getNombre())
                .apellido(primaria.getApellido())
                .sexo(primaria.getSexo())
                .numeroPrefijo(primaria.getNumeroPrefijo())
                .numeroPosfijo(primaria.getNumeroPosfijo())
                .documentos(mismoTitular.stream()
                        .map(fila -> TipoDocumentoConsulta.builder()
                                .documentoId(fila.getDocumentoId())
                                .nombre(resolverNombreDocumento(fila.getDocumentoId()))
                                .build())
                        .toList())
                .domicilio(domicilioService.findFirstByPersonaId(numeroDocumento)
                        .map(this::toConsultaDomicilio)
                        .orElse(null))
                .build());
    }

    private boolean mismaIdentidad(Persona a, Persona b) {
        return normalizado(a.getApellido()).equals(normalizado(b.getApellido()))
                && normalizado(a.getNombre()).equals(normalizado(b.getNombre()));
    }

    private String normalizado(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase();
    }

    private String resolverNombreDocumento(Integer documentoId) {
        try {
            Documento documento = documentoService.findByDocumentoId(documentoId);
            return documento == null ? null : documento.getNombre();
        } catch (RuntimeException e) {
            log.debug("No se pudo resolver el nombre del tipo de documento {}", documentoId);
            return null;
        }
    }

    private ConsultaDomicilio toConsultaDomicilio(Domicilio domicilio) {
        return ConsultaDomicilio.builder()
                .calle(domicilio.getCalle())
                .puerta(domicilio.getPuerta())
                .piso(domicilio.getPiso())
                .dpto(domicilio.getDpto())
                .codigoPostal(domicilio.getCodigoPostal())
                .provinciaId(domicilio.getProvinciaId())
                .provinciaNombre(resolverNombreProvincia(domicilio))
                .localidadId(domicilio.getLocalidadId())
                .localidadNombre(resolverNombreLocalidad(domicilio))
                .telefono(domicilio.getTelefono())
                .movil(domicilio.getMovil())
                .emailPersonal(domicilio.getEmailPersonal())
                .emailInstitucional(domicilio.getEmailInstitucional())
                .build();
    }

    private String resolverNombreProvincia(Domicilio domicilio) {
        if (domicilio.getFacultadId() == null || domicilio.getProvinciaId() == null) {
            return null;
        }
        try {
            return provinciaService.findByUnique(domicilio.getFacultadId(), domicilio.getProvinciaId()).getNombre();
        } catch (RuntimeException e) {
            log.debug("No se pudo resolver la provincia facultad={} provincia={}",
                    domicilio.getFacultadId(), domicilio.getProvinciaId());
            return null;
        }
    }

    private String resolverNombreLocalidad(Domicilio domicilio) {
        if (domicilio.getFacultadId() == null || domicilio.getProvinciaId() == null
                || domicilio.getLocalidadId() == null) {
            return null;
        }
        try {
            return localidadService
                    .findByUnique(domicilio.getFacultadId(), domicilio.getProvinciaId(), domicilio.getLocalidadId())
                    .getNombre();
        } catch (RuntimeException e) {
            log.debug("No se pudo resolver la localidad facultad={} provincia={} localidad={}",
                    domicilio.getFacultadId(), domicilio.getProvinciaId(), domicilio.getLocalidadId());
            return null;
        }
    }
}
