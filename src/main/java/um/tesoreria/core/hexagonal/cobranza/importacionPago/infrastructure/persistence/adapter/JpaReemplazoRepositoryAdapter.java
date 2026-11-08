package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoCuota;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoSerie;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ReemplazoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ReemplazoTotalEntity;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository.JpaReemplazoTotalRepository;
import um.tesoreria.core.kotlin.model.ChequeraCuotaReemplazo;
import um.tesoreria.core.kotlin.model.ChequeraSerieReemplazo;
import um.tesoreria.core.kotlin.repository.ChequeraCuotaReemplazoRepository;
import um.tesoreria.core.kotlin.repository.ChequeraSerieReemplazoRepository;
import um.tesoreria.core.model.ChequeraPagoReemplazo;
import um.tesoreria.core.repository.ChequeraPagoReemplazoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Acceso a las tablas de reemplazo.
 *
 * <p>Las tablas {@code chequera_serie_reemplazo}, {@code chequera_cuota_reemplazo} y
 * {@code chequera_pago_reemplazo} ya tienen sus entidades JPA en los paquetes legacy/Kotlin. Se
 * reutilizan esas entidades desde este adaptador (capa de infraestructura) para no declarar un
 * segundo mapeo de la misma tabla, que Hibernate rechaza al exportar el esquema. El dominio y la
 * aplicación del slice siguen sin depender de legacy.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaReemplazoRepositoryAdapter implements ReemplazoRepository {

    private final ChequeraSerieReemplazoRepository serieRepository;
    private final ChequeraCuotaReemplazoRepository cuotaRepository;
    private final ChequeraPagoReemplazoRepository pagoRepository;
    private final JpaReemplazoTotalRepository totalRepository;

    @Override
    public Optional<ReemplazoSerie> findSerieByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        var encontrada = serieRepository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
        if (encontrada == null || encontrada.isEmpty()) {
            return Optional.empty();
        }
        return encontrada.map(this::toDomain);
    }

    @Override
    public Optional<ReemplazoCuota> findCuotaByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                      Integer productoId, Integer alternativaId, Integer cuotaId) {
        return cuotaRepository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .map(this::toDomain);
    }

    @Override
    public boolean existsPagoByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                     Integer productoId, Integer alternativaId, Integer cuotaId) {
        return !pagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .isEmpty();
    }

    @Override
    public int nextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                         Integer productoId, Integer alternativaId, Integer cuotaId) {
        return pagoRepository
                .findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .map(pago -> pago.getOrden() + 1)
                .orElse(0);
    }

    @Override
    public ReemplazoPago savePago(ReemplazoPago pago) {
        ChequeraPagoReemplazo entity = ChequeraPagoReemplazo.builder()
                .chequeraPagoReemplazoId(pago.getClave())
                .facultadId(pago.getFacultadId())
                .tipoChequeraId(pago.getTipoChequeraId())
                .chequeraSerieId(pago.getChequeraSerieId())
                .productoId(pago.getProductoId())
                .alternativaId(pago.getAlternativaId())
                .cuotaId(pago.getCuotaId())
                .orden(pago.getOrden())
                .mes(pago.getMes())
                .anho(pago.getAnho())
                .fecha(pago.getFecha())
                .acreditacion(pago.getAcreditacion())
                .importe(pago.getImporte())
                .path(pago.getPath())
                .archivo(pago.getArchivo())
                .observaciones(pago.getObservaciones())
                .archivoBancoId(pago.getArchivoBancoId())
                .archivoBancoIdAcreditacion(pago.getArchivoBancoIdAcreditacion())
                .verificador(pago.getVerificador())
                .tipoPagoId(pago.getTipoPagoId())
                .build();
        return toDomain(pagoRepository.save(entity));
    }

    @Override
    public List<ReemplazoPago> findAllPagosByArchivo(String archivo) {
        return pagoRepository.findAllByArchivo(archivo).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ReemplazoPago> findAllPagosByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                   Integer productoId, Integer alternativaId, Integer cuotaId) {
        return pagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ReemplazoCuota> findCuotasByPeriodo(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                    Integer productoId, Integer cuotaId, Integer mes, Integer anho) {
        return cuotaRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndCuotaIdAndMesAndAnho(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, cuotaId, mes, anho)
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deletePagosByArchivo(String archivo) {
        pagoRepository.deleteAllByArchivo(archivo);
    }

    @Override
    public ReemplazoCuota saveCuota(ReemplazoCuota cuota) {
        ChequeraCuotaReemplazo entity = cuotaRepository.findById(cuota.getClave())
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuota de reemplazo " + cuota.getClave()));
        entity.setPagado(cuota.getPagado() == null ? (byte) 0 : cuota.getPagado());
        return toDomain(cuotaRepository.save(entity));
    }

    @Override
    public void recalculateTotal(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
                                 Integer alternativaId) {
        List<ChequeraCuotaReemplazo> cuotas = cuotaRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
                        facultadId, tipoChequeraId, chequeraSerieId, alternativaId)
                .stream()
                .filter(Objects::nonNull)
                .toList();
        BigDecimal total = cuotas.stream()
                .filter(cuota -> cuota.getBaja() == 0)
                .map(cuota -> cuota.getImporte1() == null ? BigDecimal.ZERO : cuota.getImporte1())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pagado = cuotas.stream()
                .filter(cuota -> cuota.getPagado() == 1)
                .map(cuota -> cuota.getImporte1() == null ? BigDecimal.ZERO : cuota.getImporte1())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRepository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId)
                .ifPresent(entity -> {
                    entity.setTotal(total);
                    entity.setPagado(pagado);
                    totalRepository.save(entity);
                });
    }

    private ReemplazoSerie toDomain(ChequeraSerieReemplazo entity) {
        return ReemplazoSerie.builder()
                .chequeraReemplazoId(entity.getChequeraReemplazoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .alternativaId(entity.getAlternativaId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .lectivoId(entity.getLectivoId())
                .geograficaId(entity.getGeograficaId())
                .build();
    }

    private ReemplazoCuota toDomain(ChequeraCuotaReemplazo entity) {
        return ReemplazoCuota.builder()
                .clave(entity.getChequeraCuotaReemplazoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .mes(entity.getMes())
                .anho(entity.getAnho())
                .vencimiento1(entity.getVencimiento1())
                .importe1(entity.getImporte1())
                .importe2(entity.getImporte2())
                .importe3(entity.getImporte3())
                .pagado(entity.getPagado())
                .baja(entity.getBaja())
                .build();
    }

    private ReemplazoPago toDomain(ChequeraPagoReemplazo entity) {
        return ReemplazoPago.builder()
                .clave(entity.getChequeraPagoReemplazoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .orden(entity.getOrden())
                .mes(entity.getMes())
                .anho(entity.getAnho())
                .fecha(entity.getFecha())
                .acreditacion(entity.getAcreditacion())
                .importe(entity.getImporte())
                .path(entity.getPath())
                .archivo(entity.getArchivo())
                .observaciones(entity.getObservaciones())
                .archivoBancoId(entity.getArchivoBancoId())
                .archivoBancoIdAcreditacion(entity.getArchivoBancoIdAcreditacion())
                .verificador(entity.getVerificador())
                .tipoPagoId(entity.getTipoPagoId())
                .build();
    }

}
