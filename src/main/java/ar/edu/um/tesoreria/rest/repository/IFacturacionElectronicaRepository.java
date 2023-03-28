package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFacturacionElectronicaRepository extends JpaRepository<FacturacionElectronica, Long> {

    public List<FacturacionElectronica> findAllByChequeraPagoIdIn(List<Long> chequeraPagoIds);

}
