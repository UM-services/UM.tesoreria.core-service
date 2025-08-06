/**
 *
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.client.tesoreria.mercadopago.PreferenceClient;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.LegajoFacultadConsumer;
import um.tesoreria.core.extern.consumer.PreInscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.extern.model.kotlin.InscripcionFacultad;
import um.tesoreria.core.extern.model.kotlin.LegajoFacultad;
import um.tesoreria.core.extern.model.kotlin.PreInscripcionFacultad;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.model.dto.DeudaPersonaDto;
import um.tesoreria.core.model.dto.VencimientoDto;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.repository.PersonaRepository;
import um.tesoreria.core.service.view.PersonaKeyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 */
@Service
@Slf4j
public class PersonaService {

    private final PersonaRepository repository;
    private final PersonaKeyService personaKeyService;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;
    private final PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;
    private final FacultadService facultadService;
    private final ChequeraSerieService chequeraSerieService;
    private final CarreraChequeraService carreraChequeraService;
    private final LegajoFacultadConsumer legajoFacultadConsumer;
    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoContextService mercadoPagoContextService;
    private final PreferenceClient preferenceClient;

    public PersonaService(PersonaRepository repository, PersonaKeyService personaKeyService, InscripcionFacultadConsumer inscripcionFacultadConsumer, PreInscripcionFacultadConsumer preInscripcionFacultadConsumer, FacultadService facultadService, ChequeraSerieService chequeraSerieService, CarreraChequeraService carreraChequeraService, LegajoFacultadConsumer legajoFacultadConsumer, ChequeraCuotaService chequeraCuotaService, MercadoPagoContextService mercadoPagoContextService, PreferenceClient preferenceClient) {
        this.repository = repository;
        this.personaKeyService = personaKeyService;
        this.inscripcionFacultadConsumer = inscripcionFacultadConsumer;
        this.preInscripcionFacultadConsumer = preInscripcionFacultadConsumer;
        this.facultadService = facultadService;
        this.chequeraSerieService = chequeraSerieService;
        this.carreraChequeraService = carreraChequeraService;
        this.legajoFacultadConsumer = legajoFacultadConsumer;
        this.chequeraCuotaService = chequeraCuotaService;
        this.mercadoPagoContextService = mercadoPagoContextService;
        this.preferenceClient = preferenceClient;
    }

    public Persona findByUnique(BigDecimal personaId, Integer documentoId) {
        return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .orElseThrow(() -> new PersonaException(personaId, documentoId));
    }

    public Persona findByPersonaId(BigDecimal personaId) {
        return repository.findTopByPersonaId(personaId).orElseThrow(() -> new PersonaException(personaId));
    }

    public List<Persona> findAllSantander() {
        return repository.findAllByCbuLike("072%");
    }

