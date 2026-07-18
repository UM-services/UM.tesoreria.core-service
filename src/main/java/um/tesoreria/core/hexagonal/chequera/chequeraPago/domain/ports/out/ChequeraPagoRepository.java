package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ChequeraPagoRepository {

    List<ChequeraPago> findAllByTipoPagoAndAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion);

    List<ChequeraPago> findAllByTipoPagoAndFechaBetween(Integer tipoPagoId, OffsetDateTime fechaInicio, OffsetDateTime fechaFin);

    List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    List<ChequeraPago> findAllByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                       Integer productoId, Integer alternativaId, Integer cuotaId);

    List<ChequeraPago> findPendientesFactura(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Integer tipoPagoThreshold);

    List<ChequeraPago> findAllByFacultadAndTipoChequeraAndLectivo(Integer facultadId, Integer tipoChequeraId, Integer lectivoId);

    Optional<ChequeraPago> findByChequeraPagoId(Long chequeraPagoId);

    Optional<ChequeraPago> findByIdMercadoPago(String idMercadoPago);

    Optional<ChequeraPago> findTopByCompositeKeyOrderByOrdenDesc(Integer facultadId, Integer tipoChequeraId,
                                                                  Long chequeraSerieId, Integer productoId,
                                                                  Integer alternativaId, Integer cuotaId);

    void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    ChequeraPago save(ChequeraPago chequeraPago);

    List<ChequeraPago> saveAll(List<ChequeraPago> chequeraPagos);

}
