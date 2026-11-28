package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorSede;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PaginaBorradores;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoItemResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorListadoResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final LectivoService lectivoService;
    private final PersonaKeyService personaKeyService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public BorradorListadoResponse listar(BorradorListadoRequest request) {
        if (request == null || request.lectivoId() == null || request.facultadId() == null
                || request.geograficaId() == null) {
            return BorradorListadoResponse.builder().items(List.of()).total(0).page(0).size(0).build();
        }
        int page = request.page() == null || request.page() < 0 ? 0 : request.page();
        int size = request.size() == null || request.size() <= 0 ? 20 : Math.min(request.size(), 200);
        List<Integer> tipos = tiposDeSede(request.geograficaId());
        PaginaBorradores pagina = borradorRepository.findAllByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(
                request.lectivoId(), request.facultadId(), tipos, EstadoBorrador.ABIERTO, page, size);
        if (pagina.items().isEmpty()) {
            return BorradorListadoResponse.builder().items(List.of()).total(pagina.total())
                    .page(page).size(size).build();
        }

        Map<Integer, String> facultades = facultades();
        Map<Integer, String> geograficas = geograficas();
        Map<Integer, String> tiposNombre = tiposNombre();
        Map<Integer, String> lectivos = lectivos();
        Map<String, PersonaKey> personas = personas(pagina.items());
        Map<String, String> usuarios = usuarios(pagina.items());

        List<BorradorListadoItemResponse> items = pagina.items().stream()
                .map(borrador -> {
                    PersonaKey persona = personas.get(clavePersona(borrador.getPersonaId(),
                            borrador.getDocumentoId()));
                    return BorradorListadoItemResponse.builder()
                            .borradorId(borrador.getBorradorId())
                            .estado(borrador.getEstado() == null ? null : borrador.getEstado().name())
                            .personaId(borrador.getPersonaId())
                            .documentoId(borrador.getDocumentoId())
                            .personaApellido(persona == null ? null : persona.getApellido())
                            .personaNombre(persona == null ? null : persona.getNombre())
                            .facultadId(borrador.getFacultadId())
                            .facultadNombre(facultades.get(borrador.getFacultadId()))
                            .geograficaId(borrador.getGeograficaId())
                            .geograficaNombre(geograficas.get(borrador.getGeograficaId()))
                            .tipoChequeraId(borrador.getTipoChequeraId())
                            .tipoChequeraNombre(tiposNombre.get(borrador.getTipoChequeraId()))
                            .lectivoId(borrador.getLectivoId())
                            .lectivoNombre(lectivos.get(borrador.getLectivoId()))
                            .alternativaId(borrador.getAlternativaId())
                            .arancelTipoId(borrador.getArancelTipoId())
                            .chequeraSerieId(borrador.getChequeraSerieId())
                            .totalBorrador(borrador.totalBorrador())
                            .usuarioId(borrador.getUsuarioId())
                            .usuarioNombre(usuarios.get(borrador.getUsuarioId()))
                            .createdAt(borrador.getCreatedAt())
                            .updatedAt(borrador.getUpdatedAt())
                            .build();
                })
                .toList();
        return BorradorListadoResponse.builder().items(items).total(pagina.total()).page(page).size(size).build();
    }

    @Transactional(readOnly = true)
    public long contar(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        if (facultadId == null || lectivoId == null || geograficaId == null) {
            return 0;
        }
        return borradorRepository.countByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(lectivoId, facultadId,
                tiposDeSede(geograficaId), EstadoBorrador.ABIERTO);
    }

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

    private List<Integer> tiposDeSede(Integer geograficaId) {
        return sedePorTipo().entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), geograficaId))
                .map(Map.Entry::getKey)
                .toList();
    }

    private Map<Integer, String> tiposNombre() {
        Map<Integer, String> tipos = new LinkedHashMap<>();
        for (TipoChequera tipo : tipoChequeraService.findAll()) {
            if (tipo.getTipoChequeraId() != null) {
                tipos.putIfAbsent(tipo.getTipoChequeraId(), tipo.getNombre());
            }
        }
        return tipos;
    }

    private Map<Integer, String> lectivos() {
        Map<Integer, String> lectivos = new LinkedHashMap<>();
        for (Lectivo lectivo : lectivoService.findAll()) {
            if (lectivo.getLectivoId() != null) {
                lectivos.putIfAbsent(lectivo.getLectivoId(), lectivo.getNombre());
            }
        }
        return lectivos;
    }

    private Map<String, PersonaKey> personas(List<BorradorChequera> borradores) {
        List<String> claves = borradores.stream()
                .map(borrador -> clavePersona(borrador.getPersonaId(), borrador.getDocumentoId()))
                .distinct()
                .toList();
        if (claves.isEmpty()) {
            return Map.of();
        }
        return personaKeyService.findAllByUnifiedIn(claves, List.of("apellido", "nombre")).stream()
                .collect(Collectors.toMap(PersonaKey::getUnified, Function.identity(), (primera, segunda) -> primera));
    }

    private Map<String, String> usuarios(List<BorradorChequera> borradores) {
        Map<String, String> usuarios = new LinkedHashMap<>();
        borradores.stream()
                .map(BorradorChequera::getUsuarioId)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(usuarioId -> !usuarioId.isEmpty())
                .distinct()
                .forEach(usuarioId -> usuarios.put(usuarioId, nombreUsuario(usuarioId)));
        return usuarios;
    }

    private String nombreUsuario(String usuarioId) {
        try {
            Long userId = Long.valueOf(usuarioId);
            return usuarioService.findByUserId(userId).map(this::nombreUsuario).orElse(usuarioId);
        } catch (NumberFormatException e) {
            try {
                return nombreUsuario(usuarioService.findByLogin(usuarioId));
            } catch (RuntimeException ex) {
                return usuarioId;
            }
        } catch (RuntimeException e) {
            return usuarioId;
        }
    }

    private String nombreUsuario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        String apellido = usuario.getApellido() == null ? "" : usuario.getApellido().trim();
        String nombre = usuario.getNombre() == null ? "" : usuario.getNombre().trim();
        String completo = (apellido + ", " + nombre).replaceAll("^,\\s*", "").trim();
        return completo.isEmpty() ? usuario.getLogin() : completo;
    }

    private static String clavePersona(BigDecimal personaId, Integer documentoId) {
        return (personaId == null ? "" : personaId.toPlainString()) + "." + (documentoId == null ? "" : documentoId);
    }

    private record SedeKey(Integer facultadId, Integer geograficaId) {
    }
}
