package um.tesoreria.core.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturacionElectronicaRepository extends JpaRepository<FacturacionElectronica, Long> {

    List<FacturacionElectronica> findAllByChequeraPagoIdIn(List<Long> chequeraPagoIds);

    List<FacturacionElectronica> findAllByFechaReciboBetween(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Sort sort);

    List<FacturacionElectronica> findTop10ByEnviadaAndRetriesLessThanOrderByFacturacionElectronicaIdDesc(Byte enviada, Integer retries);

    Optional<FacturacionElectronica> findByFacturacionElectronicaId(Long facturacionElectronicaId);

    Optional<FacturacionElectronica> findByChequeraPagoId(Long chequeraPagoId);

}
