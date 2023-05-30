package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.FacturacionElectronica;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFacturacionElectronicaRepository extends JpaRepository<FacturacionElectronica, Long> {

    public List<FacturacionElectronica> findAllByChequeraPagoIdIn(List<Long> chequeraPagoIds);

    public Optional<FacturacionElectronica> findByFacturacionElectronicaId(Long facturacionElectronicaId);
}
