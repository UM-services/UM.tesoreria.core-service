package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DatosBaja;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ActualizarBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AlternativaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ArancelTipoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AvisoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ContextoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.DatosConstruccion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.DeudaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.DomicilioEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PermisosEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PersonaEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SerieEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;
import um.tesoreria.core.model.dto.PlantillaArancelDto;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.view.PlantillaArancelService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Contexto y borrador de la emisión individual de chequeras.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BorradorEmisionService {

    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final SetupService setupService;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraPagoService chequeraPagoService;
    private final BajaService bajaService;
    private final LectivoCuotaService lectivoCuotaService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final TipoChequeraService tipoChequeraService;
    private final FacultadService facultadService;
    private final LectivoService lectivoService;
    private final GeograficaService geograficaService;
    private final ArancelTipoService arancelTipoService;
    private final PlantillaArancelService plantillaArancelService;
    private final GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;
    private final BorradorFactory borradorFactory;
    private final BorradorChequeraRepository borradorRepository;

    // ------------------------------------------------------------------
    // Contexto (paso 1 del wizard)
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public ContextoEmision contexto(BigDecimal personaId, Integer documentoId, Integer lectivoId, Long usuarioId) {
        Persona persona = persona(personaId, documentoId);
        Domicilio domicilio = domicilio(personaId, documentoId);
        DeudaPersonaDto deuda = deuda(personaId, documentoId);
        Setup setup = setup();
        List<ChequeraSerie> series = chequeraSerieService.findAllByPersonaLectivo(personaId, documentoId, lectivoId);

        ContextoEmision contexto = ContextoEmision.builder()
                .lectivoId(lectivoId)
                .persona(PersonaEmision.builder()
                        .personaId(personaId)
                        .documentoId(documentoId)
                        .apellido(persona == null ? null : persona.getApellido())
                        .nombre(persona == null ? null : persona.getNombre())
                        .build())
                .domicilio(domicilioEmision(domicilio))
                .deuda(deudaEmision(deuda, setup))
                .permisos(permisos(usuarioId))
                .series(series.stream().map(this::serieEmision).toList())
                .build();

        contexto.getAvisos().addAll(avisosContexto(contexto));
        return contexto;
    }

    // ------------------------------------------------------------------
    // Borrador (pasos 2 y 3)
    // ------------------------------------------------------------------

    @Transactional
    public BorradorChequera crearBorrador(CrearBorradorData data, String usuarioId) {
        if (data.getPersonaId() == null || data.getDocumentoId() == null || data.getFacultadId() == null
                || data.getLectivoId() == null || data.getTipoChequeraId() == null) {
            throw new EmisionException(ReglasEmision.SIN_DATOS,
                    "Faltan datos obligatorios: persona, documento, facultad, lectivo y tipo de chequera");
        }
        Persona persona = persona(data.getPersonaId(), data.getDocumentoId());
        TipoChequera tipo = tipoChequera(data.getTipoChequeraId());
        List<ChequeraSerie> series = chequeraSerieService.findAllByPersonaLectivo(
                data.getPersonaId(), data.getDocumentoId(), data.getLectivoId());
        ChequeraSerie serie = resolverSerie(series, data);
        if (serie != null && data.getChequeraSerieId() != null && esDeBaja(serie)) {
            throw new EmisionException(ReglasEmision.CHEQUERA_DE_BAJA,
                    "La chequera " + serie.getChequeraSerieId()
                            + " está dada de baja: no se puede editar ni emitir. Cree una chequera nueva.");
        }

        // Un solo borrador por chequera: si ya hay uno abierto para esta serie (o, cuando la
        // chequera todavía no existe, para la misma persona/lectivo/facultad/tipo), se retoma en
        // lugar de crear un duplicado. No se recalculan sus cuotas para no perder ediciones.
        Optional<BorradorChequera> abierto = serie != null
                ? borradorRepository.findAbiertoBySerie(serie.getFacultadId(), serie.getTipoChequeraId(),
                        serie.getChequeraSerieId())
                : borradorRepository.findAbiertoByNueva(data.getPersonaId(), data.getDocumentoId(),
                        data.getLectivoId(), data.getFacultadId(), data.getTipoChequeraId());
        if (abierto.isPresent()) {
            log.debug("Borrador abierto reutilizado para {}/{}/{}: {}", data.getFacultadId(),
                    data.getTipoChequeraId(), serie == null ? "nueva" : serie.getChequeraSerieId(),
                    abierto.get().getBorradorId());
            return enriquecer(abierto.get());
        }

        Domicilio domicilio = domicilio(data.getPersonaId(), data.getDocumentoId());

        Integer arancelTipoId = data.getArancelTipoId() == null ? 0 : data.getArancelTipoId();
        List<AlternativaEmision> alternativas = alternativas(data.getFacultadId(), data.getLectivoId(),
                data.getTipoChequeraId());
        Integer alternativaId = Optional.ofNullable(data.getAlternativaId())
                .orElseGet(() -> serie != null && serie.getAlternativaId() != null
                        ? serie.getAlternativaId()
                        : alternativas.stream().map(AlternativaEmision::getAlternativaId).findFirst().orElse(null));

        List<LectivoCuota> diseno = lectivoCuotaService.findAllByFacultadLectivoTipo(
                data.getFacultadId(), data.getLectivoId(), data.getTipoChequeraId());
        List<ChequeraCuota> persistidas = serie == null ? List.of()
                : chequeraCuotaService.findAllByChequera(data.getFacultadId(), data.getTipoChequeraId(),
                        serie.getChequeraSerieId());

        // Beca y hpum son atributos de la serie: si el request no los trae se preservan. Una
        // reparación de una chequera existente no debe borrarlos (ni recalcular sin beca).
        BigDecimal becaPorcentaje = data.getBecaPorcentaje() != null ? data.getBecaPorcentaje()
                : serie != null && serie.getBecaPorcentaje() != null ? serie.getBecaPorcentaje()
                : BigDecimal.ZERO;
        String becaResolucion = primeroNoVacio(data.getBecaResolucion(),
                serie == null ? null : serie.getBecaResolucion());
        OffsetDateTime becaFecha = data.getBecaFecha() != null ? data.getBecaFecha()
                : serie == null ? null : serie.getBecaFecha();
        Long becaUserId = data.getBecaUserId() != null ? data.getBecaUserId()
                : serie == null ? null : serie.getBecaUserId();
        Byte hpum = data.getHpum() != null ? data.getHpum()
                : serie != null && serie.getHpum() != null ? serie.getHpum() : hpum(persona);

        DatosConstruccion datos = DatosConstruccion.builder()
                .facultadId(data.getFacultadId())
                .lectivoId(data.getLectivoId())
                .tipoChequeraId(data.getTipoChequeraId())
                .arancelTipoIdSeleccionado(arancelTipoId)
                .arancelTipoIdChequera(serie == null ? null : serie.getArancelTipoId())
                .becaPorcentaje(nz(becaPorcentaje))
                .calculaBeca(Boolean.TRUE.equals(data.getCalculaBeca()))
                .personaId(data.getPersonaId())
                .documentoId(data.getDocumentoId())
                .enTerminoAnterior(enTerminoAnterior(data.getPersonaId(), data.getDocumentoId(),
                        data.getLectivoId() - 1))
                .diseno(diseno)
                .alternativas(alternativas)
                .build();

        List<BorradorCuota> cuotas = borradorFactory.construir(persistidas, datos);
        if (data.getFuente() == FuenteCompletado.PLANTILLA) {
            agregarDesdePlantilla(cuotas, datos, arancelTipoId);
        }

        BorradorChequera borrador = BorradorChequera.builder()
                .estado(EstadoBorrador.ABIERTO)
                .personaId(data.getPersonaId())
                .documentoId(data.getDocumentoId())
                .facultadId(data.getFacultadId())
                .lectivoId(data.getLectivoId())
                .tipoChequeraId(data.getTipoChequeraId())
                .arancelTipoId(arancelTipoId)
                .alternativaId(alternativaId)
                .cursoId(data.getCursoId() != null ? data.getCursoId() : serie == null ? null : serie.getCursoId())
                .chequeraSerieId(serie == null ? null : serie.getChequeraSerieId())
                .fecha(data.getFecha() == null ? OffsetDateTime.now() : data.getFecha())
                .hpum(hpum)
                .becaPorcentaje(nz(becaPorcentaje))
                .becaResolucion(becaResolucion)
                .becaFecha(becaFecha)
                .becaUserId(becaUserId)
                .calculaBeca(Boolean.TRUE.equals(data.getCalculaBeca()))
                .emailPersonal(primeroNoVacio(data.getEmailPersonal(), domicilio == null ? null : domicilio.getEmailPersonal()))
                .emailInstitucional(primeroNoVacio(data.getEmailInstitucional(),
                        domicilio == null ? null : domicilio.getEmailInstitucional()))
                .emailCopia(tipo.getEmailCopia())
                .incluyeBarras(Boolean.TRUE.equals(data.getIncluyeBarras()))
                .copiaFacultad(Boolean.TRUE.equals(data.getCopiaFacultad()))
                .usuarioId(usuarioId)
                .cuotas(cuotas)
                .build();

        borrador.getAvisos().addAll(avisosBorrador(borrador, datos));
        BorradorChequera guardado = borradorRepository.save(borrador);
        return enriquecer(guardado);
    }

    @Transactional(readOnly = true)
    public BorradorChequera obtenerBorrador(Long borradorId) {
        return enriquecer(cargar(borradorId));
    }

    @Transactional(readOnly = true)
    public List<BorradorChequera> borradoresPendientes(String usuarioId) {
        return borradorRepository.findAllByUsuarioIdAndEstado(usuarioId, EstadoBorrador.ABIERTO)
                .stream().map(this::enriquecer).toList();
    }

    @Transactional
    public BorradorChequera actualizarBorrador(Long borradorId, ActualizarBorradorData data) {
        BorradorChequera borrador = cargarAbierto(borradorId);
        validarVersion(borrador, data.getVersion());

        boolean recalcular = data.getArancelTipoId() != null
                && !Objects.equals(data.getArancelTipoId(), borrador.getArancelTipoId())
                || data.getBecaPorcentaje() != null
                && nz(data.getBecaPorcentaje()).compareTo(nz(borrador.getBecaPorcentaje())) != 0
                || data.getCalculaBeca() != null && !Objects.equals(data.getCalculaBeca(), borrador.isCalculaBeca())
                || data.getAlternativaId() != null && !Objects.equals(data.getAlternativaId(), borrador.getAlternativaId());

        aplicarCabecera(borrador, data);

        List<BorradorCuota> cuotas = new ArrayList<>(borrador.getCuotas());
        if (recalcular) {
            cuotas = borradorFactory.construirDesdeBorrador(cuotas, datosConstruccion(borrador));
        }
        borradorFactory.aplicarEdiciones(cuotas, data.getCuotas());
        borrador.setCuotas(cuotas);

        return enriquecer(borradorRepository.save(borrador));
    }

    @Transactional
    public void descartarBorrador(Long borradorId) {
        BorradorChequera borrador = cargar(borradorId);
        if (borrador.getEstado() == EstadoBorrador.EMITIDO) {
            throw new EmisionException(ReglasEmision.BORRADOR_NO_ABIERTO,
                    "El borrador ya fue emitido y no puede descartarse");
        }
        borrador.setEstado(EstadoBorrador.DESCARTADO);
        borradorRepository.save(borrador);
    }

    @Transactional
    public BorradorChequera completarBorrador(Long borradorId, FuenteCompletado fuente) {
        BorradorChequera borrador = cargarAbierto(borradorId);
        FuenteCompletado origen = fuente == null ? FuenteCompletado.DISENO : fuente;
        DatosConstruccion datos = datosConstruccion(borrador);

        List<BorradorCuota> base = switch (origen) {
            case DISENO -> borrador.getCuotas().stream().filter(BorradorCuota::bloqueada).toList();
            default -> new ArrayList<>(borrador.getCuotas());
        };
        List<BorradorCuota> cuotas = borradorFactory.construirDesdeBorrador(new ArrayList<>(base), datos);
        if (origen == FuenteCompletado.PLANTILLA) {
            agregarDesdePlantilla(cuotas, datos, borrador.getArancelTipoId());
        }
        borrador.setCuotas(cuotas);
        return enriquecer(borradorRepository.save(borrador));
    }

    /**
     * "Aplicar P.A.": replica {@code politicaArancelaria/recalculate/.../plazo/10} pero
     * sin persistir la cuota real (sólo toca el borrador).
     */
    @Transactional
    public BorradorChequera aplicarPoliticaArancelaria(Long borradorId, Integer productoId, Integer alternativaId,
                                                        Integer cuotaId) {
        BorradorChequera borrador = cargarAbierto(borradorId);
        BorradorCuota cuota = borrador.cuota(productoId, alternativaId, cuotaId);
        if (cuota == null) {
            throw new EmisionException(ReglasEmision.SIN_CUOTAS,
                    "No existe la cuota " + productoId + "." + alternativaId + "." + cuotaId + " en el borrador");
        }
        BigDecimal importeReferencia = nz(cuota.getImporte1Borrador());
        BigDecimal importeActual = nz(cuota.getImporte3Borrador());
        cuota.setImporte3Borrador(ReglasEmision.redondear2(importeActual.compareTo(importeReferencia) > 0
                ? importeActual : importeReferencia));
        cuota.setVencimiento3(OffsetDateTime.now().toLocalDate().atStartOfDay().atOffset(OffsetDateTime.now().getOffset())
                .plusDays(ReglasEmision.PLAZO_POLITICA_DIAS));
        cuota.setManual((byte) 1);
        cuota.setModificado((byte) 1);
        cuota.setBloqueado((byte) 1);
        return enriquecer(borradorRepository.save(borrador));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public BorradorChequera cargar(Long borradorId) {
        return borradorRepository.findById(borradorId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.SIN_DATOS,
                        "Borrador " + borradorId + " inexistente"));
    }

    public BorradorChequera cargarAbierto(Long borradorId) {
        BorradorChequera borrador = cargar(borradorId);
        if (borrador.getEstado() != EstadoBorrador.ABIERTO) {
            throw new EmisionException(ReglasEmision.BORRADOR_NO_ABIERTO,
                    "El borrador está " + borrador.getEstado() + " y no admite cambios");
        }
        return borrador;
    }

    public DatosConstruccion datosConstruccion(BorradorChequera borrador) {
        List<AlternativaEmision> alternativas = alternativas(borrador.getFacultadId(), borrador.getLectivoId(),
                borrador.getTipoChequeraId());
        Integer arancelTipoIdChequera = borrador.getChequeraSerieId() == null ? null : serie(borrador)
                .map(ChequeraSerie::getArancelTipoId).orElse(null);
        return DatosConstruccion.builder()
                .facultadId(borrador.getFacultadId())
                .lectivoId(borrador.getLectivoId())
                .tipoChequeraId(borrador.getTipoChequeraId())
                .arancelTipoIdSeleccionado(borrador.getArancelTipoId())
                .arancelTipoIdChequera(arancelTipoIdChequera)
                .becaPorcentaje(nz(borrador.getBecaPorcentaje()))
                .calculaBeca(borrador.isCalculaBeca())
                .personaId(borrador.getPersonaId())
                .documentoId(borrador.getDocumentoId())
                .enTerminoAnterior(enTerminoAnterior(borrador.getPersonaId(), borrador.getDocumentoId(),
                        borrador.getLectivoId() - 1))
                .diseno(lectivoCuotaService.findAllByFacultadLectivoTipo(borrador.getFacultadId(),
                        borrador.getLectivoId(), borrador.getTipoChequeraId()))
                .alternativas(alternativas)
                .build();
    }

    public BorradorChequera enriquecer(BorradorChequera borrador) {
        if (borrador == null) {
            return null;
        }
        borrador.setFacultadNombre(facultadNombre(borrador.getFacultadId()));
        borrador.setTipoChequeraNombre(tipoChequeraNombre(borrador.getTipoChequeraId()));
        borrador.setLectivoNombre(lectivoNombre(borrador.getLectivoId()));
        personaEmision(borrador);
        sedeEmision(borrador);
        borrador.setAlternativas(alternativas(borrador.getFacultadId(), borrador.getLectivoId(),
                borrador.getTipoChequeraId()));
        borrador.setArancelTipos(arancelTipos());
        borrador.setAvisos(avisosBorrador(borrador, datosConstruccionSeguro(borrador)));
        return borrador;
    }

    private DatosConstruccion datosConstruccionSeguro(BorradorChequera borrador) {
        try {
            return datosConstruccion(borrador);
        } catch (RuntimeException e) {
            log.warn("No se pudo reconstruir el contexto del borrador {}: {}", borrador.getBorradorId(), e.getMessage());
            return DatosConstruccion.builder()
                    .becaPorcentaje(nz(borrador.getBecaPorcentaje()))
                    .calculaBeca(borrador.isCalculaBeca())
                    .build();
        }
    }

    private List<AvisoEmision> avisosBorrador(BorradorChequera borrador, DatosConstruccion datos) {
        List<AvisoEmision> avisos = new ArrayList<>();
        if (borrador.getCuotas() == null || borrador.getCuotas().isEmpty()) {
            avisos.add(AvisoEmision.bloqueante(ReglasEmision.SIN_CUOTAS,
                    "No hay cuotas en el borrador. Use \"Completar faltantes\" o cargue las cuotas manualmente"));
        }
        if (borrador.getAlternativaId() == null) {
            avisos.add(AvisoEmision.bloqueante(ReglasEmision.SIN_ALTERNATIVA,
                    "Debe seleccionar una alternativa de pago"));
        }
        if (borrador.getEmailPersonal() == null || borrador.getEmailPersonal().isBlank()) {
            avisos.add(AvisoEmision.bloqueante(ReglasEmision.SIN_EMAIL_PERSONAL,
                    "El mail personal es obligatorio para emitir"));
        } else if (!ReglasEmision.emailValido(borrador.getEmailPersonal())) {
            avisos.add(AvisoEmision.bloqueante(ReglasEmision.EMAIL_INVALIDO,
                    "El mail personal no tiene un formato válido"));
        }
        if (datos.isEnTerminoAnterior()) {
            avisos.add(AvisoEmision.informativo("DESCUENTO_PAGO_TERMINO",
                    "La persona pagó en término el lectivo anterior: se aplicó el descuento por pago en término"));
        }
        return avisos;
    }

    private List<AvisoEmision> avisosContexto(ContextoEmision contexto) {
        List<AvisoEmision> avisos = new ArrayList<>();
        if (contexto.getDeuda() != null && contexto.getDeuda().isExcedida()) {
            avisos.add(AvisoEmision.informativo(ReglasEmision.DEUDA_EXCEDIDA,
                    "La persona tiene " + contexto.getDeuda().getCuotas() + " cuotas impagas y el tope es "
                            + contexto.getDeuda().getCuotasPermitidas()
                            + ". La emisión requerirá autorización adicional"));
        }
        if (contexto.getDomicilio() == null || contexto.getDomicilio().getEmailPersonal() == null
                || contexto.getDomicilio().getEmailPersonal().isBlank()) {
            avisos.add(AvisoEmision.bloqueante(ReglasEmision.SIN_EMAIL_PERSONAL,
                    "El domicilio no tiene mail personal; complete el mail en el paso 2"));
        }
        contexto.getSeries().stream().filter(SerieEmision::isPagada).forEach(serie ->
                avisos.add(AvisoEmision.informativo(ReglasEmision.CHEQUERA_PAGADA,
                        "La chequera " + serie.getChequeraSerieId() + " ya está totalmente paga")));
        return avisos;
    }

    private void agregarDesdePlantilla(List<BorradorCuota> cuotas, DatosConstruccion datos, Integer arancelTipoId) {
        List<PlantillaArancelDto> plantilla = plantillaArancelService.findAllByPlantilla(datos.getFacultadId(),
                datos.getLectivoId(), datos.getTipoChequeraId(), arancelTipoId);
        CuotasIndex index = new CuotasIndex(cuotas);
        for (PlantillaArancelDto fila : plantilla) {
            if (index.contiene(fila.getProductoId(), fila.getAlternativaId(), fila.getCuotaId())) {
                continue;
            }
            Integer tramoId = datos.getDiseno().stream()
                    .filter(cuota -> Objects.equals(cuota.getProductoId(), fila.getProductoId())
                            && Objects.equals(cuota.getAlternativaId(), fila.getAlternativaId())
                            && Objects.equals(cuota.getCuotaId(), fila.getCuotaId()))
                    .map(LectivoCuota::getTramoId)
                    .findFirst().orElse(null);
            cuotas.add(BorradorCuota.builder()
                    .productoId(fila.getProductoId())
                    .alternativaId(fila.getAlternativaId())
                    .cuotaId(fila.getCuotaId())
                    .mes(fila.getMes())
                    .anio(fila.getAnho())
                    .vencimiento1(fila.getVencimiento1())
                    .vencimiento2(fila.getVencimiento2())
                    .vencimiento3(fila.getVencimiento3())
                    .importe1Original(ReglasEmision.redondear2(fila.getImporte1original()))
                    .importe2Original(ReglasEmision.redondear2(fila.getImporte2original()))
                    .importe3Original(ReglasEmision.redondear2(fila.getImporte3original()))
                    .importe1Borrador(ReglasEmision.redondear2(fila.getImporte1()))
                    .importe2Borrador(ReglasEmision.redondear2(fila.getImporte2()))
                    .importe3Borrador(ReglasEmision.redondear2(fila.getImporte3()))
                    .arancelTipoId(arancelTipoId)
                    .tramoId(tramoId)
                    .descuentoPagoTermino(BigDecimal.ZERO)
                    .pagado((byte) 0)
                    .baja((byte) 0)
                    .manual((byte) 0)
                    .compensada((byte) 0)
                    .modificado((byte) 1)
                    .bloqueado((byte) 0)
                    .build());
        }
    }

    private void aplicarCabecera(BorradorChequera borrador, ActualizarBorradorData data) {
        if (data.getArancelTipoId() != null) {
            borrador.setArancelTipoId(data.getArancelTipoId());
        }
        if (data.getAlternativaId() != null) {
            borrador.setAlternativaId(data.getAlternativaId());
        }
        if (data.getCursoId() != null) {
            borrador.setCursoId(data.getCursoId());
        }
        if (data.getFecha() != null) {
            borrador.setFecha(data.getFecha());
        }
        if (data.getHpum() != null) {
            borrador.setHpum(data.getHpum());
        }
        if (data.getBecaPorcentaje() != null) {
            borrador.setBecaPorcentaje(data.getBecaPorcentaje());
        }
        if (data.getBecaResolucion() != null) {
            borrador.setBecaResolucion(data.getBecaResolucion());
        }
        if (data.getBecaFecha() != null) {
            borrador.setBecaFecha(data.getBecaFecha());
        }
        if (data.getBecaUserId() != null) {
            borrador.setBecaUserId(data.getBecaUserId());
        }
        if (data.getCalculaBeca() != null) {
            borrador.setCalculaBeca(data.getCalculaBeca());
        }
        if (data.getEmailPersonal() != null) {
            borrador.setEmailPersonal(data.getEmailPersonal());
        }
        if (data.getEmailInstitucional() != null) {
            borrador.setEmailInstitucional(data.getEmailInstitucional());
        }
        if (data.getIncluyeBarras() != null) {
            borrador.setIncluyeBarras(data.getIncluyeBarras());
        }
        if (data.getCopiaFacultad() != null) {
            borrador.setCopiaFacultad(data.getCopiaFacultad());
        }
    }

    private void validarVersion(BorradorChequera borrador, Integer version) {
        if (version != null && borrador.getVersion() != null && !version.equals(borrador.getVersion())) {
            throw new EmisionException(ReglasEmision.BORRADOR_DESACTUALIZADO,
                    "El borrador fue modificado por otra sesión (versión enviada " + version
                            + ", actual " + borrador.getVersion() + ")");
        }
    }

    public boolean enTerminoAnterior(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        if (lectivoId == null || lectivoId < 1) {
            return false;
        }
        List<ChequeraSerie> series = chequeraSerieService.findAllByPersonaLectivo(personaId, documentoId, lectivoId);
        if (series.isEmpty()) {
            return false;
        }
        ChequeraSerie serie = series.get(0);
        List<ChequeraCuota> cuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        for (ChequeraCuota cuota : cuotas) {
            if (!BorradorCuota.esVerdadero(cuota.getPagado()) && !BorradorCuota.esVerdadero(cuota.getBaja())) {
                return false;
            }
            List<ChequeraPago> pagos = chequeraPagoService.findAllByCuota(cuota.getFacultadId(),
                    cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
                    cuota.getAlternativaId(), cuota.getCuotaId());
            Optional<ChequeraPago> pagoOrdenCero = pagos.stream()
                    .filter(pago -> pago.getOrden() != null && pago.getOrden() == 0).findFirst();
            if (pagoOrdenCero.isEmpty() || pagoOrdenCero.get().getFecha() == null
                    || pagoOrdenCero.get().getFecha().isAfter(cuota.getVencimiento1())) {
                return false;
            }
        }
        return true;
    }

    private ChequeraSerie resolverSerie(List<ChequeraSerie> series, CrearBorradorData data) {
        if (data.getChequeraSerieId() != null && data.getChequeraSerieId() > 0) {
            return series.stream()
                    .filter(serie -> Objects.equals(serie.getFacultadId(), data.getFacultadId())
                            && Objects.equals(serie.getTipoChequeraId(), data.getTipoChequeraId())
                            && Objects.equals(serie.getChequeraSerieId(), data.getChequeraSerieId()))
                    .findFirst()
                    .orElseThrow(() -> new EmisionException(ReglasEmision.SIN_DATOS,
                            "La chequera " + data.getChequeraSerieId() + " no pertenece a la persona en el lectivo"));
        }
        return series.stream()
                .filter(serie -> Objects.equals(serie.getFacultadId(), data.getFacultadId())
                        && Objects.equals(serie.getTipoChequeraId(), data.getTipoChequeraId()))
                .findFirst().orElse(null);
    }

    private Optional<ChequeraSerie> serie(BorradorChequera borrador) {
        if (borrador.getChequeraSerieId() == null) {
            return Optional.empty();
        }
        return chequeraSerieService.findAllByPersonaLectivo(borrador.getPersonaId(), borrador.getDocumentoId(),
                        borrador.getLectivoId()).stream()
                .filter(serie -> Objects.equals(serie.getFacultadId(), borrador.getFacultadId())
                        && Objects.equals(serie.getTipoChequeraId(), borrador.getTipoChequeraId())
                        && Objects.equals(serie.getChequeraSerieId(), borrador.getChequeraSerieId()))
                .findFirst();
    }

    private SerieEmision serieEmision(ChequeraSerie serie) {
        TipoChequera tipo = tipoChequeraSeguro(serie.getTipoChequeraId());
        Optional<DatosBaja> baja = bajaService.findDatosByUnique(serie.getFacultadId(),
                serie.getTipoChequeraId(), serie.getChequeraSerieId());
        return SerieEmision.builder()
                .chequeraId(serie.getChequeraId())
                .facultadId(serie.getFacultadId())
                .facultadNombre(serie.getFacultad() != null ? serie.getFacultad().getNombre() : null)
                .tipoChequeraId(serie.getTipoChequeraId())
                .tipoChequeraNombre(tipo != null ? tipo.getNombre() : null)
                .chequeraSerieId(serie.getChequeraSerieId())
                .claseChequeraId(tipo != null ? tipo.getClaseChequeraId() : null)
                .geograficaId(serie.getGeograficaId() != null ? serie.getGeograficaId()
                        : tipo != null ? tipo.getGeograficaId() : null)
                .geograficaNombre(serie.getGeografica() != null ? serie.getGeografica().getNombre() : null)
                .arancelTipoId(serie.getArancelTipoId())
                .arancelTipoDescripcion(serie.getArancelTipo() != null
                        ? serie.getArancelTipo().getDescripcion() : null)
                .alternativaId(serie.getAlternativaId())
                .cursoId(serie.getCursoId())
                .fecha(serie.getFecha())
                .ultimoEnvio(serie.getUltimoEnvio())
                .enviado(serie.getEnviado())
                .retenida(serie.getRetenida())
                .pagada(esPagada(serie))
                .deBaja(baja.isPresent())
                .bajaFecha(baja.map(DatosBaja::fecha).orElse(null))
                .bajaObservaciones(baja.map(DatosBaja::observaciones).orElse(null))
                .build();
    }

    private boolean esDeBaja(ChequeraSerie serie) {
        return bajaService.findDatosByUnique(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId()).isPresent();
    }

    private boolean esPagada(ChequeraSerie serie) {
        List<ChequeraCuota> cuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        return !cuotas.isEmpty() && cuotas.stream().allMatch(cuota -> BorradorCuota.esVerdadero(cuota.getPagado()));
    }

    public List<AlternativaEmision> alternativas(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return lectivoAlternativaService.findAllByFacultadLectivoTipo(facultadId, lectivoId, tipoChequeraId)
                .stream()
                .map(alternativa -> AlternativaEmision.builder()
                        .alternativaId(alternativa.getAlternativaId())
                        .productoId(alternativa.getProductoId())
                        .titulo(alternativa.getTitulo())
                        .cuotas(alternativa.getCuotas())
                        .descuentoPagoTermino(alternativa.getDescuentoPagoTermino())
                        .build())
                .toList();
    }

    public List<ArancelTipoEmision> arancelTipos() {
        return arancelTipoService.findAll().stream()
                .map(tipo -> ArancelTipoEmision.builder()
                        .arancelTipoId(tipo.getArancelTipoId())
                        .nombre(tipo.getDescripcion())
                        .build())
                .toList();
    }

    private PermisosEmision permisos(Long usuarioId) {
        Set<String> claves = claves(usuarioId);
        return PermisosEmision.builder()
                .puedeEmitir(usuarioId == null || claves.contains("chequeras.emision.individual"))
                .puedeImprimir(usuarioId == null || claves.contains("chequeras.imprimir"))
                .puedeModificar(usuarioId == null || claves.contains("chequeras.modificar"))
                .puedeForzarDeuda(usuarioId == null || claves.contains("chequeras.emision.forzar"))
                .build();
    }

    public Set<String> claves(Long usuarioId) {
        if (usuarioId == null) {
            return Set.of();
        }
        try {
            var efectivo = getPermisosEfectivosUseCase.getPermisosEfectivos(usuarioId);
            return efectivo.getPermisos() == null ? Set.of() : new java.util.LinkedHashSet<>(efectivo.getPermisos());
        } catch (RuntimeException e) {
            log.warn("No se pudieron obtener permisos del usuario {}: {}", usuarioId, e.getMessage());
            return Set.of();
        }
    }

    private DeudaEmision deudaEmision(DeudaPersonaDto deuda, Setup setup) {
        int cuotas = deuda == null || deuda.getCuotas() == null ? 0 : deuda.getCuotas();
        Integer permitidas = setup == null ? null : setup.getCuotasPermitidas();
        return DeudaEmision.builder()
                .cuotas(cuotas)
                .importe(deuda == null ? BigDecimal.ZERO : nz(deuda.getDeuda()))
                .cuotasPermitidas(permitidas)
                .excedida(permitidas != null && cuotas > permitidas)
                .build();
    }

    private DomicilioEmision domicilioEmision(Domicilio domicilio) {
        if (domicilio == null) {
            return null;
        }
        return DomicilioEmision.builder()
                .domicilioId(domicilio.getDomicilioId())
                .emailPersonal(domicilio.getEmailPersonal())
                .emailInstitucional(domicilio.getEmailInstitucional())
                .emailPagador(domicilio.getEmailPagador())
                .emailPersonalValido(ReglasEmision.emailValido(domicilio.getEmailPersonal()))
                .emailInstitucionalValido(ReglasEmision.emailValido(domicilio.getEmailInstitucional()))
                .build();
    }

    private Persona persona(BigDecimal personaId, Integer documentoId) {
        try {
            return personaService.findByUnique(personaId, documentoId);
        } catch (RuntimeException e) {
            throw new EmisionException(ReglasEmision.SIN_DATOS,
                    "No existe la persona " + personaId + "/" + documentoId);
        }
    }

    private Domicilio domicilio(BigDecimal personaId, Integer documentoId) {
        try {
            return domicilioService.findByUnique(personaId, documentoId);
        } catch (RuntimeException e) {
            log.warn("Sin domicilio para {}/{}: {}", personaId, documentoId, e.getMessage());
            return null;
        }
    }

    private DeudaPersonaDto deuda(BigDecimal personaId, Integer documentoId) {
        try {
            return personaService.deudaByPersona(personaId, documentoId);
        } catch (RuntimeException e) {
            log.warn("No se pudo calcular la deuda de {}/{}: {}", personaId, documentoId, e.getMessage());
            return null;
        }
    }

    private Setup setup() {
        try {
            return setupService.findLastOrFail();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private TipoChequera tipoChequera(Integer tipoChequeraId) {
        try {
            return tipoChequeraService.findByTipoChequeraId(tipoChequeraId);
        } catch (RuntimeException e) {
            throw new EmisionException(ReglasEmision.SIN_DATOS,
                    "No existe el tipo de chequera " + tipoChequeraId);
        }
    }

    private TipoChequera tipoChequeraSeguro(Integer tipoChequeraId) {
        try {
            return tipoChequeraService.findByTipoChequeraId(tipoChequeraId);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String facultadNombre(Integer facultadId) {
        if (facultadId == null) {
            return null;
        }
        try {
            return facultadService.findByFacultadId(facultadId).getNombre();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String tipoChequeraNombre(Integer tipoChequeraId) {
        if (tipoChequeraId == null) {
            return null;
        }
        TipoChequera tipo = tipoChequeraSeguro(tipoChequeraId);
        return tipo == null ? null : tipo.getNombre();
    }

    private static Byte hpum(Persona persona) {
        return persona == null || persona.getHpum() == null ? 0 : persona.getHpum();
    }

    private static BigDecimal nz(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static String primeroNoVacio(String primero, String segundo) {
        return primero != null && !primero.isBlank() ? primero : segundo;
    }

    private String lectivoNombre(Integer lectivoId) {
        try {
            return lectivoService.findByLectivoId(lectivoId).getNombre();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Apellido y nombre para listar los borradores: el nombre vive en {@code persona}, no en el
     * borrador. Nunca rompe el enriquecido si la persona no existe.
     */
    private void personaEmision(BorradorChequera borrador) {
        try {
            var persona = personaService.findByUnique(borrador.getPersonaId(), borrador.getDocumentoId());
            borrador.setPersonaApellido(persona == null ? null : persona.getApellido());
            borrador.setPersonaNombre(persona == null ? null : persona.getNombre());
        } catch (RuntimeException e) {
            log.debug("Borrador {}: sin datos de persona {}", borrador.getBorradorId(), borrador.getPersonaId());
        }
    }

    /**
     * Sede del borrador: la define el tipo de chequera (igual que {@code chequera_serie.geografica}).
     */
    private void sedeEmision(BorradorChequera borrador) {
        TipoChequera tipo = tipoChequeraSeguro(borrador.getTipoChequeraId());
        if (tipo == null || tipo.getGeograficaId() == null) {
            return;
        }
        borrador.setGeograficaId(tipo.getGeograficaId());
        try {
            borrador.setGeograficaNombre(geograficaService.findByGeograficaId(tipo.getGeograficaId())
                    .map(geografica -> geografica.getNombre())
                    .orElse(null));
        } catch (RuntimeException e) {
            log.debug("Borrador {}: sin nombre de sede {}", borrador.getBorradorId(), tipo.getGeograficaId());
        }
    }

    /**
     * Índice auxiliar para no recorrer la lista completa por cada fila de plantilla.
     */
    private static final class CuotasIndex {

        private final Set<String> claves;

        private CuotasIndex(List<BorradorCuota> cuotas) {
            this.claves = cuotas.stream().map(BorradorCuota::key).collect(java.util.stream.Collectors.toSet());
        }

        private boolean contiene(Integer productoId, Integer alternativaId, Integer cuotaId) {
            return claves.contains(productoId + "." + alternativaId + "." + cuotaId);
        }
    }
}
