package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface ArchivoBancoRepository {

    ArchivoBanco save(ArchivoBanco archivoBanco);

    List<ArchivoBanco> saveAll(List<ArchivoBanco> filas);

    List<ArchivoBanco> findAllByArchivo(String archivo);

    List<ArchivoBanco> findAllByArchivoAndPasado(String archivo, Byte pasado);

    void deleteByArchivo(String archivo);

    List<String> findDistinctArchivoByTipoPagoIdIn(Collection<Integer> tipoPagoIds);

    boolean existsByPago(String codigoEmpresa, Integer identificacion, Integer productoId, Integer mesPago,
                         Integer anhoPago, Integer cuotaId, BigDecimal importe, OffsetDateTime fechaPago,
                         String marcaAcreditacion);

    boolean existsByPagoConVerificador(String codigoEmpresa, Integer identificacion, Integer productoId,
                                       Integer mesPago, Integer anhoPago, Integer cuotaId, BigDecimal importe,
                                       OffsetDateTime fechaPago, String verificador);

}
