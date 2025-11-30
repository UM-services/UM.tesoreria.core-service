/**
 *
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import um.tesoreria.core.exception.ChequeraEliminadaException;
import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.exception.DebitoException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.dto.*;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraDetailDto;
import um.tesoreria.core.model.dto.ChequeraPagoDto;
import um.tesoreria.core.model.dto.MercadoPagoContextDto;
import um.tesoreria.core.util.Tool;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.service.*;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraService {

    private final ChequeraEliminadaService chequeraEliminadaService;
    private final ChequeraPagoAsientoService chequeraPagoAsientoService;
    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final ChequeraTotalService chequeraTotalService;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraSerieControlService chequeraSerieControlService;
    private final ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;
    private final ChequeraImpresionDetalleService chequeraImpresionDetalleService;
    private final DebitoService debitoService;
    private final ModelMapper modelMapper;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final PersonaService personaService;
    private final LectivoService lectivoService;
    private final ArancelTipoService arancelTipoService;
    private final GeograficaService geograficaService;
    private final ProductoService productoService;
    private final DomicilioService domicilioService;
    private final MercadoPagoContextService mercadoPagoContextService;

    @Autowired
    public ChequeraService(ChequeraEliminadaService chequeraEliminadaService, ChequeraPagoAsientoService chequeraPagoAsientoService, ChequeraPagoService chequeraPagoService,
                           ChequeraCuotaService chequeraCuotaService, ChequeraAlternativaService chequeraAlternativaService, ChequeraTotalService chequeraTotalService,
                           ChequeraSerieService chequeraSerieService, ChequeraSerieControlService chequeraSerieControlService, ChequeraImpresionCabeceraService chequeraImpresionCabeceraService,
                           ChequeraImpresionDetalleService chequeraImpresionDetalleService, DebitoService debitoService, ModelMapper modelMapper, FacultadService facultadService,
                           TipoChequeraService tipoChequeraService, PersonaService personaService, LectivoService lectivoService, ArancelTipoService arancelTipoService, GeograficaService geograficaService,
                           ProductoService productoService, DomicilioService domicilioService, MercadoPagoContextService mercadoPagoContextService) {
        this.chequeraEliminadaService = chequeraEliminadaService;
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraPagoAsientoService = chequeraPagoAsientoService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.chequeraAlternativaService = chequeraAlternativaService;
        this.chequeraTotalService = chequeraTotalService;
        this.chequeraSerieService = chequeraSerieService;
        this.chequeraSerieControlService = chequeraSerieControlService;
        this.chequeraImpresionCabeceraService = chequeraImpresionCabeceraService;
        this.chequeraImpresionDetalleService = chequeraImpresionDetalleService;
        this.debitoService = debitoService;
        this.modelMapper = modelMapper;
        this.facultadService = facultadService;
        this.tipoChequeraService = tipoChequeraService;
        this.personaService = personaService;
        this.lectivoService = lectivoService;
        this.arancelTipoService = arancelTipoService;
        this.geograficaService = geograficaService;
        this.productoService = productoService;
        this.domicilioService = domicilioService;
        this.mercadoPagoContextService = mercadoPagoContextService;
    }

    @Transactional
    public void deleteByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, String usuarioId) {

        // Elimina el asiento asociado
        chequeraPagoAsientoService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Elimina los pagos asociados
        chequeraPagoService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Elimina las cuotas asociadas
        chequeraCuotaService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Elimina las alternativas asociadas
        chequeraAlternativaService.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Elimina los totales asociados
        chequeraTotalService.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Elimina chequera
        ChequeraSerie chequeraserie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        chequeraSerieService.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);

        // Marca chequera serie control como eliminada
        ChequeraSerieControl chequeraSerieControl = null;
        try {
            chequeraSerieControl = chequeraSerieControlService.findByUnique(facultadId, tipoChequeraId,
                    chequeraSerieId);
            chequeraSerieControl.setEliminada((byte) 1);
            chequeraSerieControlService.update(chequeraSerieControl, chequeraSerieControl.getChequeraSerieControlId());
        } catch (ChequeraSerieControlException e) {
            chequeraSerieControl = new ChequeraSerieControl(null, facultadId, tipoChequeraId, chequeraSerieId, (byte) 0,
                    null, null, null, (byte) 1);
            chequeraSerieControlService.add(chequeraSerieControl);
        }

        // Registra chequera eliminada
        try {
            chequeraEliminadaService.findByUnique(chequeraserie.getFacultadId(), chequeraserie.getTipoChequeraId(),
                    chequeraserie.getChequeraSerieId());
        } catch (ChequeraEliminadaException e) {
            chequeraEliminadaService.add(new ChequeraEliminada(null, chequeraserie.getFacultadId(),
                    chequeraserie.getTipoChequeraId(), chequeraserie.getChequeraId(), chequeraserie.getPersonaId(),
                    chequeraserie.getDocumentoId(), chequeraserie.getLectivoId(), chequeraserie.getArancelTipoId(),
                    chequeraserie.getCursoId(), chequeraserie.getAsentado(), chequeraserie.getGeograficaId(),
                    chequeraserie.getFecha(), chequeraserie.getCuotasPagadas(), chequeraserie.getObservaciones(),
                    chequeraserie.getAlternativaId(), chequeraserie.getAlgoPagado(), chequeraserie.getTipoImpresionId(),
                    usuarioId));
        }
    }

    @Transactional
    public void extendDebito(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        // Buscar la última cuota del debito
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        log.debug("Serie -> {}", serie);
        Debito lastDebito = null;
        try {
            lastDebito = debitoService.findLastByChequera(facultadId, tipoChequeraId, chequeraSerieId,
                    serie.getAlternativaId());
            // Si tiene fecha de baja no se debe extender
            if (lastDebito.getFechaBaja() != null) {
                return;
            }
        } catch (DebitoException e) {
            return;
        }
        // Si tiene débito asociado y no está de baja extender hasta la última cuota
        Map<String, Debito> debitos = debitoService
                .findAllByAlternativa(facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(),
                        lastDebito.getDebitoTipoId())
                .stream().collect(Collectors.toMap(Debito::cuotaKey, debito -> debito));
        List<Debito> newDebitos = new ArrayList<>();
        for (ChequeraCuota cuota : chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId, tipoChequeraId,
                        chequeraSerieId, serie.getAlternativaId())) {
            if (!debitos.containsKey(cuota.cuotaKey())) {
                if (cuota.getImporte1().compareTo(BigDecimal.ZERO) > 0
                        && cuota.getVencimiento1().isAfter(Tool.dateAbsoluteArgentina())) {
                    newDebitos.add(new Debito(null, facultadId, tipoChequeraId, chequeraSerieId, serie.getChequeraId(),
                            cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId(),
                            cuota.getVencimiento1(), null, lastDebito.getDebitoTipoId(), lastDebito.getCbu(),
                            lastDebito.getNumeroTarjeta(), lastDebito.getObservaciones(), null, (byte) 0, "", null,
                            null, null));
                }
            }
        }
        log.debug("Debitos -> {}", newDebitos);
        newDebitos = debitoService.saveAll(newDebitos);
    }

    @Transactional
    public void track(Long chequeraId) {
        ChequeraSerie serie = chequeraSerieService.findByChequeraId(chequeraId);
        ChequeraImpresionCabecera cabecera = new ChequeraImpresionCabecera(null, serie.getFacultadId(),
                serie.getTipoChequeraId(), serie.getChequeraSerieId(), Tool.hourAbsoluteArgentina(),
                serie.getPersonaId(), serie.getDocumentoId(), serie.getLectivoId(), serie.getGeograficaId(),
                serie.getArancelTipoId(), serie.getAlternativaId(), serie.getUsuarioId(), serie.getVersion());
        cabecera = chequeraImpresionCabeceraService.add(cabecera);
        Map<Integer, ChequeraTotal> totales = chequeraTotalService
                .findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId())
                .stream().collect(Collectors.toMap(ChequeraTotal::getProductoId, total -> total));
        Map<Integer, ChequeraAlternativa> alternativas = chequeraAlternativaService
                .findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(),
                        serie.getAlternativaId())
                .stream().collect(Collectors.toMap(ChequeraAlternativa::getProductoId, alternativa -> alternativa));
        List<ChequeraImpresionDetalle> detalles = new ArrayList<ChequeraImpresionDetalle>();
        for (ChequeraCuota cuota : chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId())) {
            ChequeraTotal total = totales.get(cuota.getProductoId());
            ChequeraAlternativa alternativa = alternativas.get(cuota.getProductoId());
            detalles.add(new ChequeraImpresionDetalle(null, cabecera.getChequeraImpresionCabeceraId(),
                    cuota.getFacultadId(), cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
                    cuota.getAlternativaId(), cuota.getCuotaId(), alternativa.getTitulo(), total.getTotal(),
                    alternativa.getCuotas(), cuota.getMes(), cuota.getAnho(), cuota.getArancelTipoId(),
                    cuota.getVencimiento1(), cuota.getImporte1(), cuota.getVencimiento2(), cuota.getImporte2(),
                    cuota.getVencimiento3(), cuota.getImporte3(), serie.getUsuarioId(), serie.getVersion()));
        }
        detalles = chequeraImpresionDetalleService.saveAll(detalles);
    }

    public PreuniversitarioData findLastPreData(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        ChequeraSerie chequeraSerie = chequeraSerieService
                .findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId);

        return new PreuniversitarioData(this.constructChequeraDataDTO(chequeraSerie));
    }

    public ChequeraSerieDto constructChequeraDataDTO(ChequeraSerie chequeraSerie) {
        log.debug("\n\nConstructing ChequeraService.constructChequeraDataDTO\n\n");
        List<ChequeraCuota> chequeraCuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdConImporte(
                        chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                        chequeraSerie.getChequeraSerieId(), chequeraSerie.getAlternativaId());
        
        List<ChequeraPago> chequeraPagos = chequeraPagoService.findAllByChequera(
                chequeraSerie.getFacultadId(), chequeraSerie.getTipoChequeraId(),
                chequeraSerie.getChequeraSerieId(), chequeraCuotaService);
        
        FacultadDto facultadDTO = modelMapper.map(facultadService.findByFacultadId(chequeraSerie.getFacultadId()), FacultadDto.class);
        TipoChequeraDto tipoChequeraDTO = modelMapper.map(tipoChequeraService.findByTipoChequeraId(chequeraSerie.getTipoChequeraId()), TipoChequeraDto.class);
        PersonaDto personaDTO = modelMapper.map(personaService.findByUnique(chequeraSerie.getPersonaId(), chequeraSerie.getDocumentoId()), PersonaDto.class);
        DomicilioDto domicilioDTO = modelMapper.map(domicilioService.findByUnique(chequeraSerie.getPersonaId(), chequeraSerie.getDocumentoId()), DomicilioDto.class);
        LectivoDto lectivoDTO = modelMapper.map(lectivoService.findByLectivoId(chequeraSerie.getLectivoId()), LectivoDto.class);
        ArancelTipoDto arancelTipoDTO = modelMapper.map(arancelTipoService.findByArancelTipoId(chequeraSerie.getArancelTipoId()), ArancelTipoDto.class);
        GeograficaDto geograficaDTO = modelMapper.map(geograficaService.findByGeograficaId(chequeraSerie.getGeograficaId()), GeograficaDto.class);
        List<ChequeraCuotaDto> chequeraCuotaDtos = chequeraCuotas.stream()
                .map(cuota -> {
                    // Filtrar los pagos de esta cuota específica usando chequeraCuotaId
                    List<ChequeraPago> pagosCuota = chequeraPagos.stream()
                            .filter(pago -> Objects.equals(pago.getChequeraCuotaId(), cuota.getChequeraCuotaId()))
                            .collect(Collectors.toList());
                    
                    // Convertir los pagos a DTOs
                    List<ChequeraPagoDto> pagosCuotaDto = modelMapper.map(pagosCuota, new TypeToken<List<ChequeraPagoDto>>() {}.getType());
                    
                    // Obtener el contexto de MercadoPago activo para esta cuota
                    MercadoPagoContextDto mercadoPagoContextDto = null;
                    try {
                        MercadoPagoContext mercadoPagoContext = mercadoPagoContextService.findActiveByChequeraCuotaId(cuota.getChequeraCuotaId());
                        mercadoPagoContextDto = modelMapper.map(mercadoPagoContext, MercadoPagoContextDto.class);
                        log.debug("MercadoPagoContextDto mapeado exitosamente para cuota {}", cuota.getChequeraCuotaId());
                    } catch (Exception e) {
                        log.debug("Error al obtener o mapear contexto de MercadoPago para la cuota {}: {}", cuota.getChequeraCuotaId(), e.getMessage());
                    }
                    
                    return new ChequeraCuotaDto(
                            cuota.getChequeraCuotaId(),
                            cuota.getCuotaId(),
                            cuota.getMes(),
                            cuota.getAnho(),
                            cuota.getVencimiento1(),
                            cuota.getImporte1(),
                            cuota.getVencimiento2(),
                            cuota.getImporte2(),
                            cuota.getVencimiento3(),
                            cuota.getImporte3(),
                            cuota.getPagado(),
                            modelMapper.map(productoService.findByProductoId(cuota.getProductoId()), ProductoDto.class),
                            pagosCuotaDto,
                            mercadoPagoContextDto);
                })
                .collect(Collectors.toList());
        log.debug("Formando ChequeraSerieDTO");
        assert chequeraSerie.getAlternativaId() != null;
        return new ChequeraSerieDto(chequeraSerie.getChequeraSerieId(),
                chequeraSerie.getFecha(),
                Objects.requireNonNull(chequeraSerie.getObservaciones()),
                chequeraSerie.getAlternativaId(),
                facultadDTO,
                tipoChequeraDTO,
                personaDTO,
                domicilioDTO,
                lectivoDTO,
                arancelTipoDTO,
                geograficaDTO,
                chequeraCuotaDtos);
    }

    public List<ChequeraCuotaPagosDto> findAllCuotaPagosByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        List<ChequeraCuota> cuotas = chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
        List<ChequeraPago> pagos = chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService);
        
        return cuotas.stream()
            .map(cuota -> {
                ChequeraCuotaPagosDto dto = modelMapper.map(cuota, ChequeraCuotaPagosDto.class);
                // Filtrar los pagos de esta cuota específica usando chequeraCuotaId
                List<ChequeraPago> pagosCuota = pagos.stream()
                    .filter(pago -> Objects.equals(pago.getChequeraCuotaId(), cuota.getChequeraCuotaId()))
                    .collect(Collectors.toList());
                dto.setChequeraPagos(modelMapper.map(pagosCuota, new TypeToken<List<ChequeraPagoDto>>() {}.getType()));
                return dto;
            })
            .collect(Collectors.toList());
    }

    public ChequeraDetailDto constructStatusFromChequera(BigDecimal personaId, Integer facultadId) {
        log.debug("\n\nProcessing ChequeraService.constructStatusFromChequera\n\n");
        var persona = personaService.findByPersonaId(personaId);
        var lectivo = lectivoService.findByFecha(OffsetDateTime.now());
        var facultad = facultadService.findByFacultadId(facultadId);
        var chequeraSeries = chequeraSerieService.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(persona.getPersonaId(), persona.getDocumentoId(), facultad.getFacultadId(), lectivo.getLectivoId());
        
        // Convertir cada ChequeraSerie a ChequeraSerieDto usando constructChequeraDataDTO para incluir las cuotas
        List<ChequeraSerieDto> chequeraSeriesDto = chequeraSeries.stream()
                .map(this::constructChequeraDataDTO)
                .collect(Collectors.toList());
        
        return ChequeraDetailDto.builder()
                .persona(modelMapper.map(persona, PersonaDto.class))
                .lectivo(modelMapper.map(lectivo, LectivoDto.class))
                .facultad(modelMapper.map(facultad, FacultadDto.class))
                .chequeraSeries(chequeraSeriesDto)
                .build();
    }

}