    public DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId) {
        var deudaCuotas = 0;
        var deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        for (ChequeraSerie chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
                null)) {
            DeudaChequeraDto deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
            if (deuda.getCuotas() > 0) {
                deudas.add(deuda);
                deudaCuotas += deuda.getCuotas();
                deudaTotal = deudaTotal.add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        DeudaPersonaDto deudaPersonaDto = DeudaPersonaDto.builder()
                .personaId(personaId)
                .documentoId(documentoId)
                .cuotas(deudaCuotas)
                .deuda(deudaTotal)
                .deudas(deudas)
                .vencimientos(new ArrayList<>())
                .build();
        logDeudaPersona(deudaPersonaDto);
        return deudaPersonaDto;
    }

    public DeudaPersonaDto deudaByPersonaExtended(BigDecimal personaId, Integer documentoId) {
        log.debug("Processing deudaByPersonaExtended");
        var deudaCuotas = 0;
        var deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        List<VencimientoDto> vencimientoDtos = new ArrayList<>();
        for (ChequeraSerie chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
                null)) {
            logChequeraSerie(chequera);
            DeudaChequeraDto deuda = chequeraCuotaService.calculateDeudaExtended(chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
            logDeuda(deuda);
            if (deuda.getCuotas() > 0) {
                deudas.add(deuda);
                deudaCuotas += deuda.getCuotas();
                deudaTotal = deudaTotal.add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP);
            }
            for (ChequeraCuota chequeraCuota : chequeraCuotaService
                    .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(chequera.getFacultadId(),
                            chequera.getTipoChequeraId(), chequera.getChequeraSerieId(), chequera.getAlternativaId())) {
                logChequeraCuota(chequeraCuota);
                if (chequeraCuota.getPagado() == 0 && chequeraCuota.getBaja() == 0
                        && chequeraCuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {
                    log.debug("Creando preferencia");
                    preferenceClient.createPreference(chequeraCuota.getChequeraCuotaId());
                    MercadoPagoContext mercadoPagoContext = null;
                    try {
                        mercadoPagoContext = mercadoPagoContextService.findActiveByChequeraCuotaId(chequeraCuota.getChequeraCuotaId());
                        logMercadoPagoContext(mercadoPagoContext);
                    } catch (MercadoPagoContextException e) {
                        log.error("Error al obtener el contexto de MercadoPago para el cuota {}", chequeraCuota.getChequeraCuotaId());
                    }
                    if (mercadoPagoContext != null) {
                        // Formatear la fecha de vencimiento
                        String fechaVencimientoFormatted = mercadoPagoContext.getFechaVencimiento()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        vencimientoDtos.add(VencimientoDto.builder()
                                .chequeraCuotaId(chequeraCuota.getChequeraCuotaId())
                                .mercadoPagoContextId(mercadoPagoContext.getMercadoPagoContextId())
                                .producto(Objects.requireNonNull(chequeraCuota.getProducto()).getNombre())
                                .periodo(chequeraCuota.getMes() + "/" + chequeraCuota.getAnho())
                                .vencimiento(fechaVencimientoFormatted)
                                .importe(mercadoPagoContext.getImporte())
                                .initPoint(mercadoPagoContext.getInitPoint())
                                .build());
                    }
                }
            }
        }

        DeudaPersonaDto deudaPersonaDto = DeudaPersonaDto.builder()
                .personaId(personaId)
                .documentoId(documentoId)
                .cuotas(deudaCuotas)
                .deuda(deudaTotal)
                .deudas(deudas)
                .vencimientos(vencimientoDtos)
                .build();
        logDeudaPersona(deudaPersonaDto);
        return deudaPersonaDto;
    }

    private void logDeudaPersona(DeudaPersonaDto deudaPersonaDto) {
        try {
            log.debug("DeudaPersona -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(deudaPersonaDto));
        } catch (JsonProcessingException e) {
            log.debug("DeudaPersona error -> {}", e.getMessage());
        }
    }

    private void logMercadoPagoContext(MercadoPagoContext mercadoPagoContext) {
        try {
            log.debug("MercadoPagoContext -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(mercadoPagoContext));
        } catch (JsonProcessingException e) {
            log.debug("MercadoPagoContext error -> {}", e.getMessage());
        }
    }

    private void logChequeraCuota(ChequeraCuota chequeraCuota) {
        try {
            log.debug("ChequeraCuota -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(chequeraCuota));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraCuota error -> {}", e.getMessage());
        }
    }

    private void logDeuda(DeudaChequeraDto deuda) {
        try {
            log.debug("DeudaChequera -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(deuda));
        } catch (JsonProcessingException e) {
            log.debug("DeudaChequera error -> {}", e.getMessage());
        }
    }

    public List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                         Integer curso) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
                .findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId, curso)
                .stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
                        (inscripto, replacement) -> inscripto));
        // Elimina los que ya tengan chequera
        Map<String, InscripcionFacultad> pendientes = new HashMap<String, InscripcionFacultad>();
        for (InscripcionFacultad inscripto : inscriptos.values()) {
            boolean add = true;
            try {
                ChequeraSerie chequeraSerie;
                if (facultadId == 15) {
                    chequeraSerie = chequeraSerieService.findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                            inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId, geograficaId);
                } else {
                    chequeraSerie = chequeraSerieService.findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                            inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId, geograficaId);
                }
                logChequeraSerie(chequeraSerie);
                add = false;
            } catch (ChequeraSerieException e) {
                log.debug("Sin chequera");
            }
            // Verifica los alumnos de intercambio
            if (add) {
                try {
                    LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
                            facultad.getApiport(), inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId);
                    if (legajo.getIntercambio() == 1) {
                        add = false;
                    }
                } catch (Exception e) {
                    log.debug("Sin legajo");
                }
            }
            // Agrega el alumno
            if (add) {
                pendientes.put(inscripto.getPersonaKey(), inscripto);
            }
        }
        return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }

    public List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId,
                                                                Integer geograficaId, Integer claseChequeraId, Integer curso) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
                .findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId, curso)
                .stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
                        (inscripto, replacemente) -> inscripto));
        Map<String, CarreraChequera> tipos = carreraChequeraService
                .findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(facultadId, lectivoId,
                        geograficaId, claseChequeraId, curso)
                .stream().collect(Collectors.toMap(CarreraChequera::getCarreraKey, Function.identity(),
                        (tipo, replacement) -> tipo));
        // Elimina los que ya tengan chequera
        Map<String, InscripcionFacultad> pendientes = new HashMap<String, InscripcionFacultad>();
        for (InscripcionFacultad inscripto : inscriptos.values()) {
            CarreraChequera carreraChequera = null;
            if (!tipos.containsKey(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId()))
                continue;
            carreraChequera = tipos.get(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId());
            boolean add = true;
            try {
                var chequeraSerie = chequeraSerieService
                        .findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
                                inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId,
                                geograficaId, carreraChequera.getTipoChequeraId());
                add = false;
            } catch (ChequeraSerieException e) {
                log.debug("Sin chequera");
            }
            // Verifica los alumnos de intercambio
            if (add) {
                try {
                    LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
                            facultad.getApiport(), inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId);
                    if (legajo.getIntercambio() == 1) {
                        add = false;
                    }
                } catch (Exception e) {
                    log.debug("Sin legajo");
                }
            }
            // Agrega el alumno
            if (add) {
                pendientes.put(inscripto.getPersonaKey(), inscripto);
            }
        }
        return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }

    public List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId,
                                                            Integer geograficaId) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        Map<String, PreInscripcionFacultad> preinscriptos = preInscripcionFacultadConsumer
                .findAllByPreInscriptos(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
                        geograficaId)
                .stream().collect(Collectors.toMap(PreInscripcionFacultad::getPersonaKey, Function.identity(),
                        (preinscripto, replacement) -> preinscripto));
        // Elimina los que ya tengan chequera
        Map<String, PreInscripcionFacultad> pendientes = new HashMap<String, PreInscripcionFacultad>();
        for (PreInscripcionFacultad preinscripto : preinscriptos.values()) {
            boolean add = true;
            try {
                var chequeraSerie = chequeraSerieService
                        .findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                                preinscripto.getPersonaId(), preinscripto.getDocumentoId(), facultadId, lectivoId - 1,
                                geograficaId);
                logChequeraSerie(chequeraSerie);
                add = false;
            } catch (ChequeraSerieException e) {
                log.debug("Sin chequera");
            }
            // Agrega el alumno
            if (add) {
                pendientes.put(preinscripto.getPersonaKey(), preinscripto);
            }
        }
        return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }

    public List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                     Integer cuotas) {
        List<String> unifieds = new ArrayList<String>();
        for (ChequeraSerie serie : chequeraSerieService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId,
                facultadId, geograficaId)) {
            if (cuotas <= chequeraCuotaService.findAllDebidas(facultadId, serie.getTipoChequeraId(),
                    serie.getChequeraSerieId(), serie.getAlternativaId()).size()) {
                unifieds.add(serie.getPersonaKey());
            }
        }
        return personaKeyService.findAllByUnifiedIn(unifieds,
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
    }

    public List<PersonaKey> findByUnifieds(List<String> unifieds) {
        return personaKeyService.findAllByUnifiedIn(unifieds, Sort.by("apellido").ascending());
    }

    public List<PersonaKey> findByStrings(List<String> conditions) {
        return personaKeyService.findAllByStrings(conditions);
    }

    public Persona findByUniqueId(Long uniqueId) {
        return repository.findByUniqueId(uniqueId).orElseThrow(() -> new PersonaException(uniqueId));
    }

    public Persona add(Persona persona) {
        persona = repository.save(persona);
        return persona;
    }

    public Persona update(Persona newpersona, Long uniqueId) {
        return repository.findByUniqueId(uniqueId).map(persona -> {
            persona = new Persona(uniqueId, newpersona.getPersonaId(), newpersona.getDocumentoId(),
                    newpersona.getApellido(), newpersona.getNombre(), newpersona.getSexo(), newpersona.getPrimero(),
                    newpersona.getCuit(), newpersona.getCbu(), newpersona.getPassword());
            repository.save(persona);
            return persona;
        }).orElseThrow(() -> new PersonaException(uniqueId));
    }

    private void logChequeraSerie(ChequeraSerie chequeraSerie) {
        try {
            log.debug("ChequeraSerie -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraSerie));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerie jsonify error -> {}", e.getMessage());
        }
    }

    public InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        var facultad = facultadService.findByFacultadId(facultadId);
        return inscripcionFacultadConsumer.findInscripcionFull(facultad.getApiserver(), facultad.getApiport(), facultadId, personaId, documentoId, lectivoId);
    }

}
