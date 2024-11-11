package um.tesoreria.core.service.facade;

import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraPagoService;
import um.tesoreria.core.service.ChequeraTotalService;
import um.tesoreria.core.service.MercadoPagoContextService;

@Service
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

        final Integer MERCADOPAGO = 18;

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());

        var nextOrder = chequeraPagoService.nextOrden(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId());

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
                .acreditacion(mercadoPagoContext.getFechaPago()) // averiguar fecha de acreditacion
                .importe(mercadoPagoContext.getImportePagado())
                .tipoPagoId(MERCADOPAGO)
                .build();
        chequeraPago = chequeraPagoService.add(chequeraPago, chequeraCuotaService);

        marcarPago(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId(), chequeraCuotaService);

        return chequeraPago;
    }

    public void marcarPago(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {

        boolean pagado = chequeraPagoService.isPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, chequeraCuotaService);
        boolean pagoMarcado = false;

        var chequeraCuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        chequeraCuota.setPagado((byte) (pagado ? 1 : 0));
        chequeraCuota = chequeraCuotaService.update(chequeraCuota, chequeraCuota.getChequeraCuotaId());

        calcularPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    private void calcularPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        var chequeraTotal = chequeraTotalService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId);
        chequeraTotal.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal = chequeraTotalService.update(chequeraTotal, chequeraTotal.getChequeraTotalId());
    }

}
