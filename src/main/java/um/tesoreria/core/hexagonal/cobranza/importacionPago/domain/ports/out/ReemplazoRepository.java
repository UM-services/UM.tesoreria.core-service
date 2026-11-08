package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoCuota;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoSerie;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a las tablas de reemplazo de chequera. Se mantiene como puerto propio del slice de
 * cobranza para no acoplar la importación a los servicios legacy de reemplazo.
 */
public interface ReemplazoRepository {

    Optional<ReemplazoSerie> findSerieByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    Optional<ReemplazoCuota> findCuotaByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                               Integer productoId, Integer alternativaId, Integer cuotaId);

    boolean existsPagoByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                              Integer productoId, Integer alternativaId, Integer cuotaId);

    int nextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                  Integer productoId, Integer alternativaId, Integer cuotaId);

    ReemplazoPago savePago(ReemplazoPago pago);

    List<ReemplazoPago> findAllPagosByArchivo(String archivo);

    List<ReemplazoPago> findAllPagosByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                            Integer productoId, Integer alternativaId, Integer cuotaId);

    List<ReemplazoCuota> findCuotasByPeriodo(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                             Integer productoId, Integer cuotaId, Integer mes, Integer anho);

    void deletePagosByArchivo(String archivo);

    ReemplazoCuota saveCuota(ReemplazoCuota cuota);

    void recalculateTotal(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
                          Integer alternativaId);

}
