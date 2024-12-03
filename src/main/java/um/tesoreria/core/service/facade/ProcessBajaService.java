package um.tesoreria.core.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import um.tesoreria.core.exception.BajaException;
import um.tesoreria.core.kotlin.model.Baja;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.service.BajaService;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.util.Periodo;

import java.util.List;
import java.util.Objects;

/**
 * Service class for processing baja (withdrawal) operations.
 * Handles the business logic for creating and undoing bajas for chequeras.
 */
@Service
@Slf4j
public class ProcessBajaService {

    private static final int CUTOFF_DAY = 15;
    private static final int MAX_MONTH = 12;
    private static final int DEFAULT_MONTH = 3;
    private static final byte BAJA_ACTIVE = 1;
    private static final byte BAJA_INACTIVE = 0;

    private final ChequeraCuotaService chequeraCuotaService;
    private final DebitoService debitoService;
    private final BajaService bajaService;
    private final ChequeraSerieService chequeraSerieService;
    private final JsonMapper jsonMapper;

    public ProcessBajaService(ChequeraCuotaService chequeraCuotaService,
                            DebitoService debitoService,
                            BajaService bajaService,
                            ChequeraSerieService chequeraSerieService) {
        this.chequeraCuotaService = chequeraCuotaService;
        this.debitoService = debitoService;
        this.bajaService = bajaService;
        this.chequeraSerieService = chequeraSerieService;
        this.jsonMapper = JsonMapper.builder().findAndAddModules().build();
    }

    /**
     * Undoes a baja operation for the specified chequera.
     *
     * @param facultadId      ID of the facultad
     * @param tipoChequeraId  ID of the tipo chequera
     * @param chequeraSerieId ID of the chequera serie
     * @return true if the operation was successful
     */
    @Transactional
    public Boolean undoBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        validateInputParameters(facultadId, tipoChequeraId, chequeraSerieId);
        log.debug("Processing undoBaja for facultadId: {}, tipoChequeraId: {}, chequeraSerieId: {}", 
                  facultadId, tipoChequeraId, chequeraSerieId);

        List<ChequeraCuota> cuotas = chequeraCuotaService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
        cuotas.forEach(this::processCuotaUndo);
        
        return true;
    }

    /**
     * Creates a new baja operation.
     *
     * @param baja the baja object containing the operation details
     * @return true if the operation was successful
     */
    @Transactional
    public Boolean makeBaja(Baja baja) {
        Assert.notNull(baja, "Baja object cannot be null");
        Assert.notNull(baja.getFecha(), "Baja fecha cannot be null");
        
        log.debug("Processing makeBaja for facultadId: {}, tipoChequeraId: {}, chequeraSerieId: {}", 
                  baja.getFacultadId(), baja.getTipoChequeraId(), baja.getChequeraSerieId());

        baja = updateBajaWithExistingId(baja);
        logObject(baja, "Baja");
        
        undoBaja(baja.getFacultadId(), baja.getTipoChequeraId(), baja.getChequeraSerieId());
        
        Periodo periodo = calculatePeriodo(baja);
        ensureChequeraSerieIsSet(baja);
        
        processPendingCuotas(baja, periodo);
        
        return true;
    }

    private void validateInputParameters(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        Assert.notNull(facultadId, "FacultadId cannot be null");
        Assert.notNull(tipoChequeraId, "TipoChequeraId cannot be null");
        Assert.notNull(chequeraSerieId, "ChequeraSerieId cannot be null");
    }

    private void processCuotaUndo(ChequeraCuota cuota) {
        cuota.setBaja(BAJA_INACTIVE);
        ChequeraCuota updatedCuota = chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
        logObject(updatedCuota, "Cuota");
        processDebitosUndo(updatedCuota);
    }

    private void processDebitosUndo(ChequeraCuota cuota) {
        List<Debito> debitos = debitoService.findAllAsociados(
                cuota.getFacultadId(), cuota.getTipoChequeraId(), cuota.getChequeraSerieId(),
                cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId());
        
        debitos.forEach(debito -> {
            debito.setFechaBaja(null);
            Debito updatedDebito = debitoService.update(debito, debito.getDebitoId());
            logObject(updatedDebito, "Debito");
        });
    }

    private Baja updateBajaWithExistingId(Baja baja) {
        log.debug("Processing updateBajaWithExistingId");
        Long bajaId = null;
        try {
            bajaId = bajaService.findByUnique(baja.getFacultadId(), baja.getTipoChequeraId(), baja.getChequeraSerieId())
                    .getBajaId();
        } catch (BajaException e) {
            log.error("Baja not found -> {}", e.getMessage());
        }
        baja.setBajaId(bajaId);
        if (bajaId == null) {
            return bajaService.add(baja);
        }
        return bajaService.update(baja, bajaId);
    }

    private Periodo calculatePeriodo(Baja baja) {
        Periodo periodo = Periodo.builder()
                .anho(baja.getFecha().getYear())
                .mes(baja.getFecha().getMonthValue())
                .build();
        
        if (baja.getFecha().getDayOfMonth() > CUTOFF_DAY) {
            periodo.next();
        }
        return periodo;
    }

    private void ensureChequeraSerieIsSet(Baja baja) {
        if (baja.getChequeraSerie() == null) {
            baja.setChequeraSerie(chequeraSerieService.findByUnique(
                    baja.getFacultadId(), baja.getTipoChequeraId(), baja.getChequeraSerieId()));
        }
    }

    private void processPendingCuotas(Baja baja, Periodo periodo) {
        List<ChequeraCuota> pendingCuotas = chequeraCuotaService.findAllPendientesBaja(
                baja.getFacultadId(), baja.getTipoChequeraId(), baja.getChequeraSerieId(),
                baja.getChequeraSerie().getAlternativaId());

        pendingCuotas.forEach(cuota -> processPendingCuota(cuota, periodo, baja));
    }

    private void processPendingCuota(ChequeraCuota cuota, Periodo periodo, Baja baja) {
        int mes = normalizeMonth(cuota.getMes());
        if (shouldProcessCuota(periodo, cuota.getAnho(), mes)) {
            cuota.setBaja(BAJA_ACTIVE);
            ChequeraCuota updatedCuota = chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
            logObject(updatedCuota, "Cuota");
            processDebitosBaja(updatedCuota, baja);
        }
    }

    private int normalizeMonth(int mes) {
        return mes > MAX_MONTH ? DEFAULT_MONTH : mes;
    }

    private boolean shouldProcessCuota(Periodo periodo, int cuotaAnho, int cuotaMes) {
        return periodo.getAnho() * 100 + periodo.getMes() <= cuotaAnho * 100 + cuotaMes;
    }

    private void processDebitosBaja(ChequeraCuota cuota, Baja baja) {
        List<Debito> debitos = debitoService.findAllAsociados(
                cuota.getFacultadId(), cuota.getTipoChequeraId(), cuota.getChequeraSerieId(),
                cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId());

        debitos.forEach(debito -> {
            debito.setFechaBaja(baja.getFecha());
            Debito updatedDebito = debitoService.update(debito, debito.getDebitoId());
            logObject(updatedDebito, "Debito");
        });
    }

    private <T> void logObject(T object, String objectType) {
        try {
            log.debug("{} -> {}", objectType, jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.debug("{} JSON error -> {}", objectType, e.getMessage());
        }
    }
}
