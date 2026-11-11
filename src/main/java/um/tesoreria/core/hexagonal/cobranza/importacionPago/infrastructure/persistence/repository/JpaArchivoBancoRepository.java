package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ArchivoBancoEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface JpaArchivoBancoRepository extends JpaRepository<ArchivoBancoEntity, Long> {

    List<ArchivoBancoEntity> findAllByArchivo(String archivo);

    List<ArchivoBancoEntity> findAllByArchivoAndPasado(String archivo, Byte pasado);

    List<ArchivoBancoEntity> findAllByTipoPagoIdIn(Collection<Integer> tipoPagoIds);

    List<ArchivoBancoEntity> findAllByCodigoEmpresaAndIdentificacionAndProductoIdAndMesPagoAndAnhoPagoAndCuotaIdAndImporteAndFechaPago(
            String codigoEmpresa, Integer identificacion, Integer productoId, Integer mesPago, Integer anhoPago,
            Integer cuotaId, BigDecimal importe, OffsetDateTime fechaPago);

    void deleteAllByArchivo(String archivo);

}
