package um.tesoreria.core.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraPagoService;
import um.tesoreria.core.service.ChequeraTotalService;
import um.tesoreria.core.service.MercadoPagoContextService;

@Service
@Slf4j
public class PagoService {

    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;
    private final MercadoPagoContextService mercadoPagoContextService;

    public PagoService(ChequeraPagoService chequeraPagoService,
                       ChequeraCuotaService chequeraCuotaService,
                       ChequeraTotalService chequeraTotalService, MercadoPagoContextService mercadoPagoContextService) {
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.chequeraTotalService = chequeraTotalService;
        this.mercadoPagoContextService = mercadoPagoContextService;
    }

    public ChequeraPago registraPagoMP(Long mercadoPagoContextId) {
        log.debug("Processing registraPagoMP");

        final Integer MERCADOPAGO = 18;

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
        logMercadoPagoContext(mercadoPagoContext);

        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());
        logChequeraCuota(chequeraCuota);

        var nextOrder = chequeraPagoService.nextOrden(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId());
        log.debug("NextOrder = {}", nextOrder);

        var chequeraPago = new ChequeraPago.Builder()
                .chequeraCuotaId(chequeraCuota.getChequeraCuotaId())
                .facultadId(chequeraCuota.getFacultadId())
                .tipoChequeraId(chequeraCuota.getTipoChequeraId())
                .chequeraSerieId(chequeraCuota.getChequeraSerieId())
                .productoId(chequeraCuota.getProductoId())
                .alternativaId(chequeraCuota.getAlternativaId())
                .cuotaId(chequeraCuota.getCuotaId())
                .orden(nextOrder)
                .mes(chequeraCuota.getMes())
                .anho(chequeraCuota.getAnho())
                .fecha(mercadoPagoContext.getFechaPago())
                .acreditacion(mercadoPagoContext.getFechaAcreditacion())
                .importe(mercadoPagoContext.getImportePagado())
                .tipoPagoId(MERCADOPAGO)
                .archivo("MercadoPago")
                .build();
        logChequeraPago(chequeraPago);
        chequeraPago = chequeraPagoService.add(chequeraPago, chequeraCuotaService);
        log.debug("After");
        logChequeraPago(chequeraPago);

        marcarPago(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId(), chequeraCuotaService);

        return chequeraPago;
    }

    public void marcarPago(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {
        log.debug("Processing marcarPago");

        boolean pagado = chequeraPagoService.isPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, chequeraCuotaService);
        boolean pagoMarcado = false;

        var chequeraCuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        chequeraCuota.setPagado((byte) (pagado ? 1 : 0));
        chequeraCuota = chequeraCuotaService.update(chequeraCuota, chequeraCuota.getChequeraCuotaId());

        calcularPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    private void calcularPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        log.debug("Processing calcularPagado");
        var chequeraTotal = chequeraTotalService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId);
        chequeraTotal.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal = chequeraTotalService.update(chequeraTotal, chequeraTotal.getChequeraTotalId());
    }

    private void logMercadoPagoContext(MercadoPagoContext mercadoPagoContext) {
        log.debug("Processing logMercadoPagoContext");
        try {
            log.debug("MercadoPagoContext -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mercadoPagoContext));
        } catch (JsonProcessingException e) {
            log.error("MercadoPagoContext error {}", e.getMessage());
        }
    }

    private void logChequeraCuota(ChequeraCuota chequeraCuota) {
        log.debug("Processing logChequeraCuota");
        try {
            log.debug("ChequeraCuota -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraCuota));
        } catch (JsonProcessingException e) {
            log.error("ChequeraCuota error {}", e.getMessage());
        }
    }

    private void logChequeraPago(ChequeraPago chequeraPago) {
        log.debug("Processing logChequeraPago");
        try {
            log.debug("ChequeraPago -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraPago));
        } catch (JsonProcessingException e) {
            log.error("ChequeraPago error {}", e.getMessage());
        }
    }

}
