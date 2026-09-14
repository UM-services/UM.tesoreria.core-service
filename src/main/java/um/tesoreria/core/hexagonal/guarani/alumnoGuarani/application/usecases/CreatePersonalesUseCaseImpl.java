package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonalesResultado;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePersonalesUseCase;
import um.tesoreria.core.hexagonal.personas.documento.application.exception.DocumentoException;
import um.tesoreria.core.hexagonal.personas.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePersonalesUseCaseImpl implements CreatePersonalesUseCase {

    private final DocumentoService documentoService;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;

    @Override
    @Transactional
    public PersonalesResultado createPersonales(AlumnoGuarani alumnoGuarani) {
        Persona persona = null;
        Domicilio domicilio = null;
        try {
            PersonaGuarani personaRel = alumnoGuarani.getPersonaRel();
            String nroDocumento = personaRel.getDocumentoPrincipalRel().getNroDocumento();
            if (nroDocumento == null || !nroDocumento.matches("\\d+")) {
                log.error("nroDocumento '{}' no es numérico; el cliente debe enviar prefijo/posfijo por separado", nroDocumento);
                return PersonalesResultado.builder().result(false).build();
            }
            BigDecimal personaId = new BigDecimal(nroDocumento);
            Integer documentoId = documentoService.findFirstByGuaraniTipoDocumento(
                    personaRel.getDocumentoPrincipalRel().getTipoDocumentoRel().getTipoDocumento())
                    .getDocumentoId();

            try {
                persona = personaService.findByUnique(personaId, documentoId);
                if (requiereSincronizacion(persona, personaRel)) {
                    sincronizarConGuarani(persona, personaRel);
                    persona = personaService.create(persona);
                    if (persona == null) {
                        log.error("La sincronización de la persona devolvió null");
                        return PersonalesResultado.builder().result(false).build();
                    }
                }
            } catch (PersonaException e) {
                log.debug("Persona no encontrada, se intentará crear: {}", e.getMessage());
                persona = Persona.builder()
                        .personaId(personaId)
                        .documentoId(documentoId)
                        .apellido(personaRel.getApellido())
                        .nombre(personaRel.getNombres())
                        .cbu("")
                        .cuit("")
                        .hpum((byte) 0)
                        .primero((byte) 0)
                        .sexo(personaRel.getSexo())
                        .numeroPrefijo(orEmpty(personaRel.getNumeroPrefijo()))
                        .numeroPosfijo(orEmpty(personaRel.getNumeroPosfijo()))
                        .guaraniPersona(personaRel.getPersona())
                        .build();
                persona = personaService.create(persona);
                if (persona == null) {
                    log.error("La creación de la persona devolvió null");
                    return PersonalesResultado.builder().result(false).build();
                }
            }
            log.debug("Persona -> {}", persona.jsonify());

            DatosContactoExtraidos contactosExtraidos = extraerDatosContacto(personaRel);
            try {
                domicilio = domicilioService.findByUnique(persona.getPersonaId(), persona.getDocumentoId());
                if (requiereSincronizacionDomicilio(domicilio, contactosExtraidos)) {
                    sincronizarDomicilioConGuarani(domicilio, contactosExtraidos);
                    domicilio = domicilioService.create(domicilio);
                    if (domicilio == null) {
                        log.error("La sincronización del domicilio devolvió null");
                        return PersonalesResultado.builder().result(false).persona(persona).build();
                    }
                }
            } catch (DomicilioException e) {
                log.debug("Domicilio no encontrado, se intentará crear: {}", e.getMessage());
                domicilio = Domicilio.builder()
                        .personaId(persona.getPersonaId())
                        .documentoId(persona.getDocumentoId())
                        .fecha(OffsetDateTime.now())
                        .emailPersonal(contactosExtraidos.emailPersonal())
                        .emailInstitucional(contactosExtraidos.emailInstitucional())
                        .calle("")
                        .puerta("")
                        .codigoPostal("")
                        .piso("")
                        .dpto("")
                        .laboral(contactosExtraidos.laboral())
                        .telefono(contactosExtraidos.telefono())
                        .movil(contactosExtraidos.movil())
                        .observaciones(contactosExtraidos.observaciones())
                        .build();
                domicilio = domicilioService.create(domicilio);
                if (domicilio == null) {
                    log.error("La creación del domicilio devolvió null");
                    return PersonalesResultado.builder().result(false).persona(persona).build();
                }
            }
            log.debug("Domicilio -> {}", domicilio.jsonify());
            return PersonalesResultado.builder().result(true).persona(persona).domicilio(domicilio).build();
        } catch (DocumentoException e) {
            log.error("No se pudo obtener el tipo de documento -> {}", e.getMessage());
            return PersonalesResultado.builder().result(false).build();
        } catch (RuntimeException e) {
            log.error("No se pudo completar la creación de personales -> {}", e.getMessage());
            return PersonalesResultado.builder().result(false).build();
        }
    }

    private boolean requiereSincronizacion(Persona persona, PersonaGuarani personaRel) {
        if (persona == null || personaRel == null) {
            return false;
        }
        return estaPendiente(persona.getNumeroPrefijo(), personaRel.getNumeroPrefijo())
                || estaPendiente(persona.getNumeroPosfijo(), personaRel.getNumeroPosfijo())
                || (persona.getGuaraniPersona() == null && personaRel.getPersona() != null);
    }

    private void sincronizarConGuarani(Persona persona, PersonaGuarani personaRel) {
        if (estaPendiente(persona.getNumeroPrefijo(), personaRel.getNumeroPrefijo())) {
            persona.setNumeroPrefijo(personaRel.getNumeroPrefijo());
        }
        if (estaPendiente(persona.getNumeroPosfijo(), personaRel.getNumeroPosfijo())) {
            persona.setNumeroPosfijo(personaRel.getNumeroPosfijo());
        }
        if (persona.getGuaraniPersona() == null && personaRel.getPersona() != null) {
            persona.setGuaraniPersona(personaRel.getPersona());
        }
    }

    private boolean requiereSincronizacionDomicilio(Domicilio domicilio, DatosContactoExtraidos contactos) {
        if (domicilio == null || contactos == null) {
            return false;
        }
        return estaPendiente(domicilio.getEmailPersonal(), contactos.emailPersonal())
                || estaPendiente(domicilio.getEmailInstitucional(), contactos.emailInstitucional())
                || estaPendiente(domicilio.getMovil(), contactos.movil())
                || estaPendiente(domicilio.getTelefono(), contactos.telefono())
                || estaPendiente(domicilio.getLaboral(), contactos.laboral())
                || estaPendiente(domicilio.getObservaciones(), contactos.observaciones());
    }

    private void sincronizarDomicilioConGuarani(Domicilio domicilio, DatosContactoExtraidos contactos) {
        if (estaPendiente(domicilio.getEmailPersonal(), contactos.emailPersonal())) {
            domicilio.setEmailPersonal(contactos.emailPersonal());
        }
        if (estaPendiente(domicilio.getEmailInstitucional(), contactos.emailInstitucional())) {
            domicilio.setEmailInstitucional(contactos.emailInstitucional());
        }
        if (estaPendiente(domicilio.getMovil(), contactos.movil())) {
            domicilio.setMovil(contactos.movil());
        }
        if (estaPendiente(domicilio.getTelefono(), contactos.telefono())) {
            domicilio.setTelefono(contactos.telefono());
        }
        if (estaPendiente(domicilio.getLaboral(), contactos.laboral())) {
            domicilio.setLaboral(contactos.laboral());
        }
        if (estaPendiente(domicilio.getObservaciones(), contactos.observaciones())) {
            domicilio.setObservaciones(contactos.observaciones());
        }
    }

    private DatosContactoExtraidos extraerDatosContacto(PersonaGuarani personaRel) {
        if (personaRel == null) {
            return new DatosContactoExtraidos("", "", "", "", "", "");
        }

        var contactos = personaRel.getContactos() != null ? personaRel.getContactos() : java.util.Collections.<um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.ContactoGuarani>emptyList();

        String emailPersonal = "";
        String emailInstitucional = "";
        String movil = "";
        String telefono = "";
        String laboral = "";
        StringBuilder otros = new StringBuilder();

        // 1. Identificar correos
        for (var c : contactos) {
            if (esTipo(c, "MP") && tieneTexto(c.getEmail())) {
                emailPersonal = c.getEmail().trim();
                break;
            }
        }
        for (var c : contactos) {
            if (esTipo(c, "MI") && tieneTexto(c.getEmail())) {
                emailInstitucional = c.getEmail().trim();
                break;
            }
        }
        if (emailPersonal.isEmpty()) {
            for (var c : contactos) {
                if (!esTipo(c, "MI") && tieneTexto(c.getEmail())) {
                    emailPersonal = c.getEmail().trim();
                    break;
                }
            }
        }
        if (emailPersonal.isEmpty() && tieneTexto(personaRel.getEmailTemporal())) {
            emailPersonal = personaRel.getEmailTemporal().trim();
        }

        // 2. Identificar teléfonos por tipo preferente
        for (var c : contactos) {
            String formattedPhone = formatearTelefono(c.getTelefonoCodigoArea(), c.getTelefonoNumero());
            if (formattedPhone.isEmpty()) {
                continue;
            }
            if (esTipo(c, "C") && movil.isEmpty()) {
                movil = formattedPhone;
            } else if (esTipo(c, "TF") && telefono.isEmpty()) {
                telefono = formattedPhone;
            } else if (esTipo(c, "TL") && laboral.isEmpty()) {
                laboral = formattedPhone;
            }
        }

        // 3. Fallback: asignar números de teléfono no clasificados
        for (var c : contactos) {
            String formattedPhone = formatearTelefono(c.getTelefonoCodigoArea(), c.getTelefonoNumero());
            if (formattedPhone.isEmpty()) {
                continue;
            }
            if (formattedPhone.equals(movil) || formattedPhone.equals(telefono) || formattedPhone.equals(laboral)) {
                continue;
            }
            if (movil.isEmpty()) {
                movil = formattedPhone;
            } else if (telefono.isEmpty()) {
                telefono = formattedPhone;
            } else if (laboral.isEmpty()) {
                laboral = formattedPhone;
            }
        }

        // 4. Otros contactos / observaciones
        for (var c : contactos) {
            if (tieneTexto(c.getOtrosContactos())) {
                if (!otros.isEmpty()) {
                    otros.append(" | ");
                }
                otros.append(c.getOtrosContactos().trim());
            }
        }

        return new DatosContactoExtraidos(
                limitar(emailPersonal, 100),
                limitar(emailInstitucional, 100),
                limitar(movil, 100),
                limitar(telefono, 100),
                limitar(laboral, 100),
                limitar(otros.toString(), 100)
        );
    }

    private boolean esTipo(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.ContactoGuarani contacto, String tipoBuscado) {
        return contacto != null && contacto.getContactoTipo() != null && contacto.getContactoTipo().trim().equalsIgnoreCase(tipoBuscado);
    }

    private boolean tieneTexto(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private String formatearTelefono(String codigoArea, String numero) {
        if (!tieneTexto(numero)) {
            return "";
        }
        String num = numero.trim();
        if (tieneTexto(codigoArea)) {
            return "(" + codigoArea.trim() + ") " + num;
        }
        return num;
    }

    private String limitar(String str, int maxLen) {
        if (str == null) {
            return "";
        }
        return str.length() > maxLen ? str.substring(0, maxLen) : str;
    }

    private record DatosContactoExtraidos(
            String emailPersonal,
            String emailInstitucional,
            String movil,
            String telefono,
            String laboral,
            String observaciones
    ) {}

    private boolean estaPendiente(String valorActual, String valorGuarani) {
        return (valorActual == null || valorActual.trim().isEmpty()) && valorGuarani != null && !valorGuarani.trim().isEmpty();
    }

    private String orEmpty(String valor) {
        return valor == null ? "" : valor;
    }

}
