package um.tesoreria.rest.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.FacturacionElectronica;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IFacturacionElectronicaRepository extends JpaRepository<FacturacionElectronica, Long> {

    public List<FacturacionElectronica> findAllByChequeraPagoIdIn(List<Long> chequeraPagoIds);

    public List<FacturacionElectronica> findAllByFechaReciboBetween(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Sort sort);

    public Optional<FacturacionElectronica> findByFacturacionElectronicaId(Long facturacionElectronicaId);

}
